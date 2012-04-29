package cz.cvut.fit.vybirjan.mp.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import java.util.jar.JarEntry;

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

	public static boolean matchesPackagePattern(String classPattern, JarEntry entry) {
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
}
