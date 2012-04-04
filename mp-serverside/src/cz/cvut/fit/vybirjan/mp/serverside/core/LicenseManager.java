package cz.cvut.fit.vybirjan.mp.serverside.core;

import cz.cvut.fit.vybirjan.mp.common.comm.LicenseRequest;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseResponse;

public interface LicenseManager {

	LicenseResponse activateLicense(LicenseRequest request);

	LicenseResponse getLicense(LicenseRequest request);

}
