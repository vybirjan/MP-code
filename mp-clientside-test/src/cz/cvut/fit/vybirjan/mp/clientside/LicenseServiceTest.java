package cz.cvut.fit.vybirjan.mp.clientside;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.cvut.fit.vybirjan.mp.clientside.LicenseCheckException.LicenseCheckErrorType;
import cz.cvut.fit.vybirjan.mp.clientside.internal.core.HardwareFingerprintProvider;
import cz.cvut.fit.vybirjan.mp.clientside.internal.core.LicenseServiceClient;
import cz.cvut.fit.vybirjan.mp.clientside.internal.core.SecureStorage;
import cz.cvut.fit.vybirjan.mp.common.Utils;
import cz.cvut.fit.vybirjan.mp.common.comm.Feature;
import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseInformation;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseResponse;
import cz.cvut.fit.vybirjan.mp.common.comm.ResponseType;

public class LicenseServiceTest {

	private LicenseService service;

	private SecureStorage storage;
	private LicenseServiceClient client;
	private HardwareFingerprintProvider fingerprintProvider;

	private static LicenseServiceConfig config;
	private static Key publicKey;
	private static Key privateKey;

	@BeforeClass
	public static void initClass() {
		publicKey = Utils
				.deserialize(
						Utils.decode("rO0ABXNyABRqYXZhLnNlY3VyaXR5LktleVJlcL35T7OImqVDAgAETAAJYWxnb3JpdGhtdAASTGphdmEvbGFuZy9TdHJpbmc7WwAHZW5jb2RlZHQAAltCTAAGZm9ybWF0cQB+AAFMAAR0eXBldAAbTGphdmEvc2VjdXJpdHkvS2V5UmVwJFR5cGU7eHB0AANSU0F1cgACW0Ks8xf4BghU4AIAAHhwAAAAojCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAh+sLzWpr4kubWnpwddOcynX7jQD5RkHGm8UrpIv0JF+zrWZCXY6qOp3SKPk0OU1hlwFyq3nVX4d50Y3iH0vJ4yTEEX0SQfbRDK4zsaEkQdb2aVPVnS71EpXOfi/dXD/R1zqL7Ix1yST9pZYIbDD1cWfdtIAQIlQ4JDOAc02lKx0CAwEAAXQABVguNTA5fnIAGWphdmEuc2VjdXJpdHkuS2V5UmVwJFR5cGUAAAAAAAAAABIAAHhyAA5qYXZhLmxhbmcuRW51bQAAAAAAAAAAEgAAeHB0AAZQVUJMSUM="),
						Key.class);
		privateKey = Utils
				.deserialize(
						Utils.decode("rO0ABXNyABRqYXZhLnNlY3VyaXR5LktleVJlcL35T7OImqVDAgAETAAJYWxnb3JpdGhtdAASTGphdmEvbGFuZy9TdHJpbmc7WwAHZW5jb2RlZHQAAltCTAAGZm9ybWF0cQB+AAFMAAR0eXBldAAbTGphdmEvc2VjdXJpdHkvS2V5UmVwJFR5cGU7eHB0AANSU0F1cgACW0Ks8xf4BghU4AIAAHhwAAACejCCAnYCAQAwDQYJKoZIhvcNAQEBBQAEggJgMIICXAIBAAKBgQCH6wvNamviS5taenB105zKdfuNAPlGQcabxSuki/QkX7OtZkJdjqo6ndIo+TQ5TWGXAXKredVfh3nRjeIfS8njJMQRfRJB9tEMrjOxoSRB1vZpU9WdLvUSlc5+L91cP9HXOovsjHXJJP2llghsMPVxZ920gBAiVDgkM4BzTaUrHQIDAQABAoGAHpLaxsxHlFHZklK0dWyyekBr413ytMEbAfMqTAtHUd9NNZYpG4558FGL8reL7c/gQe2Lc9MmEiURW/gEg3Gy6ddZhnh7I7Elw24hKZdfCQpJocNFCQKvJl74Uxg1Zb2ChVmlNMkQ9ZBU5CGWgXG/J09SeiCiOW4UKhYh/X3j5ykCQQDDn/rA2xCUYjJk7Xlr1/O5WGzf+fF0zKZ+PJnHHj7CaonChf9Ezi7exjGFAgpR5HGFYlv60JP6alIh9GNH81xjAkEAsd26P0vJA4YCDgNv8RN6g2axqWYjIt5A/TtgfiHSG8VfhqKW70T9wV0gzwaJvOGKABxd61DQ7J4h6MWeLVUyfwJAUES1dCqvC+Oa//S9DLYoiFWzHtgE7kMG0ed1xdEwOT/T1OB8lLPxYXF7YjMrHt0Yg32PdkPdZDP6DXqkviIqNwJAH/qUjOLMTlTes7RMgRSWd1+UW7egY5mAJ8dwLt1X0GRpK2S6LGTEFuruhipPw2Ttkd2HyVIrbg90W0yEhvQFcwJBAIDZFPBx0dwvii3/ifEmTkDfmjGRoeCKoPn9AVkIXHdmMKAS0OSdeC+cNMs0tQqjMDprEdT2HgwESik3UnCKLHl0AAZQS0NTIzh+cgAZamF2YS5zZWN1cml0eS5LZXlSZXAkVHlwZQAAAAAAAAAAEgAAeHIADmphdmEubGFuZy5FbnVtAAAAAAAAAAASAAB4cHQAB1BSSVZBVEU="),
						Key.class);
		config = new LicenseServiceConfig("testapp", publicKey, "foo", true);
		LicenseService.configure(config);
	}

