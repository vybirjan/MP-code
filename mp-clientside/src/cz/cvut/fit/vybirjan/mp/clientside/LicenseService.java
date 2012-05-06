package cz.cvut.fit.vybirjan.mp.clientside;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseInformation;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseResponse;

/**
 * Class used by desktop application to access information about license and to
 * request new license.
 * 
 * @author Jan Vybíral
 * 
 */
public final class LicenseService {

	/**
	 * Listener notified when active license changes
	 * 
	 * @author Jan Vybíral
	 * 
	 */
	public static interface LicenseChangedListener {
		/**
		 * Called when active license changes
		 * 
		 * @param newInfo
		 *            New license information, or null
		 */
		void onLicenseChanged(LicenseInformation newInfo);

	}

	private static class InstanceHolder {

		private static LicenseService INSTANCE = createInstance();

	}

	public static void configure(LicenseServiceConfig config) {
		if (LicenseService.config == null) {
			LicenseService.config = config;
		} else {
			throw new IllegalStateException("Service already configured");
		}
	}

	private static LicenseServiceConfig config;

	/**
	 * Returns instance of service
	 * 
	 * @return
	 */
	public static LicenseService getInstance() {
		return InstanceHolder.INSTANCE;
	}

	private static LicenseService createInstance() {
		if (config == null) {
			throw new IllegalStateException("Service not configured");
		} else {
			return new LicenseService(new EquinoxSecureStorage(), new RESTServiceClient(config.getApplicationId(), config.getServiceBaseurl(),
					config.isUseEncryption()),
					HardwareFingerprintProviderFactory.getProvider());
		}
	}

	private LicenseService(SecureStorage storage, LicenseServiceClient serviceClient, HardwareFingerprintProvider fingerprintProvider) {
		this.storage = storage;
		this.serviceClient = serviceClient;
		this.fingerprintProvider = fingerprintProvider;
	}

	private final Object licenseLock = new Object();
	private LicenseInformation currentLicense;

	private final SecureStorage storage;
	private final LicenseServiceClient serviceClient;
	private final HardwareFingerprintProvider fingerprintProvider;
	private final List<LicenseChangedListener> licenseChangedListeners = new LinkedList<LicenseService.LicenseChangedListener>();

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

		notifyListeners(currentLicense);
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

		notifyListeners(null);
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
		if (!license.isSigned() || !license.verify(config.getEncryptionKey())) {
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

		List<HardwareFingerprint> fingerprints = fingerprintProvider.collectFingerprints();

		for (HardwareFingerprint fp : license.getFingerPrints()) {
			if (!fingerprints.contains(fp)) {
				throw new LicenseCheckException(LicenseCheckErrorType.FINGERPRINT_MISMATCH);
			}
		}

		return license;
	}

	/**
	 * Adds new license listener
	 * 
	 * @param listener
	 */
	public void addLicenseChangedListener(LicenseChangedListener listener) {
		synchronized (licenseChangedListeners) {
			licenseChangedListeners.add(listener);
		}
	}

	/**
	 * Removes license listener
	 * 
	 * @param listener
	 * @return
	 */
	public boolean removeLicenseChangedListener(LicenseChangedListener listener) {
		synchronized (licenseChangedListeners) {
			return licenseChangedListeners.remove(listener);
		}
	}

	private void notifyListeners(LicenseInformation info) {
		List<LicenseChangedListener> listeners = null;
		synchronized (licenseChangedListeners) {
			listeners = new ArrayList<LicenseService.LicenseChangedListener>(licenseChangedListeners);
		}

		for (LicenseChangedListener lisetner : listeners) {
			try {
				lisetner.onLicenseChanged((LicenseInformation) info.clone());
			} catch (Exception ignore) {
			}
		}
	}
}
