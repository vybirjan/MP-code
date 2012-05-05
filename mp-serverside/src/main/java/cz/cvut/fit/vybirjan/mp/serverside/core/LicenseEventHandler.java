package cz.cvut.fit.vybirjan.mp.serverside.core;

import cz.cvut.fit.vybirjan.mp.common.comm.LicenseRequest;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseResponse;

/**
 * <p>
 * Interface for extending default behavior of license manager.
 * </p>
 * 
 * <p>
 * Enables clients to modify request aon/or response before/after processing.
 * </p>
 * 
 * <p>
 * Reponse is signed after all handlers are called
 * </p>
 * 
 * @author Jan Vybíral
 * 
 */
public interface LicenseEventHandler {

	/**
	 * Called before license is activated. Clients can perform arbitrary
	 * modification upon request or response returned from
	 * {@link RequestProcessingContext#processActivateLicense(LicenseRequest)}.
	 * 
	 * @param request
	 *            Request sent to license manager
	 * @param context
	 *            Processing context for obtaining response from license manager
	 * @return Response to request
	 */
	LicenseResponse handleActivateLicense(LicenseRequest request, RequestProcessingContext context);

	/**
	 * Called before license info is read from license manager. Clients can
	 * perform arbitrary modification upon request or response returned from
	 * {@link RequestProcessingContext#processGetExistingLicense(LicenseRequest)}
	 * .
	 * 
	 * @param request
	 *            Request sent to license manager
	 * @param context
	 *            Processing context for obtaining response from license manager
	 * @return Response to request
	 */
	LicenseResponse handleGetExistingLicense(LicenseRequest request, RequestProcessingContext context);

}
