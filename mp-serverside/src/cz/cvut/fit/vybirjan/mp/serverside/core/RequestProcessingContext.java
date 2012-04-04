package cz.cvut.fit.vybirjan.mp.serverside.core;

import cz.cvut.fit.vybirjan.mp.common.comm.LicenseRequest;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseResponse;

public interface RequestProcessingContext {

	LicenseResponse processActivateLicense(LicenseRequest request);

	LicenseResponse processGetExistingLicense(LicenseRequest request);

}
