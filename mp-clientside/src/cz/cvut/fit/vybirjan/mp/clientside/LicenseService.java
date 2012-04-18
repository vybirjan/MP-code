package cz.cvut.fit.vybirjan.mp.clientside;

import java.io.IOException;
import java.security.Key;

import cz.cvut.fit.vybirjan.mp.clientside.LicenseCheckException.LicenseCheckErrorType;
import cz.cvut.fit.vybirjan.mp.clientside.internal.client.RESTServiceClient;
import cz.cvut.fit.vybirjan.mp.clientside.internal.core.HardwareFingerprintProvider;
import cz.cvut.fit.vybirjan.mp.clientside.internal.core.LicenseServiceClient;
import cz.cvut.fit.vybirjan.mp.clientside.internal.core.SecureStorage;
import cz.cvut.fit.vybirjan.mp.clientside.internal.fingerprints.HardwareFingerprintProviderFactory;
import cz.cvut.fit.vybirjan.mp.clientside.internal.hook.SecurityHook;
import cz.cvut.fit.vybirjan.mp.clientside.internal.storage.EquinoxSecureStorage;
import cz.cvut.fit.vybirjan.mp.common.Utils;
import cz.cvut.fit.vybirjan.mp.common.comm.Feature;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseInformation;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseResponse;

/**
 * Class used by desktop application to access information about license and to
 * request new license.
 * 
 * @author Jan Vybíral
 * 
 */
public class LicenseService {

	private static class InstanceHolder {

		private static LicenseService INSTANCE = createInstance();

	}

	/**
	 * Returns instance of service
	 * 
	 * @return
	 */
	public static LicenseService getInstance() {
		return InstanceHolder.INSTANCE;
	}

	private static LicenseService createInstance() {
		return new LicenseService(new EquinoxSecureStorage(), new RESTServiceClient("testapp", "localhost:8888/license/", false),
				HardwareFingerprintProviderFactory.getProvider());
	}

	public LicenseService(SecureStorage storage, LicenseServiceClient serviceClient, HardwareFingerprintProvider fingerprintProvider) {
		this.storage = storage;
		this.serviceClient = serviceClient;
		this.fingerprintProvider = fingerprintProvider;
	}

	private final Object licenseLock = new Object();
	private LicenseInformation currentLicense;

	private final SecureStorage storage;
	private final LicenseServiceClient serviceClient;
	private final HardwareFingerprintProvider fingerprintProvider;

	private LicenseInformation getCurrentInternal() {
		LicenseInformation tmpLicense = null;
		synchronized (licenseLock) {
			tmpLicense = currentLicense;
		}

		if (tmpLicense == null) {
			try {
				tmpLicense = storage.loadInfo();
			} catch (IOException e) {
				// log
			}

			synchronized (licenseLock) {
				currentLicense = tmpLicense;
			}
			if (currentLicense != null) {
				SecurityHook.addKeys(currentLicense.getKeys());
			}
		}

		return currentLicense;
	}

	/**
	 * Returns current license information, or null if no license information is
	 * available
	 * 
	 * @return
	 */
	public LicenseInformation getCurrent() {
		LicenseInformation info = getCurrentInternal();
		return info == null ? null : (LicenseInformation) info.clone();
	}

	private void setCurrentLicense(LicenseInformation info) {
		synchronized (licenseLock) {
			currentLicense = info;
		}

		try {
			storage.save(info);
		} catch (IOException e) {
			// log
		}

		SecurityHook.clearKeys();
		if (info != null) {
			SecurityHook.addKeys(currentLicense.getKeys());
		}
	}

	/**
	 * Clears current license. Any consecutive calls to {@link #getCurrent()}
	 * will return null.
	 */
	public void clearCurrentLicense() {
		synchronized (licenseLock) {
			currentLicense = null;
		}

		try {
			storage.clear();
		} catch (IOException e) {
			// damn
		}

		SecurityHook.clearKeys();
	}

