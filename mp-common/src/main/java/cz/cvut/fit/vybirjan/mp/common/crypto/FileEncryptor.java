package cz.cvut.fit.vybirjan.mp.common.crypto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import cz.cvut.fit.vybirjan.mp.common.Utils;

/**
 * Class with helper methods to encrypt and decrypt files.
 * 
 * @author Jan Vybíral
 * 
 */
public final class FileEncryptor {

	public static final String CLASS_SUFFIX = ".class";
	public static final String DEFAULT_CIPHER = "AES";
	public static final String DEFAULT_CIPHER_ALGORITHM = DEFAULT_CIPHER + "/CBC/PKCS5Padding";
	public static final int DEFAULT_IV_SIZE = 16;

	public static final byte HEAD = (byte) 0xEC;

	private static final int BUFFER_SIZE = 1024;

	/**
	 * Basic interface for processing elements between sources.
	 * 
	 * @author Jan Vybíral
	 * 
	 * @param <E>
	 */
	public interface ProcessStrategy<E> {

		/**
		 * Method which should process given element
		 */
		void process(E element, InputStream source, OutputStream target) throws IOException;

	}

	/**
	 * Creates default processing strategy which encrypts data using specified
	 * key.
	 * 
	 * @param key
	 *            Key to encrypt data
	 * @return
	 */
	public static final ProcessStrategy<Object> createDefaultEncryptStrategy(Key key) {
		try {
			return new InitVectorEncryptStrategy(Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM), key, DEFAULT_IV_SIZE);
		} catch (GeneralSecurityException e) {
			throw new AssertionError(e);
		}
	}

	/**
	 * Creates default strategy which decrpts data using specified key.
	 * 
	 * @param key
	 * @return
	 */
	public static final ProcessStrategy<Object> createDefaultDecryptStrategy(Key key) {
		try {
			return new InitVectorDecryptStrategy(Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM), key, DEFAULT_IV_SIZE);
		} catch (GeneralSecurityException e) {
			throw new AssertionError(e);
		}
	}

	/**
	 * Wrapper for encryption strategy which prepends given tag in front of
	 * data.
	 * 
	 * @author Jan Vybíral
	 * 
	 * @param <T>
	 */
	public static final class TaggindEncryptionStrategy<T> implements ProcessStrategy<T> {

		/**
		 * Creates wrapper
		 * 
		 * @param tag
		 *            Tag to be prepend to data
		 * @param next
		 *            Strategy delegate used to process data
		 */
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

	/**
	 * Wrapper class used to process .class files. Class files are delegated to
	 * wrapped strategy to process, other files are just copied without
	 * modification
	 * 
	 * @author Jan Vybíral
	 * 
	 */
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

	/**
	 * Strategy used for encryption. Encrypts data using provided symmetric
	 * cipher with init vector. Saves init vector used for encryption at the
	 * beginning of processed data.
	 * 
	 * @author Jan Vybíral
	 * 
	 */
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

	/**
	 * Class used to decrypt data encrypted using
	 * {@link InitVectorEncryptStrategy}. Reads init vector from first n bytes
	 * and uses it in decryption.
	 * 
	 * @author Jan Vybíral
	 * 
	 */
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

	/**
	 * Helper method for processing JAR files. JAR file is processed by entries.
	 * 
	 * @param toProcess
	 *            JAr file to process
	 * @param target
	 *            Output file to where write processed data
	 * @param strategy
	 *            Strategy used to process jar file
	 * @throws IOException
	 */
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

	/**
	 * Helper method used to encrypt content of jar file. Only .class files are
	 * encrypted.
	 * 
	 * @param source
	 *            jar file to encrypt
	 * @param target
	 *            File where to write encrypter jar file
	 * @param key
	 *            Key used for encryption
	 * @param tag
	 *            Tag added at the beginning of each encrypted class used to
	 *            identify key used in encryption.
	 * @throws IOException
	 */
	public static void encryptJarFile(JarFile source, File target, Key key, int tag) throws IOException {
		processJarFile(source, target, new JarEntryClassProcessingStrategy(new TaggindEncryptionStrategy(tag, createDefaultEncryptStrategy(key))));
	}

	/**
	 * Helper method used to encrypt content of jar file. Only .class files are
	 * encrypted.
	 * 
	 * @param source
	 *            jar file to encrypt
	 * @param target
	 *            File where to write encrypter jar file
	 * @param key
	 *            Key used for encryption
	 * @param tag
	 *            Tag added at the beginning of each encrypted class used to
	 *            identify key used in encryption.
	 * @throws IOException
	 */
	public static void encryptJarFile(JarFile source, File target, Key key) throws IOException {
		processJarFile(source, target, new JarEntryClassProcessingStrategy(createDefaultEncryptStrategy(key)));
	}

	/**
	 * Helper method used to decrypt content of jar file. Only .class files are
	 * encrypted.
	 * 
	 * @param source
	 *            jar file to decrypt
	 * @param target
	 *            File where to write decrypted jar file
	 * @param key
	 *            Key used for decrpytion
	 * @throws IOException
	 */
	public static void decryptJarFile(JarFile source, File target, Key key) throws IOException {
		processJarFile(source, target, new JarEntryClassProcessingStrategy(createDefaultDecryptStrategy(key)));
	}

	/**
	 * Helper method to encrypt whole file using symmetric cipher.
	 * 
	 * @param source
	 *            File to encrypt
	 * @param target
	 *            file where to write encrypted source
	 * @param key
	 *            Key used for encryption
	 * @throws IOException
	 */
	public static void encryptFile(File source, File target, Key key) throws IOException {
		processFile(source, target, createDefaultEncryptStrategy(key));
	}

	/**
	 * Helper method used to decrypt content of file.
	 * 
	 * @param source
	 *            file to decrypt
	 * @param target
	 *            File where to write decrypted file
	 * @param key
	 *            Key used for decrpytion
	 * @throws IOException
	 */
	public static void decryptFile(File source, File target, Key key) throws IOException {
		processFile(source, target, createDefaultDecryptStrategy(key));
	}

	/**
	 * Helper method which processes file using provided strategy
	 * 
	 * @param source
	 *            Source file
	 * @param target
	 *            Target file
	 * @param strategy
	 *            Strategy used during processing
	 * @throws IOException
	 */
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

	/**
	 * Helper method which copies data from one stram to another using small
	 * buffer.
	 * 
	 * @param source
	 *            Source stream
	 * @param target
	 *            Target stream
	 * @throws IOException
	 */
	public static void copyData(InputStream source, OutputStream target) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		int read = 0;

		while ((read = source.read(buffer)) != -1) {
			target.write(buffer, 0, read);
		}

	}

	/**
	 * Method to serialize tagged key into byte array.
	 * 
	 * @param key
	 *            Key to serialize
	 * @return
	 */
	public static byte[] serializeKey(TaggedKey key) {
		byte[] tag = Utils.toByteArray(key.getTag());
		byte[] alg = Utils.toUtf8(key.getAlgorithm());
		byte[] value = key.getEncoded();

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream(tag.length + alg.length + value.length + 4);
			out.write(tag);
			out.write(Utils.toByteArray(alg.length));
			out.write(alg);
			out.write(value);
			return out.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException("Failed to serialize key to byte array");
		}
	}

	/**
	 * Method to deserialize tagged key from byte array previously serialized by
	 * {@link #serializeKey(TaggedKey)} method.
	 * 
	 * @param key
	 *            Key to serialize
	 * @return
	 */
	public static TaggedKey deserializeKey(byte[] data) {
		int tag = Utils.toInt(data, 0);
		int algLen = Utils.toInt(data, 4);
		String alg = Utils.fromUtf8(data, 8, algLen);
		int dataOffset = 4 + 4 + algLen;
		byte[] keyData = Arrays.copyOfRange(data, dataOffset, data.length);

		return new TaggedKeyImpl(tag, new SecretKeySpec(keyData, alg));
	}

	/**
	 * Generates new key with given tag.
	 * 
	 * @param tag
	 * @return
	 */
	public static TaggedKey generateDefaultKey(int tag) {
		try {
			KeyGenerator keygen = KeyGenerator.getInstance(DEFAULT_CIPHER);
			return new TaggedKeyImpl(tag, keygen.generateKey());
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError("Default cipher algorithm " + DEFAULT_CIPHER_ALGORITHM + " not available");
		}
	}
}
