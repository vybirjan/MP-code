package cz.cvut.fit.vybirjan.mp.common.crypto;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.util.Random;
import java.util.zip.ZipEntry;

import org.junit.BeforeClass;
import org.junit.Test;

import cz.cvut.fit.vybirjan.mp.common.crypto.FileEncryptor.ProcessStrategy;
import cz.cvut.fit.vybirjan.mp.common.crypto.FileEncryptor.ZipEntryClassProcessingStrategy;

public class TestSigning {

	static KeyPair kp;
	static Random rnd;

	@BeforeClass
	public static void init() {
		kp = Signing.generateKeyPair();
		assertNotNull(kp);

		rnd = new Random();
	}

	@Test
	public void testSign() {
		Signing enc = new Signing(kp.getPrivate());
		Signing dec = new Signing(kp.getPublic());

		byte[] data = new byte[1024];
		rnd.nextBytes(data);

		byte[] sig = enc.sign(data);
		assertNotNull(sig);

		byte[] dataCopy = new byte[data.length];
		System.arraycopy(data, 0, dataCopy, 0, data.length);

		assertTrue(dec.verify(dataCopy, sig));
		dataCopy[5] = (byte) (dataCopy[5] + 2);

		assertFalse(dec.verify(dataCopy, sig));
	}

	@Test
	public void testZipProcessing() throws IOException {
		ProcessStrategy<Object> mock = mock(ProcessStrategy.class);

		ZipEntryClassProcessingStrategy strategy = new ZipEntryClassProcessingStrategy(mock);

		byte[] data = new byte[] { 0 };
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ByteArrayOutputStream mockOut = mock(ByteArrayOutputStream.class);

		strategy.process(new ZipEntry("com/test/foo.txt"), in, mockOut);

		verify(mock, never()).process(any(), any(InputStream.class), any(OutputStream.class));
		verify(mockOut).write(any(byte[].class), anyInt(), eq(1));

		mockOut = mock(ByteArrayOutputStream.class);
		in.reset();
		ZipEntry entry = new ZipEntry("com/test/Class.class");
		strategy.process(entry, in, mockOut);

		verify(mock, only()).process(entry, in, mockOut);

		verify(mockOut, never()).write(any(byte[].class));
		verify(mockOut, never()).write(anyInt());
		verify(mockOut, never()).write(any(byte[].class), anyInt(), anyInt());
	}
}
