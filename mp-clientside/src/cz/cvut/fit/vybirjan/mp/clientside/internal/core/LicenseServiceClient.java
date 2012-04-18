package cz.cvut.fit.vybirjan.mp.clientside.internal.core;

import java.io.IOException;
import java.util.List;

import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseResponse;

/**
 * Interface to abstract communication with license server.
 * 
 * @author Jan Vybíral
 * 
 */
public interface LicenseServiceClient {

	LicenseResponse activateLicense(String licenseNumber, List<HardwareFingerprint> fingerprints) throws IOException;

	LicenseResponse getLicense(String licenseNumber, List<HardwareFingerprint> fingerprints) throws IOException;

}
