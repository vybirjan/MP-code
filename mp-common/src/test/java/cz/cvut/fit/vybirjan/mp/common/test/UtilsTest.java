package cz.cvut.fit.vybirjan.mp.common.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.zip.ZipEntry;

import org.junit.BeforeClass;
import org.junit.Test;

import cz.cvut.fit.vybirjan.mp.common.Utils;

public class UtilsTest {

	private static Random rnd;

	@BeforeClass
	public static void init() {
		rnd = new Random();
	}

	@Test
	public void testHash() {
		byte[] data = new byte[1024];
		rnd.nextBytes(data);

		byte[] hash = Utils.hash(data);
		assertNotNull(hash);

		byte[] hashAgain = Utils.hash(data);
		assertNotNull(hashAgain);

		assertTrue(hash != hashAgain);

		assertTrue(Arrays.equals(hash, hashAgain));

		hash = Utils.hash(new byte[0]);
		assertNotNull(hash);

		assertTrue(Arrays.equals(hash, Utils.hash(new byte[0])));

		hash = Utils.hash(data, 3, 20);
		assertNotNull(hash);

		hashAgain = Utils.hash(data, 3, 21);
		assertNotNull(hashAgain);

		assertFalse(Arrays.equals(hash, hashAgain));

		hash = Utils.hash(data, 3, 20);
		assertNotNull(hash);

		hashAgain = Utils.hash(data, 3, 20);
		assertNotNull(hashAgain);

		assertTrue(Arrays.equals(hash, hashAgain));
	}

	@Test
	public void testEncode() {
		byte[] data = new byte[1024];
		rnd.nextBytes(data);

		String result = Utils.encode(data);
		assertNotNull(result);

		byte[] decoded = Utils.decode(result);
		assertNotNull(decoded);

		assertTrue(Arrays.equals(data, decoded));
	}

	@Test
	public void testToInt() {
		int[] data = new int[] { 0, 1, -1, Integer.MAX_VALUE, Integer.MIN_VALUE, rnd.nextInt(), rnd.nextInt() };

		for (int i : data) {
			byte[] raw = Utils.toByteArray(i);
			assertNotNull(raw);
			assertEquals(i, Utils.toInt(raw, 0));
		}
	}

	@Test
	public void testMatchesPackagePattern() {
		assertTrue(Utils.matchesPackagePattern("com.test", new ZipEntry("com/test/Class.class")));
		assertTrue(Utils.matchesPackagePattern("com.test.Class", new ZipEntry("com/test/Class.class")));
		assertTrue(Utils.matchesPackagePattern("com.test.*", new ZipEntry("com/test/Class.class")));
		assertTrue(Utils.matchesPackagePattern("com.*", new ZipEntry("com/test/Class.class")));

		assertFalse(Utils.matchesPackagePattern("com.test.Class", new ZipEntry("com/test/OtherClass.class")));
		assertFalse(Utils.matchesPackagePattern("com.test", new ZipEntry("com/test/foo/OtherClass.class")));
		assertFalse(Utils.matchesPackagePattern("com.test.*", new ZipEntry("com/test2/OtherClass.class")));
	}

	@Test
	public void testMinMax() {
		Date d1 = new Date(123456);
		Date d2 = new Date(1234567);

		assertEquals(d2, Utils.max(d1, d2));
		assertEquals(d1, Utils.min(d1, d2));

		assertEquals(d1, Utils.min(d1, null));
		assertEquals(d2, Utils.max(null, d2));

		assertNull(Utils.max(null, null));
	}

	@Test
	public void testIsValid() {
		Date start = new Date(System.currentTimeMillis() - 1000);
		Date end = new Date(System.currentTimeMillis() + 1000000);

		assertTrue(Utils.isValid(null, null));
		assertTrue(Utils.isValid(null, end));
		assertTrue(Utils.isValid(start, end));
		assertTrue(Utils.isValid(start, null));

		assertFalse(Utils.isValid(end, null));
		assertFalse(Utils.isValid(null, start));
	}

}