	@Before
	public void init() {
		service = new LicenseService(storage = mock(SecureStorage.class), client = mock(LicenseServiceClient.class),
				fingerprintProvider = mock(HardwareFingerprintProvider.class));
	}

	@Test
	public void testNoneFound() throws IOException, LicenseCheckException {
		when(storage.loadInfo()).thenReturn(null);

		assertNull(service.getCurrent());

		try {
			service.checkOffline();
			fail();
		} catch (LicenseCheckException e) {
			assertEquals(LicenseCheckErrorType.NOT_FOUND, e.getErrorType());
			verify(storage).clear();
		}
	}

	@Test
	public void testInvalid() throws IOException {
		LicenseInformation info = new LicenseInformation();
		info.setLicenseNumber("dfg");
		info.sign(privateKey);
		info.addFeature(new Feature("d", "ffd", null, null));

		when(storage.loadInfo()).thenReturn(info);

		try {
			service.getCurrent();
			fail();
		} catch (LicenseCheckException e) {
			assertEquals(LicenseCheckErrorType.INVALID, e.getErrorType());
			verify(storage).clear();
		}
	}

	@Test
	public void testExpired() throws IOException {
		LicenseInformation info = new LicenseInformation();
		info.setLicenseNumber("dfg");
		info.addFeature(new Feature("d", "ffd", null, new Date(System.currentTimeMillis() - 10000)));
		info.sign(privateKey);

		when(storage.loadInfo()).thenReturn(info);

		try {
			service.getCurrent();
			fail();
		} catch (LicenseCheckException e) {
			assertEquals(LicenseCheckErrorType.EXPIRED, e.getErrorType());
			verify(storage).clear();
		}
	}

	@Test
	public void testFPMismatch() throws IOException {
		LicenseInformation info = new LicenseInformation();
		info.setLicenseNumber("dfg");
		info.addFeature(new Feature("d", "ffd", null, null));
		info.addFingerPrint(new HardwareFingerprint("FOO", "BAR"));
		info.sign(privateKey);

		when(storage.loadInfo()).thenReturn(info);
		when(fingerprintProvider.collectFingerprints()).thenReturn(Collections.singletonList(new HardwareFingerprint("Other", "CCc")));

		try {
			service.getCurrent();
			fail();
		} catch (LicenseCheckException e) {
			assertEquals(LicenseCheckErrorType.FINGERPRINT_MISMATCH, e.getErrorType());
			verify(storage).clear();
		}
	}

