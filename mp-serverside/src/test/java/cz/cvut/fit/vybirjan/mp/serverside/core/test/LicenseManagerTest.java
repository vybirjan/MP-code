package cz.cvut.fit.vybirjan.mp.serverside.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import cz.cvut.fit.vybirjan.mp.common.comm.Feature;
import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseInformation;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseRequest;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseResponse;
import cz.cvut.fit.vybirjan.mp.common.comm.ResponseType;
import cz.cvut.fit.vybirjan.mp.common.crypto.TaggedKey;
import cz.cvut.fit.vybirjan.mp.serverside.core.DataSource;
import cz.cvut.fit.vybirjan.mp.serverside.core.LicenseManager;
import cz.cvut.fit.vybirjan.mp.serverside.domain.Activation;
import cz.cvut.fit.vybirjan.mp.serverside.domain.EntityFactory;
import cz.cvut.fit.vybirjan.mp.serverside.domain.License;
import cz.cvut.fit.vybirjan.mp.serverside.impl.LicenseManagerImpl;

public class LicenseManagerTest {

	private DataSource dataSource;
	private EntityFactory factory;
	private final List<HardwareFingerprint> hwfp = HardwareFingerprint.fromMultiString("A:b;C:d");
	private static final Feature testFeature = new Feature("F01", "testfeature", null, null);
	private final Set<Feature> features = new HashSet<Feature>(Arrays.asList(testFeature));

	private static final cz.cvut.fit.vybirjan.mp.serverside.domain.Feature MOCK_FEAURE = new cz.cvut.fit.vybirjan.mp.serverside.domain.Feature() {

		@Override
		public Date getValidTo() {
			return null;
		}

		@Override
		public Date getValidFrom() {
			return null;
		}

		@Override
		public TaggedKey getKey() {
			return null;
		}

		@Override
		public String getDescription() {
			return testFeature.getDescription();
		}

		@Override
		public String getCode() {
			return testFeature.getCode();
		}
	};

	@Before
	public void before() {
		dataSource = mock(DataSource.class);
		factory = mock(EntityFactory.class);
	}

	@Test
	public void testGetLicense_NotFound() {
		LicenseManager mgr = new LicenseManagerImpl(dataSource, factory);

		LicenseRequest req = new LicenseRequest("testapp", "qwe123");
		req.addFingerprints(hwfp);

		LicenseResponse resp = mgr.getLicense(req);
		assertNotNull(resp);
		assertNull(resp.getLicenseInformation());
		assertEquals(ResponseType.ERROR_LICENSE_NOT_FOUND, resp.getType());
	}

	@Test
	public void testGetLicense_Inactive() {
		LicenseManager mgr = new LicenseManagerImpl(dataSource, factory);

		LicenseRequest req = new LicenseRequest("testapp", "qwe123");
		req.addFingerprints(hwfp);

		License licMock = mock(License.class);

		when(dataSource.findByNumber(eq("qwe123"))).thenReturn(licMock);

		LicenseResponse resp = mgr.getLicense(req);
		assertNotNull(resp);
		assertNull(resp.getLicenseInformation());
		assertEquals(ResponseType.ERROR_INACTIVE, resp.getType());
	}

	@Test
	public void testGetLicense_Expired() {
		LicenseManager mgr = new LicenseManagerImpl(dataSource, factory);

		LicenseRequest req = new LicenseRequest("testapp", "qwe123");
		req.addFingerprints(hwfp);

		License licMock = mock(License.class);
		when(licMock.isActive()).thenReturn(true);
		when(licMock.getValidTo()).thenReturn(new Date(System.currentTimeMillis() - 10000));

		Activation actiMock = mock(Activation.class);

		when(dataSource.findByNumber(eq("qwe123"))).thenReturn(licMock);
		when(dataSource.findActiveActivationForLicense(licMock, hwfp)).thenReturn(actiMock);

		LicenseResponse resp = mgr.getLicense(req);
		assertNotNull(resp);
		assertNull(resp.getLicenseInformation());
		assertEquals(ResponseType.ERROR_EXPIRED, resp.getType());
	}

