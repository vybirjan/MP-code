package cz.cvut.fit.vybirjan.mp.common.comm.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;

public class HardwareFingerprintTest {

	@Test
	public void testFingerprint() {
		HardwareFingerprint fp1 = new HardwareFingerprint("foo", "bar");
		HardwareFingerprint fp2 = new HardwareFingerprint("XXX", " F ");
		HardwareFingerprint fp3 = new HardwareFingerprint("___", "fccsdfh");

		List<HardwareFingerprint> fps = Arrays.asList(fp1, fp2, fp3);

		String serialized = HardwareFingerprint.toMultiString(fps);
		assertNotNull(serialized);

		List<HardwareFingerprint> other = HardwareFingerprint.fromMultiString(serialized);
		for (HardwareFingerprint fp : other) {
			assertTrue(fps.contains(fp));
		}

		List<HardwareFingerprint> fps2 = Arrays.asList(fp2, fp3, fp1);
		String serialized2 = HardwareFingerprint.toMultiString(fps2);

		assertEquals(serialized, serialized2);
	}

}
