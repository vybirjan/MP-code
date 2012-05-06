package cz.cvut.fit.vybirjan.mp.common.crypto;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;

import cz.cvut.fit.vybirjan.mp.common.Utils;
import cz.cvut.fit.vybirjan.mp.common.crypto.FileEncryptor.ProcessStrategy;

public class TestFileEncryptor {

	static TaggedKey key;
	static Random rnd;

	@BeforeClass
	public static void init() {
		rnd = new Random();
		key = FileEncryptor.generateDefaultKey(6);
		assertEquals(6, key.getTag());
	}

	@Test
	public void testSerialize() {
		byte[] serialized = FileEncryptor.serializeKey(key);
		assertNotNull(serialized);

		TaggedKey deser = FileEncryptor.deserializeKey(serialized);
		assertFalse(key == deser);

		assertEquals(key.getTag(), deser.getTag());
		assertEquals(key.getAlgorithm(), deser.getAlgorithm());
		assertArrayEquals(key.getEncoded(), deser.getEncoded());
		assertEquals(key.getFormat(), deser.getFormat());
	}

	@Test
	public void testEncryption() throws IOException {
		ProcessStrategy<Object> encr = FileEncryptor.createDefaultEncryptStrategy(key);
		ProcessStrategy<Object> dex = FileEncryptor.createDefaultDecryptStrategy(key);

		byte[] data = new byte[1024];
		rnd.nextBytes(data);

		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayOutputStream decOut = new ByteArrayOutputStream();

		encr.process(null, in, out);

		assertFalse(Arrays.equals(out.toByteArray(), data));

		dex.process(null, new ByteArrayInputStream(out.toByteArray()), decOut);
		assertArrayEquals(data, decOut.toByteArray());
	}

	@Test
	public void testEncryptionFormat() throws IOException {
		ProcessStrategy<Object> encr = new FileEncryptor.TaggingEncryptionStrategy(key.getTag(), FileEncryptor.createDefaultEncryptStrategy(key));

		byte[] data = new byte[1024];
		rnd.nextBytes(data);

		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		encr.process(null, in, out);
		byte[] enc = out.toByteArray();

		assertEquals(FileEncryptor.HEAD, enc[0]);
		byte[] buf = new byte[4];
		System.arraycopy(enc, 1, buf, 0, 4);

		assertEquals(key.getTag(), Utils.toInt(buf, 0));

		ProcessStrategy<Object> decr = new FileEncryptor.TaggingDecryptionStrategy(key.getTag(), FileEncryptor.createDefaultDecryptStrategy(key));
		out.reset();
		in = new ByteArrayInputStream(enc);
		decr.process(null, in, out);

		assertArrayEquals(data, out.toByteArray());
	}
}
