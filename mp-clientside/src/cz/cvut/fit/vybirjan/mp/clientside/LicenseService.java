package cz.cvut.fit.vybirjan.mp.clientside;

import java.io.IOException;
import java.security.Key;

import cz.cvut.fit.vybirjan.mp.clientside.LicenseCheckException.LicenseCheckErrorType;
import cz.cvut.fit.vybirjan.mp.clientside.internal.client.RESTServiceClient;
import cz.cvut.fit.vybirjan.mp.clientside.internal.core.HardwareFingerprintProvider;
import cz.cvut.fit.vybirjan.mp.clientside.internal.core.LicenseServiceClient;
import cz.cvut.fit.vybirjan.mp.clientside.internal.core.SecureStorage;
import cz.cvut.fit.vybirjan.mp.clientside.internal.fingerprints.HardwareFingerprintProviderFactory;
import cz.cvut.fit.vybirjan.mp.clientside.internal.storage.EquinoxSecureStorage;
import cz.cvut.fit.vybirjan.mp.common.Utils;
import cz.cvut.fit.vybirjan.mp.common.comm.Feature;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseInformation;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseResponse;

public class LicenseService {

	private static class InstanceHolder {

		private static LicenseService INSTANCE = createInstance();

	}

	public static LicenseService getInstance() {
		return InstanceHolder.INSTANCE;
	}

	private static LicenseService createInstance() {
		return new LicenseService(new EquinoxSecureStorage(), new RESTServiceClient("test", false), HardwareFingerprintProviderFactory.getProvider());
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
		}

		return currentLicense;
	}

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
	}

	public void clearCurrentLicense() {
		synchronized (licenseLock) {
			currentLicense = null;
		}

		try {
			storage.clear();
		} catch (IOException e) {
			// damn
		}
	}

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

	public LicenseInformation checkOffline() throws LicenseCheckException {
		try {
			return (LicenseInformation) checkLicense(getCurrentInternal()).clone();
		} catch (LicenseCheckException e) {
			clearCurrentLicense();
			throw e;
		}
	}

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

	private Key getKey() {
		return null; // TODO get key from somewhere
	}

}
