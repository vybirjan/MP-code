package cz.cvut.fit.vybirjan.mp.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class Utils {

	public static final String HASH_ALGORITHM = "SHA1";

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
			return str.getBytes("UTF-8");
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
		if (data == null || data.length != 4)
			return 0x0;

		return ((0xff & data[offset]) << 24 |
				(0xff & data[offset + 1]) << 16 |
				(0xff & data[offset + 2]) << 8 | (0xff & data[offset + 3]) << 0);
	}

}
