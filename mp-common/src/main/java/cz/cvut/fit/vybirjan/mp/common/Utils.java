package cz.cvut.fit.vybirjan.mp.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;

public class Utils {

	public static final String HASH_ALGORITHM = "SHA1";
	public static final String MIME_WILDCARD = "*";
	public static final String UTF_8 = "UTF-8";

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

	public static byte[] hash(byte[] data) {
		MessageDigest digest = getDigest();
		return digest.digest(data);
	}

	public static String encode(byte[] data) {
		return DatatypeConverter.printBase64Binary(data);
	}

	public static byte[] decode(String encoded) {
		return DatatypeConverter.parseBase64Binary(encoded);
	}

	public static byte[] toUtf8(String str) {
		try {
			return str.getBytes(UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError("UTF-8 encoding not supported");
		}
	}

	public static String fromUtf8(byte[] data, int offset, int length) {
		try {
			return new String(data, offset, length, UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError("UTF-8 encoding not supported");
		}
	}

	/*
	 * Source:
	 * http://www.daniweb.com/software-development/java/code/216874/primitive
	 * -types-as-byte-arrays
	 */
	public static int toInt(byte[] data, int offset) {
		return ((0xff & data[offset]) << 24 |
				(0xff & data[offset + 1]) << 16 |
				(0xff & data[offset + 2]) << 8 | (0xff & data[offset + 3]) << 0);
	}

	public static final byte[] toByteArray(int value) {
		return new byte[] {
				(byte) (value >>> 24),
				(byte) (value >>> 16),
				(byte) (value >>> 8),
				(byte) value };
	}

	public static Date min(Date first, Date second) {
		if (first == null) {
			return second;
		} else if (second == null) {
			return first;
		} else {
			return first.getTime() > second.getTime() ? second : first;
		}
	}

	public static Date max(Date first, Date second) {
		if (first == null) {
			return second;
		} else if (second == null) {
			return first;
		} else {
			return first.getTime() > second.getTime() ? first : second;
		}
	}

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
}
