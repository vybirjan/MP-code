package cz.cvut.fit.vybirjan.mp.clientside.internal.core;

import java.util.List;

import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseResponse;

public interface LicenseServiceClient {

	LicenseResponse requestLicense(String licenseNumber, List<HardwareFingerprint> fingerprints);

}
