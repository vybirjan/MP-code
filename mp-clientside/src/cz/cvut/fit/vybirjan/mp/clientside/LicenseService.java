package cz.cvut.fit.vybirjan.mp.clientside;

import cz.cvut.fit.vybirjan.mp.common.comm.LicenseInformation;

public class LicenseService {

	public static LicenseService getInstance() {
		return new LicenseService();
	}

	public LicenseInformation getCurrent() {
		return null;
	}

	public LicenseInformation requestLicense(String licenseNumber) {
		return null;
	}

	public void checkOffline() {

	}

	public void checkOnline() {

	}
}
