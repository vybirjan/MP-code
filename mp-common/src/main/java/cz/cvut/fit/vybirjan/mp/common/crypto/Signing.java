package cz.cvut.fit.vybirjan.mp.common.crypto;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;

import cz.cvut.fit.vybirjan.mp.common.Utils;

public class Signing {

	private final Key key;

	public static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
	public static final String KEY_ALGORITHM = "RSA";
	public static final int KEY_SIZE = 1024;

	public Signing(Key key) {
		this.key = key;
	}

	public boolean verify(byte[] data, byte[] signature) {
		try {
			Cipher c = Cipher.getInstance(CIPHER_ALGORITHM);
			c.init(Cipher.DECRYPT_MODE, key);
			byte[] decrypted = c.doFinal(signature);
			byte[] dataHash = Utils.hash(data);
			return Arrays.equals(decrypted, dataHash);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			throw new AssertionError("Cipher error");
		}
	}

	public byte[] sign(byte[] data) {
		try {
			Cipher c = Cipher.getInstance(CIPHER_ALGORITHM);
			c.init(Cipher.ENCRYPT_MODE, key);

			byte[] hash = Utils.hash(data);
			byte[] encoded = c.doFinal(hash);

			return encoded;
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			throw new AssertionError("Cipher error");
		}
	}

	public static KeyPair generateKeyPair() {
		try {
			KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
			generator.initialize(KEY_SIZE);
			return generator.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError(KEY_ALGORITHM + " key algorithm not found");
		}
	}

	public static void main(String[] args) {
		KeyPair pair = generateKeyPair();
		System.out.format("Public: %s\n", Utils.encode(Utils.serialize(pair.getPublic())));
		System.out.format("Private: %s\n", Utils.encode(Utils.serialize(pair.getPrivate())));
	}
}
