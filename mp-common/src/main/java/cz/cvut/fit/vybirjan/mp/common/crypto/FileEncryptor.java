package cz.cvut.fit.vybirjan.mp.common.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import cz.cvut.fit.vybirjan.mp.common.Utils;

public final class FileEncryptor {

	public static final String CLASS_SUFFIX = ".class";
	public static final String DEFAULT_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
	public static final int DEFAULT_IV_SIZE = 16;

	public static final byte HEAD = (byte) 0xEC;

	private static final int BUFFER_SIZE = 1024;

	public interface ProcessStrategy<E> {

		void process(E element, InputStream source, OutputStream target) throws IOException;

	}

	public static final ProcessStrategy<Object> createDefaultEncryptStrategy(Key key) {
		try {
			return new InitVectorEncryptStrategy(Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM), key, DEFAULT_IV_SIZE);
		} catch (GeneralSecurityException e) {
			throw new AssertionError(e);
		}
	}

	public static final ProcessStrategy<Object> createDefaultDecryptStrategy(Key key) {
		try {
			return new InitVectorDecryptStrategy(Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM), key, DEFAULT_IV_SIZE);
		} catch (GeneralSecurityException e) {
			throw new AssertionError(e);
		}
	}

	public static final class TaggindEncryptionStrategy<T> implements ProcessStrategy<T> {

		public TaggindEncryptionStrategy(int tag, ProcessStrategy<T> next) {
			this.tag = tag;
			this.next = next;
		}

		private final int tag;
		private final ProcessStrategy<T> next;

		@Override
		public void process(T element, InputStream source, OutputStream target) throws IOException {
			target.write(HEAD);
			target.write(Utils.toByteArray(tag));
			next.process(element, source, target);
		}
	}

	public static final class JarEntryClassProcessingStrategy implements ProcessStrategy<JarEntry> {

		public JarEntryClassProcessingStrategy(ProcessStrategy<? super JarEntry> delegate) {
			this.delegate = delegate;
		}

		private final ProcessStrategy<? super JarEntry> delegate;

		@Override
		public void process(JarEntry element, InputStream source, OutputStream target) throws IOException {
			if (element.getName().toLowerCase().endsWith(CLASS_SUFFIX)) {
				delegate.process(element, source, target);
			} else {
				copyData(source, target);
			}
		}

	}

	public static final class InitVectorEncryptStrategy implements ProcessStrategy<Object> {

		public InitVectorEncryptStrategy(Cipher c, Key key, int initVectorSize) {
			this.c = c;
			this.key = key;
			this.initVectorSize = initVectorSize;
		}

		private final Cipher c;
		private final Key key;
		private final int initVectorSize;

		@Override
		public void process(Object element, InputStream source, OutputStream target) throws IOException {
			SecureRandom rnd = new SecureRandom();
			byte[] iv = new byte[initVectorSize];
			rnd.nextBytes(iv);

			target.write(iv);

			try {
				c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
				byte[] buffer = new byte[BUFFER_SIZE];
				int read = 0;

				while ((read = source.read(buffer)) != -1) {
					target.write(c.update(buffer, 0, read));
				}

				target.write(c.doFinal());

			} catch (GeneralSecurityException e) {
				throw new IOException("Encryption error", e);
			}
		}
	}

	public static final class InitVectorDecryptStrategy implements ProcessStrategy<Object> {

		public InitVectorDecryptStrategy(Cipher c, Key key, int initVectorSize) {
			this.c = c;
			this.key = key;
			this.initVectorSize = initVectorSize;
		}

		private final Cipher c;
		private final Key key;
		private final int initVectorSize;

		@Override
		public void process(Object element, InputStream source, OutputStream target) throws IOException {

			byte[] iv = new byte[initVectorSize];
			fillBuffer(iv, source);

			try {
				c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
				byte[] buffer = new byte[BUFFER_SIZE];
				int read = 0;

				while ((read = source.read(buffer)) != -1) {
					target.write(c.update(buffer, 0, read));
				}

				target.write(c.doFinal());

			} catch (GeneralSecurityException e) {
				throw new IOException("Encryption error", e);
			}
		}
	}

	private static void fillBuffer(byte[] b, InputStream in) throws IOException {
		int read = 0;
		while (read < b.length) {
			read = in.read(b, read, b.length - read);
		}
	}

	private FileEncryptor() {
		throw new AssertionError("Class not intended for instantiation");
	}

	public static void processJarFile(JarFile toProcess, File target, ProcessStrategy<? super JarEntry> strategy) throws IOException {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(target));
		try {
			Enumeration<JarEntry> entries = toProcess.entries();
			JarEntry entry = null;
			while (entries.hasMoreElements()) {
				entry = entries.nextElement();

				ZipEntry newEntry = new ZipEntry(entry.getName());
				out.putNextEntry(newEntry);

				InputStream in = toProcess.getInputStream(entry);
				try {
					strategy.process(entry, in, out);
				} finally {
					in.close();
				}
			}
		} finally {
			out.close();

		}
	}

	public static void encryptJarFile(JarFile source, File target, Key key, int tag) throws IOException {
		processJarFile(source, target, new JarEntryClassProcessingStrategy(new TaggindEncryptionStrategy(tag, createDefaultEncryptStrategy(key))));
	}

	public static void encryptJarFile(JarFile source, File target, Key key) throws IOException {
		processJarFile(source, target, new JarEntryClassProcessingStrategy(createDefaultEncryptStrategy(key)));
	}

	public static void decryptJarFile(JarFile source, File target, Key key) throws IOException {
		processJarFile(source, target, new JarEntryClassProcessingStrategy(createDefaultDecryptStrategy(key)));
	}

	public static void encryptFile(File source, File target, Key key) throws IOException {
		processFile(source, target, createDefaultEncryptStrategy(key));
	}

	public static void decryptFile(File source, File target, Key key) throws IOException {
		processFile(source, target, createDefaultDecryptStrategy(key));
	}

	public static void processFile(File source, File target, ProcessStrategy<? super File> strategy) throws IOException {
		FileInputStream in = new FileInputStream(source);
		try {
			if (!target.exists()) {
				if (!target.createNewFile()) {
					throw new IOException("Failed to create file " + target.getAbsolutePath());
				}
			}

			FileOutputStream out = new FileOutputStream(target);
			try {
				strategy.process(source, in, out);
			} finally {
				out.close();
			}
		} finally {
			in.close();
		}
	}

	public static void copyData(InputStream source, OutputStream target) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		int read = 0;

		while ((read = source.read(buffer)) != -1) {
			target.write(buffer, 0, read);
		}

	}
}
