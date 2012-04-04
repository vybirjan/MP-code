package cz.cvut.fit.vybirjan.mp.serverside.core;

import cz.cvut.fit.vybirjan.mp.common.comm.LicenseRequest;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseResponse;

public interface LicenseEventHandler {

	LicenseResponse handleActivateLicense(LicenseRequest request, RequestProcessingContext context);

	LicenseResponse handleGetExistingLicense(LicenseRequest request, RequestProcessingContext context);

}
