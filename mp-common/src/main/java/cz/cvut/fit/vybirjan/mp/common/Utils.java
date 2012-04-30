package cz.cvut.fit.vybirjan.mp.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.zip.ZipEntry;

import javax.xml.bind.DatatypeConverter;

/**
 * Class containing frequently used methods
 * 
 * @author Jan Vybíral
 * 
 */
public final class Utils {

	/**
	 * Name of algorithm used to compute digests
	 */
	public static final String HASH_ALGORITHM = "SHA1";
	/**
	 * Textual name of UTF-8 encoding used when encoding name is required
	 */
	public static final String UTF_8 = "UTF-8";

	/**
	 * Creates configured instance of digest class using default algorithm to
	 * compute digests from data.
	 * 
	 * @return Configured instance of MessageDigest class
	 */
	public static MessageDigest getDigest() {
		try {
			return MessageDigest.getInstance(HASH_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError(HASH_ALGORITHM + " hash algorithm not found");
		}
	}

	private Utils() {
		throw new AssertionError();
	}

	/**
	 * Computes message digest from provided data using default algorithm.
	 * 
	 * @param data
	 *            Data to be processed
	 * @return Message digest
	 */
	public static byte[] hash(byte[] data) {
		MessageDigest digest = getDigest();
		return digest.digest(data);
	}

	/**
	 * Computes message digest from provided data using default algorithm.
	 * 
	 * @return Message digest
	 */
	public static byte[] hash(byte[] data, int offset, int length) {
		MessageDigest digest = getDigest();
		digest.update(data, offset, length);
		return digest.digest();
	}

	/**
	 * <p>
	 * Encodes input data into printable characters.
	 * </p>
	 * 
	 * <p>
	 * Data can be decoded using {@link Utils#decode(String)} method
	 * </p>
	 * 
	 * @param data
	 *            Data to be encoded
	 * @return String of printable characters with encoded data
	 */
	public static String encode(byte[] data) {
		return DatatypeConverter.printBase64Binary(data);
	}

	/**
	 * Decodes String obtained from {@linkplain Utils#encode(byte[])} into data.
	 * 
	 * @param encoded
	 *            Encoded data
	 * @return Original data
	 */
	public static byte[] decode(String encoded) {
		return DatatypeConverter.parseBase64Binary(encoded);
	}

	/**
	 * Converts String into byte array encoded in UTF-8 encoding
	 * 
	 * @param str
	 *            Input string
	 * @return String encoded in UTF-8 encoding
	 */
	public static byte[] toUtf8(String str) {
		try {
			return str.getBytes(UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError("UTF-8 encoding not supported");
		}
	}

	/**
	 * Decodes String from byte array encoded using UTF-8 encoding
	 * 
	 * @param data
	 *            Data containing string
	 * @param offset
	 *            First byte with string
	 * @param length
	 *            Number of bytes of string
	 * @return Decoded string
	 */
	public static String fromUtf8(byte[] data, int offset, int length) {
		try {
			return new String(data, offset, length, UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError("UTF-8 encoding not supported");
		}
	}

	/*
	 * Source:
	 */
	/**
	 * Converts 4 bytes of input array into integer.
	 * 
	 * @param data
	 *            Source data
	 * @param offset
	 *            Starting position in array. Next 4 bytes will be converted to
	 *            int.
	 * @return Integer value of data
	 * @see http 
	 *      ://www.daniweb.com/software-development/java/code/216874/primitive
	 *      -types-as-byte-arrays
	 */
	public static int toInt(byte[] data, int offset) {
		return ((0xff & data[offset]) << 24 |
				(0xff & data[offset + 1]) << 16 |
				(0xff & data[offset + 2]) << 8 | (0xff & data[offset + 3]) << 0);
	}

	/**
	 * Converts integer into byte array.
	 * 
	 * @param value
	 *            Integer to be converted
	 * @return Byte array of 4 bytes containing integer value
	 */
	public static final byte[] toByteArray(int value) {
		return new byte[] {
				(byte) (value >>> 24),
				(byte) (value >>> 16),
				(byte) (value >>> 8),
				(byte) value };
	}

	/**
	 * Returns smaller of two dates. Any date is considered bigger than null.
	 * 
	 * @param first
	 *            First date to be compared
	 * @param second
	 *            Second date to be compared
	 * @return Smaller of two dates, or null if both dates are null
	 */
	public static Date min(Date first, Date second) {
		if (first == null) {
			return second;
		} else if (second == null) {
			return first;
		} else {
			return first.getTime() > second.getTime() ? second : first;
		}
	}

	/**
	 * Returns bigger of two dates. Any date is considered bigger than null.
	 * 
	 * @param first
	 *            First date to be compared
	 * @param second
	 *            Second date to be compared
	 * @return Bigger of two dates, or null if both dates are null
	 */
	public static Date max(Date first, Date second) {
		if (first == null) {
			return second;
		} else if (second == null) {
			return first;
		} else {
			return first.getTime() > second.getTime() ? first : second;
		}
	}

	/**
	 * Indicates, whether object, whose validity is bound by two dates is valid
	 * at current time.
	 * 
	 * @param validityStart
	 *            Start date of validity, or null if validity is not bounded
	 * @param validityEnd
	 *            End date of validity, or null if validity is not bounded
	 * @return True if object is valid, false otherwise
	 */
	public static boolean isValid(Date validityStart, Date validityEnd) {
		Date now = new Date();

		if (validityStart != null && validityStart.after(now)) {
			return false;
		}

		if (validityEnd != null && validityEnd.before(now)) {
			return false;
		}

		return true;
	}

	/**
	 * Serializes object into byte array
	 * 
	 * @param s
	 *            Object to be serialized
	 * @return Object serialized in byte array
	 */
	public static byte[] serialize(Serializable s) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objOut = new ObjectOutputStream(out);
			objOut.writeObject(s);
			objOut.close();
		} catch (IOException e) {
			// should not happen on in-memory stream
			throw new RuntimeException("Failed to serialize to inmemory stream", e);
		}

		return out.toByteArray();
	}

	/**
	 * Deserializes object from byte array
	 * 
	 * @param serialized
	 *            Serialized object in byte array using ObjectOutputStream
	 * @param result
	 *            Class of serialized object
	 * @return Deserialized object
	 * @see ObjectOutputStream
	 */
	public static <T> T deserialize(byte[] serialized, Class<T> result) {
		ByteArrayInputStream in = new ByteArrayInputStream(serialized);
		try {
			ObjectInputStream objIn = new ObjectInputStream(in);
			return result.cast(objIn.readObject());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Failed to deserialize from inmemory stream", e);
		} catch (IOException e) {
			// should not happen on in-memory stream
			throw new RuntimeException("Failed to deserialize from inmemory stream", e);
		}
	}

	/**
	 * <p>
	 * Checks whether given jar entry matches package pattern.
	 * </p>
	 * 
	 * <p>
	 * Package pattern is string in one of these forms:
	 * </p>
	 * <ul>
	 * <li>Package name, e.g. com.test.mypackage - matches only classes in
	 * package com.test.mypackage</li>
	 * <li>Fully qualified class name, e.g. com.test.mypackage.MyClass - matches
	 * only class com.test.mypackage.MyClass</li>
	 * <li>Package name with wildcard, e.g. com.test.* - matches all classes in
	 * com.test package and all subpackages</li>
	 * </ul>
	 * 
	 * <p>
	 * </p>
	 * 
	 * @param classPattern
	 *            Pattern to match entry against
	 * @param entry
	 *            Jar entry to match
	 * @return true if entry matches pattern
	 */
	public static boolean matchesPackagePattern(String classPattern, ZipEntry entry) {
		if (classPattern.length() > entry.getName().length()) {
			return false;
		}
		int i = 0;
		for (; i < classPattern.length(); i++) {
			char patternChar = classPattern.charAt(i);
			char entryNameChar = entry.getName().charAt(i);

			switch (patternChar) {
				case '*':
					return true;
				case '.':
					if (entryNameChar != '/') {
						return false;
					}
					break;
				default:
					if (patternChar != entryNameChar) {
						return false;
					}
			}
		}

		return !(entry.getName().lastIndexOf('/') > i);
	}

	private static final String[] UNITS = new String[] { "B", "kB", "MB", "GB", "TB" };

	/**
	 * Converts value in bytes into human readable form, e.g 520,51 MB
	 * 
	 * @param size
	 *            Size in bytes
	 * @return Human readable form of size
	 */
	public static String toHumanReadable(long size) {
		int unitIndex = 0;
		BigDecimal currentVal = new BigDecimal(size);

		BigDecimal kilo = new BigDecimal(1024);

		while (currentVal.compareTo(kilo) > 0) {
			unitIndex++;
			currentVal = currentVal.divide(kilo);
		}

		DecimalFormat df = new DecimalFormat("#.##");
		return df.format(currentVal) + " " + UNITS[unitIndex];

	}

	/**
	 * Reads content of file into specified buffer.
	 * 
	 * @param f
	 *            file to read
	 * @param buffer
	 *            Buffer to fill with data
	 * @return Number of bytes read or -1 if buffer is too small
	 */
	public static int readFully(File f, byte[] buffer) throws IOException {
		long length = f.length();

		if (buffer.length < length) {
			return -1;
		}

		FileInputStream in = new FileInputStream(f);
		try {
			int readSoFar = 0;
			int read = 0;
			while ((read = in.read(buffer, readSoFar, buffer.length - readSoFar)) != -1 && readSoFar != length) {
				readSoFar += read;
			}
			return readSoFar;
		} finally {
			in.close();
		}
	}

	public static void writeFully(File f, byte[] buffer, int offset, int length) throws IOException {
		FileOutputStream out = new FileOutputStream(f);
		try {
			out.write(buffer, offset, length);
		} finally {
			out.close();
		}
	}

	/**
	 * Ensures that input buffer has specified size.
	 * 
	 * @param size
	 *            Minimal required size
	 * @param buffer
	 *            Buffer to check
	 * @return Byte array of at least required size. Returns provided buffer if
	 *         its size is sufficent, allocates new byte array otherwise
	 */
	public static byte[] ensureSize(int size, byte[] buffer) {
		if (buffer.length < size) {
			return new byte[size];
		} else {
			return buffer;
		}
	}
}
