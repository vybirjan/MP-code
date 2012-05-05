package cz.cvut.fit.vybirjan.mp.serverside.core;

import cz.cvut.fit.vybirjan.mp.common.comm.LicenseRequest;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseResponse;

/**
 * Interface for delegating request processing to next handlers or license
 * manager itself.
 * 
 * @author Jan Vybíral
 * 
 */
public interface RequestProcessingContext {

	/**
	 * Call to delegate license activations to next handler in chain or license
	 * manger itself
	 * 
	 * @param request
	 *            Request sent from client
	 * @return Response on request
	 */
	LicenseResponse processActivateLicense(LicenseRequest request);

	/**
	 * Call to delegate reading license info to next handler in chain or license
	 * manger itself
	 * 
	 * @param request
	 *            Request sent from client
	 * @return Response on request
	 */
	LicenseResponse processGetExistingLicense(LicenseRequest request);

}
