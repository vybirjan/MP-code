package cz.cvut.fit.vybirjan.mp.common.crypto;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
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

	public static final Name EXCLUDED_CLASSES_MANIFEST_PROP = new Name("Encryption-Exclude");

	public static final String CLASS_SUFFIX = ".class";
	public static final String DEFAULT_CIPHER = "AES";
	public static final String DEFAULT_CIPHER_ALGORITHM = DEFAULT_CIPHER + "/CBC/PKCS5Padding";
	public static final int DEFAULT_IV_SIZE = 16;

	public static final byte HEAD = (byte) 0xEC;

	private static final int BUFFER_SIZE = 1024 * 1024 * 6; // 6MB

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
	public static final class TaggingEncryptionStrategy<T> implements ProcessStrategy<T> {

		/**
		 * Creates wrapper
		 * 
		 * @param tag
		 *            Tag to be prepend to data
		 * @param next
		 *            Strategy delegate used to process data
		 */
		public TaggingEncryptionStrategy(int tag, ProcessStrategy<T> next) {
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

	private static List<String> parseExcludedPackages(Manifest m) {
		if (m == null) {
			return Collections.emptyList();
		}

		String attribs = (String) m.getMainAttributes().get(EXCLUDED_CLASSES_MANIFEST_PROP);

		if (attribs == null) {
			return Collections.emptyList();
		}

		String[] attribArr = attribs.split(",");
		List<String> ret = new ArrayList<String>(attribArr.length);

		for (String s : attribArr) {
			ret.add(s.trim());
		}

		return ret;
	}

	public static final class TaggingDecryptionStrategy<T> implements ProcessStrategy<T> {

		public TaggingDecryptionStrategy(int tag, ProcessStrategy<T> next) {
			this.tag = tag;
			this.next = next;
		}

		private final int tag;
		private final ProcessStrategy<T> next;

		@Override
		public void process(T element, InputStream source, OutputStream target) throws IOException {
			int head = (byte) source.read();
			if (head != HEAD) {
				throw new IllegalArgumentException("Element " + element + " does not have valid head - expected " + HEAD + ", read " + head);
			}
			byte[] tagData = INT_BUFFER.get();
			fillBuffer(tagData, source);
			int readTag = Utils.toInt(tagData, 0);
			if (readTag != tag) {
				throw new IllegalArgumentException("Invalid tag: " + readTag);
			} else {
				next.process(element, source, target);
			}
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
	public static final class ZipEntryClassProcessingStrategy implements ProcessStrategy<ZipEntry> {

		public ZipEntryClassProcessingStrategy(Manifest manifest, ProcessStrategy<? super ZipEntry> delegate) {
			this.delegate = delegate;
			this.excludedPatterns = parseExcludedPackages(manifest);
		}

		public ZipEntryClassProcessingStrategy(ProcessStrategy<? super ZipEntry> delegate) {
			this.delegate = delegate;
			this.excludedPatterns = Collections.emptyList();
		}

		private final ProcessStrategy<? super ZipEntry> delegate;
		private final List<String> excludedPatterns;

		@Override
		public void process(ZipEntry element, InputStream source, OutputStream target) throws IOException {
			if (element.getName().toLowerCase().endsWith(CLASS_SUFFIX) && !matchesAny(element, excludedPatterns)) {
				delegate.process(element, source, target);
			} else {
				copyData(source, target);
			}
		}

	}

	private static boolean matchesAny(ZipEntry entry, Iterable<String> patterns) {
		for (String str : patterns) {
			if (Utils.matchesPackagePattern(str, entry)) {
				return true;
			}
		}

		return false;
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
			this.iv = new byte[initVectorSize];
		}

		private final Cipher c;
		private final Key key;
		private final byte[] iv;

		@Override
		public void process(Object element, InputStream source, OutputStream target) throws IOException {
			SecureRandom rnd = new SecureRandom();
			rnd.nextBytes(iv);

			target.write(iv);

			try {
				c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
				byte[] buffer = COPY_BUFFER.get();
				int read = 0;

				while ((read = source.read(buffer)) != -1) {
					target.write(c.update(buffer, 0, read));
				}

				target.write(c.doFinal());

			} catch (GeneralSecurityException e) {
				throw new IOException("Encryption error: " + e.getMessage(), e);
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
			this.iv = new byte[initVectorSize];
		}

		private final Cipher c;
		private final Key key;
		private final byte[] iv;

		@Override
		public void process(Object element, InputStream source, OutputStream target) throws IOException {

			fillBuffer(iv, source);

			try {
				c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

				byte[] buffer = COPY_BUFFER.get();
				int read = 0;

				while ((read = source.read(buffer)) != -1) {
					target.write(c.update(buffer, 0, read));
				}

				target.write(c.doFinal());

			} catch (GeneralSecurityException e) {
				throw new IOException("Encryption error: " + e.getMessage(), e);
			}
		}

	}

	private static void fillBuffer(byte[] b, InputStream in) throws IOException {
		int readSoFar = 0;
		int read = 0;
		while (readSoFar < b.length) {
			read = in.read(b, readSoFar, b.length - readSoFar);

			if (read == -1) {
				throw new EOFException("Reached end of stream");
			} else {
				readSoFar += read;
			}
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

				ZipEntry newEntry = copyEntry(entry);
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

	private static ZipEntry copyEntry(ZipEntry original) {
		ZipEntry ret = new ZipEntry(original.getName());
		ret.setTime(original.getTime());
		ret.setExtra(original.getExtra());
		ret.setComment(original.getComment());

		return ret;
	}

	/**
	 * Processes zip file in memory
	 * 
	 * @param in
	 *            Input stream to read jar entries from
	 * @param processedJarFile
	 *            Buffer to place processed jar file to
	 * @param strategy
	 *            Strategy to process jar files with
	 * @return Number of bytes written to provided buffer
	 * @throws IOException
	 */
	public static int processZipInMemory(ZipInputStream in, byte[] processedJarFile, ProcessStrategy<? super ZipEntry> strategy)
			throws IOException {
		FixedByteArrayOutputStream out = new FixedByteArrayOutputStream(processedJarFile);
		ZipOutputStream zipOut = new ZipOutputStream(out);
		try {

			ZipEntry entry = null;
			while ((entry = in.getNextEntry()) != null) {
				ZipEntry e = copyEntry(entry);
				zipOut.putNextEntry(e);

				strategy.process(entry, in, zipOut);

				zipOut.closeEntry();
			}
		} finally {
			zipOut.close();
		}

		return out.getBytesWritten();
	}

	public static int encryptJarFileInMemory(ZipInputStream in, Manifest mf, byte[] outputBuffer, TaggedKey key) throws IOException {
		return processZipInMemory(in, outputBuffer, new ZipEntryClassProcessingStrategy(mf, new TaggingEncryptionStrategy(key.getTag(),
				createDefaultEncryptStrategy(key))));
	}

	public static int decryptJarFileInMemory(ZipInputStream in, Manifest mf, byte[] outputBuffer, TaggedKey key) throws IOException {
		return processZipInMemory(in, outputBuffer, new ZipEntryClassProcessingStrategy(mf, new TaggingDecryptionStrategy(key.getTag(),
				createDefaultDecryptStrategy(key))));
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
		processJarFile(source, target, new ZipEntryClassProcessingStrategy(source.getManifest(), new TaggingEncryptionStrategy(tag,
				createDefaultEncryptStrategy(key))));
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
		processJarFile(source, target, new ZipEntryClassProcessingStrategy(source.getManifest(), createDefaultEncryptStrategy(key)));
	}

	public static void encryptJarFile(JarFile source, File target, TaggedKey key) throws IOException {
		encryptJarFile(source, target, key, key.getTag());
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
		processJarFile(source, target, new ZipEntryClassProcessingStrategy(source.getManifest(), createDefaultDecryptStrategy(key)));
	}

	public static void decryptJarFile(JarFile source, File target, TaggedKey key) throws IOException {
		processJarFile(source, target, new ZipEntryClassProcessingStrategy(source.getManifest(), new TaggingDecryptionStrategy<Object>(key.getTag(),
				createDefaultDecryptStrategy(key))));
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

	/*
	 * Buffer in thread local to prevent allocation new buffers each time
	 * copyData method is called
	 */
	private static ThreadLocal<byte[]> COPY_BUFFER = new ThreadLocal<byte[]>() {
		@Override
		protected byte[] initialValue() {
			return new byte[BUFFER_SIZE];
		};
	};
	/*
	 * Buffer to prevent allocation of new buffers for int conversions
	 */
	private static ThreadLocal<byte[]> INT_BUFFER = new ThreadLocal<byte[]>() {
		@Override
		protected byte[] initialValue() {
			return new byte[4];
		};
	};

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
		byte[] buffer = COPY_BUFFER.get();
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
	 * Method to deserialize tagged key from byte array previously serialized by
	 * {@link #serializeKey(TaggedKey)} method.
	 * 
	 * @param key
	 *            Key to serialize
	 * @param maxlength
	 *            maximal number of bytes to
	 * @return
	 */
	public static TaggedKey deserializeKey(byte[] data, int maxlength) {
		int tag = Utils.toInt(data, 0);
		int algLen = Utils.toInt(data, 4);
		String alg = Utils.fromUtf8(data, 8, algLen);
		int dataOffset = 4 + 4 + algLen;
		byte[] keyData = Arrays.copyOfRange(data, dataOffset, maxlength);

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

	/**
	 * <p>
	 * Stream which writes data into provided byte array.
	 * </p>
	 * 
	 * <p>
	 * Unlike {@link ByteArrayOutputStream} does not allocate new byte array but
	 * rather uses the one provided with the risk that data may not fit in.
	 * </p>
	 * 
	 * <p>
	 * It is up to user to ensure provided array is large enough
	 * </p>
	 * 
	 * @author Jan Vybíral
	 * 
	 */
	private static class FixedByteArrayOutputStream extends OutputStream {

		public FixedByteArrayOutputStream(byte[] buffer) {
			this.buffer = buffer;
		}

		private int currentMark = 0;
		private final byte[] buffer;

		@Override
		public void write(int b) throws IOException {
			buffer[currentMark] = (byte) b;
			currentMark++;
		}

		@Override
		public void write(byte[] b) throws IOException {
			System.arraycopy(b, 0, buffer, currentMark, b.length);
			currentMark += b.length;
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			System.arraycopy(b, off, buffer, currentMark, len);
			currentMark += len;
		}

		/**
		 * Returns number of bytes written by the stream.
		 * 
		 * @return
		 */
		public int getBytesWritten() {
			return currentMark;
		}
	}

}