	@Test
	public void testGetLicense_Ok() {
		LicenseManager mgr = new LicenseManagerImpl(dataSource, factory);

		LicenseRequest req = new LicenseRequest("testapp", "qwe123");
		req.addFingerprints(hwfp);

		License licMock = mock(License.class);
		when(licMock.isActive()).thenReturn(true);
		when(licMock.getNumber()).thenReturn("qwe123");

		Activation actiMock = mock(Activation.class);
		when(actiMock.getFingerprints()).thenReturn(hwfp);

		when(dataSource.findByNumber(eq("qwe123"))).thenReturn(licMock);
		when(dataSource.findActiveActivationForLicense(licMock, hwfp)).thenReturn(actiMock);
		when(dataSource.findFeaturesForLicense(licMock)).thenReturn(Collections.singletonList(MOCK_FEAURE));

		LicenseResponse resp = mgr.getLicense(req);
		assertNotNull(resp);
		assertNotNull(resp.getLicenseInformation());
		assertEquals(ResponseType.OK_EXISTING_VERIFIED, resp.getType());

		LicenseInformation info = resp.getLicenseInformation();
		assertEquals("qwe123", info.getLicenseNumber());
		assertEquals(features, info.getFeatures());
		assertEquals(new HashSet<HardwareFingerprint>(hwfp), info.getFingerPrints());
	}

	@Test
	public void testActivateLicense_NotFound() {
		LicenseManager mgr = new LicenseManagerImpl(dataSource, factory);

		LicenseRequest req = new LicenseRequest("testapp", "qwe123");
		req.addFingerprints(hwfp);

		LicenseResponse resp = mgr.activateLicense(req);
		assertNotNull(resp);
		assertNull(resp.getLicenseInformation());
		assertEquals(ResponseType.ERROR_LICENSE_NOT_FOUND, resp.getType());
	}

	@Test
	public void testActivateLicense_Inactive() {
		LicenseManager mgr = new LicenseManagerImpl(dataSource, factory);

		LicenseRequest req = new LicenseRequest("testapp", "qwe123");
		req.addFingerprints(hwfp);

		License licMock = mock(License.class);

		when(dataSource.findByNumber(eq("qwe123"))).thenReturn(licMock);

		LicenseResponse resp = mgr.activateLicense(req);
		assertNotNull(resp);
		assertNull(resp.getLicenseInformation());
		assertEquals(ResponseType.ERROR_INACTIVE, resp.getType());
	}

	@Test
	public void testActivateLicense_Expired() {
		LicenseManager mgr = new LicenseManagerImpl(dataSource, factory);

		LicenseRequest req = new LicenseRequest("testapp", "qwe123");
		req.addFingerprints(hwfp);

		License licMock = mock(License.class);
		when(licMock.isActive()).thenReturn(true);
		when(licMock.getValidTo()).thenReturn(new Date(System.currentTimeMillis() - 10000));

		Activation actiMock = mock(Activation.class);

		when(dataSource.findByNumber(eq("qwe123"))).thenReturn(licMock);
		when(dataSource.findActiveActivationForLicense(licMock, hwfp)).thenReturn(actiMock);

		LicenseResponse resp = mgr.activateLicense(req);
		assertNotNull(resp);
		assertNull(resp.getLicenseInformation());
		assertEquals(ResponseType.ERROR_EXPIRED, resp.getType());
	}

	@Test
	public void testActivateLicense_NoActivationsAllowed() {
		LicenseManager mgr = new LicenseManagerImpl(dataSource, factory);

		LicenseRequest req = new LicenseRequest("testapp", "qwe123");
		req.addFingerprints(hwfp);

		License licMock = mock(License.class);
		when(licMock.isActive()).thenReturn(true);
		when(licMock.getValidTo()).thenReturn(new Date(System.currentTimeMillis() + 100000));
		when(licMock.getMaxActivations()).thenReturn(50);

		when(dataSource.findByNumber(eq("qwe123"))).thenReturn(licMock);

		LicenseResponse resp = mgr.activateLicense(req);
		assertNotNull(resp);
		assertNull(resp.getLicenseInformation());
		assertEquals(ResponseType.ERROR_NEW_ACTIVATIONS_NOT_ALLOWED, resp.getType());
	}

	@Test
	public void testActivateLicense_TooManyActivations() {
		LicenseManager mgr = new LicenseManagerImpl(dataSource, factory);

		LicenseRequest req = new LicenseRequest("testapp", "qwe123");
		req.addFingerprints(hwfp);

		License licMock = mock(License.class);
		when(licMock.isActive()).thenReturn(true);
		when(licMock.getValidTo()).thenReturn(new Date(System.currentTimeMillis() + 100000));
		when(licMock.getMaxActivations()).thenReturn(1);
		when(licMock.isAllowNewActivations()).thenReturn(true);

		Activation actiMock = mock(Activation.class);

		when(dataSource.findByNumber(eq("qwe123"))).thenReturn(licMock);
		when(dataSource.findActiveActivationsForLicense(licMock)).thenReturn(Collections.singletonList(actiMock));

		LicenseResponse resp = mgr.activateLicense(req);
		assertNotNull(resp);
		assertEquals(ResponseType.ERROR_TOO_MANY_ACTIVATIONS, resp.getType());
		assertNull(resp.getLicenseInformation());
	}