	@Test
	public void testOk() throws IOException, LicenseCheckException {
		LicenseInformation info = new LicenseInformation();
		info.setLicenseNumber("dfg");
		info.addFeature(new Feature("d", "ffd", null, null));
		info.addFingerPrint(new HardwareFingerprint("FOO", "BAR"));
		info.sign(privateKey);

		when(storage.loadInfo()).thenReturn(info);
		when(fingerprintProvider.collectFingerprints()).thenReturn(Collections.singletonList(new HardwareFingerprint("FOO", "BAR")));

		assertNotNull(service.getCurrent());
		verify(storage, never()).clear();

		service.checkOffline();
		verify(client, never()).getLicense(anyString(), anyList());
	}

	@Test
	public void checkStorageErrorWontFail() throws IOException, LicenseCheckException {
		when(storage.loadInfo()).thenThrow(IOException.class);

		assertNull(service.getCurrent());
	}

	@Test(expected = IOException.class)
	public void checkServerIOError() throws IOException, LicenseCheckException, LicenseRetrieveException {
		LicenseInformation info = new LicenseInformation();
		info.setLicenseNumber("dfg");
		info.addFeature(new Feature("d", "ffd", null, null));
		info.addFingerPrint(new HardwareFingerprint("FOO", "BAR"));
		info.sign(privateKey);

		when(storage.loadInfo()).thenReturn(info);
		when(fingerprintProvider.collectFingerprints()).thenReturn(Collections.singletonList(new HardwareFingerprint("FOO", "BAR")));

		when(client.getLicense(eq(info.getLicenseNumber()), eq(new ArrayList(info.getFingerPrints())))).thenThrow(IOException.class);

		assertNotNull(service.getCurrent());
		verify(storage, never()).clear();

		service.checkOnline();
	}

	@Test
	public void checkServerVerificationError() throws IOException, LicenseCheckException, LicenseRetrieveException {
		LicenseInformation info = new LicenseInformation();
		info.setLicenseNumber("dfg");
		info.addFeature(new Feature("d", "ffd", null, null));
		info.addFingerPrint(new HardwareFingerprint("FOO", "BAR"));
		info.sign(privateKey);

		when(storage.loadInfo()).thenReturn(info);
		when(fingerprintProvider.collectFingerprints()).thenReturn(Collections.singletonList(new HardwareFingerprint("FOO", "BAR")));

		when(client.getLicense(eq(info.getLicenseNumber()), eq(new ArrayList(info.getFingerPrints())))).thenReturn(LicenseResponse.expired());

		assertNotNull(service.getCurrent());
		verify(storage, never()).clear();

		try {
			service.checkOnline();
			fail();
		} catch (LicenseRetrieveException e) {
			assertEquals(ResponseType.ERROR_EXPIRED, e.getResponseType());
			verify(storage).clear();
		}
	}

	public void checkServerOk() throws LicenseCheckException, IOException, LicenseRetrieveException {
		LicenseInformation info = new LicenseInformation();
		info.setLicenseNumber("dfg");
		info.addFeature(new Feature("d", "ffd", null, null));
		info.addFingerPrint(new HardwareFingerprint("FOO", "BAR"));
		info.sign(privateKey);

		when(storage.loadInfo()).thenReturn(info);
		when(fingerprintProvider.collectFingerprints()).thenReturn(Collections.singletonList(new HardwareFingerprint("FOO", "BAR")));

		when(client.getLicense(eq(info.getLicenseNumber()), eq(new ArrayList(info.getFingerPrints())))).thenReturn(LicenseResponse.createdNew(info));

		assertNotNull(service.getCurrent());
		verify(storage, never()).clear();

		assertNotNull(service.checkOnline());
	}
}