	/**
	 * Requests activation of license. If this method succeedes, any consecutive
	 * calls to {@link #getCurrent()} will, return obtained license.
	 * 
	 * @param licenseNumber
	 *            Number of license to obtain from server
	 * @return Information about obtained license
	 * @throws LicenseRetrieveException
	 *             Thrown when obtaining license from server fails
	 * @throws LicenseCheckException
	 *             Thrown when validation of obtained license fails
	 * @throws IOException
	 *             Thrown when connection to license server could not be
	 *             established
	 */
	public LicenseInformation activateLicense(String licenseNumber) throws LicenseRetrieveException, LicenseCheckException, IOException {
		LicenseResponse response = serviceClient.activateLicense(licenseNumber, fingerprintProvider.collectFingerprints());

		if (response.isOk()) {
			LicenseInformation info = response.getLicenseInformation();
			checkLicense(info);
			setCurrentLicense(info);
			return getCurrent();
		} else {
			throw new LicenseRetrieveException(response.getType());
		}
	}

	/**
	 * Checks current license without connecting to central server.
	 * 
	 * @return Current license information.
	 * @throws LicenseCheckException
	 *             Thrown when checking current license fails.
	 */
	public LicenseInformation checkOffline() throws LicenseCheckException {
		try {
			return (LicenseInformation) checkLicense(getCurrentInternal()).clone();
		} catch (LicenseCheckException e) {
			clearCurrentLicense();
			throw e;
		}
	}

	/**
	 * Connects to server and verifies current license.
	 * 
	 * @return License information obtained from server.
	 * @throws LicenseCheckException
	 *             Thrown when checking license fails
	 * @throws LicenseRetrieveException
	 *             Thrown when retireving license from server fails
	 * @throws IOException
	 *             Thrown when connection to license server could not be
	 *             established
	 */
	public LicenseInformation checkOnline() throws LicenseCheckException, LicenseRetrieveException, IOException {
		try {
			LicenseInformation info = getCurrentInternal();
			if (info == null) {
				throw new LicenseCheckException(LicenseCheckErrorType.NOT_FOUND);
			}

			LicenseResponse response = serviceClient.getLicense(info.getLicenseNumber(), fingerprintProvider.collectFingerprints());
			if (response.isOk()) {
				info = response.getLicenseInformation();
				checkLicense(info);
				setCurrentLicense(info);
				return (LicenseInformation) info.clone();
			} else {
				clearCurrentLicense();
				throw new LicenseRetrieveException(response.getType());
			}

		} catch (LicenseCheckException e) {
			clearCurrentLicense();
			throw e;
		}
	}

	private LicenseInformation checkLicense(LicenseInformation license) throws LicenseCheckException {
		if (!license.isSigned() || !license.verify(getKey())) {
			throw new LicenseCheckException(LicenseCheckErrorType.INVALID);
		}

		boolean validFeatures = false;
		for (Feature f : license.getFeatures()) {
			if (Utils.isValid(f.getValidFrom(), f.getValidTo())) {
				validFeatures = true;
				break;
			}
		}

		// no valid features
		if (!validFeatures) {
			throw new LicenseCheckException(LicenseCheckErrorType.EXPIRED);
		}

		return license;
	}

	static Key k = Utils
			.deserialize(
					Utils.decode("rO0ABXNyABRqYXZhLnNlY3VyaXR5LktleVJlcL35T7OImqVDAgAETAAJYWxnb3JpdGhtdAASTGphdmEvbGFuZy9TdHJpbmc7WwAHZW5jb2RlZHQAAltCTAAGZm9ybWF0cQB+AAFMAAR0eXBldAAbTGphdmEvc2VjdXJpdHkvS2V5UmVwJFR5cGU7eHB0AANSU0F1cgACW0Ks8xf4BghU4AIAAHhwAAAAojCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAmNsTaNbGZtc0UAtN8q73uW2Bpn7Zfqq/IsxEyexCVj/RvMGKuKIG+ysoyYRXnNV1xga19AZxdmYycCbRzp25TxvY6waUJdg9fdkY2ChTHXWvwzQzKHKTTMTJEBV9QFJ3udBY++BXojcB+U4/RRK0wz12AsaYSqiwWa0cSULWH50CAwEAAXQABVguNTA5fnIAGWphdmEuc2VjdXJpdHkuS2V5UmVwJFR5cGUAAAAAAAAAABIAAHhyAA5qYXZhLmxhbmcuRW51bQAAAAAAAAAAEgAAeHB0AAZQVUJMSUM="),
					Key.class);

	private Key getKey() {
		return k;
	}

}
