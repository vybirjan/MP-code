package cz.cvut.fit.vybirjan.mp.serverside.core;

import cz.cvut.fit.vybirjan.mp.common.comm.LicenseRequest;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseResponse;

/**
 * Interface for handling license activations
 * 
 * @author Jan Vybíral
 * 
 */
public interface LicenseManager {

	/**
	 * Tries to create new activation based on request. Might return data about
	 * existing activation instead of creating new one.
	 * 
	 * @param request
	 * @return Information about newly created activation, or error report
	 */
	LicenseResponse activateLicense(LicenseRequest request);

	/**
	 * Returns information about license activation. Does not create new
	 * activation.
	 * 
	 * @param request
	 * @return Information about existing activation, or error report
	 */
	LicenseResponse getLicense(LicenseRequest request);

}