	@Test
	public void testGetLicense_InternalError() {
		LicenseManager mgr = new LicenseManagerImpl(dataSource, factory);

		when(dataSource.findByNumber(anyString())).thenThrow(new NullPointerException("oops"));

		LicenseRequest req = new LicenseRequest("testapp", "qwe123");
		req.addFingerprints(hwfp);

		LicenseResponse resp = mgr.getLicense(req);
		assertNotNull(resp);
		assertEquals(ResponseType.ERROR_INTERNAL_ERROR, resp.getType());
		assertNull(resp.getLicenseInformation());
	}

	@Test
	public void testActivateLicenseLicense_InternalError() {
		LicenseManager mgr = new LicenseManagerImpl(dataSource, factory);

		when(dataSource.findByNumber(anyString())).thenThrow(new NullPointerException("oops"));

		LicenseRequest req = new LicenseRequest("testapp", "qwe123");
		req.addFingerprints(hwfp);

		LicenseResponse resp = mgr.activateLicense(req);
		assertNotNull(resp);
		assertEquals(ResponseType.ERROR_INTERNAL_ERROR, resp.getType());
		assertNull(resp.getLicenseInformation());
	}

	@Test
	public void testActivatetLicense_OkCreated() {
		LicenseManager mgr = new LicenseManagerImpl(dataSource, factory);

		LicenseRequest req = new LicenseRequest("testapp", "qwe123");
		req.addFingerprints(hwfp);

		License licMock = mock(License.class);
		when(licMock.isActive()).thenReturn(true);
		when(licMock.getNumber()).thenReturn("qwe123");
		when(licMock.getMaxActivations()).thenReturn(null);
		when(licMock.isAllowNewActivations()).thenReturn(true);

		when(dataSource.findByNumber(eq("qwe123"))).thenReturn(licMock);
		when(dataSource.findFeaturesForLicense(licMock)).thenReturn(Collections.singletonList(MOCK_FEAURE));

		Activation actiMock = mock(Activation.class);
		when(actiMock.getFingerprints()).thenReturn(hwfp);

		when(factory.createActivation(hwfp)).thenReturn(actiMock);

		LicenseResponse resp = mgr.activateLicense(req);

		assertNotNull(resp);
		assertEquals(ResponseType.OK_NEW_CREATED, resp.getType());
		assertNotNull(resp.getLicenseInformation());

		LicenseInformation info = resp.getLicenseInformation();
		assertEquals("qwe123", info.getLicenseNumber());
		assertEquals(features, info.getFeatures());
		assertEquals(new HashSet<HardwareFingerprint>(hwfp), info.getFingerPrints());
	}

	@Test
	public void testActivatetLicense_ExistingVerified() {
		LicenseManager mgr = new LicenseManagerImpl(dataSource, factory);

		LicenseRequest req = new LicenseRequest("testapp", "qwe123");
		req.addFingerprints(hwfp);

		License licMock = mock(License.class);
		when(licMock.isActive()).thenReturn(true);
		when(licMock.getNumber()).thenReturn("qwe123");
		when(licMock.getMaxActivations()).thenReturn(null);
		when(licMock.isAllowNewActivations()).thenReturn(true);

		Activation actiMock = mock(Activation.class);
		when(actiMock.getFingerprints()).thenReturn(hwfp);

		when(dataSource.findByNumber(eq("qwe123"))).thenReturn(licMock);
		when(dataSource.findFeaturesForLicense(licMock)).thenReturn(Collections.singletonList(MOCK_FEAURE));
		when(dataSource.findActiveActivationForLicense(licMock, hwfp)).thenReturn(actiMock);

		when(factory.createActivation(hwfp)).thenReturn(actiMock);

		LicenseResponse resp = mgr.activateLicense(req);

		assertNotNull(resp);
		assertEquals(ResponseType.OK_EXISTING_VERIFIED, resp.getType());
		assertNotNull(resp.getLicenseInformation());

		LicenseInformation info = resp.getLicenseInformation();
		assertEquals("qwe123", info.getLicenseNumber());
		assertEquals(features, info.getFeatures());
		assertEquals(new HashSet<HardwareFingerprint>(hwfp), info.getFingerPrints());
	}
}
