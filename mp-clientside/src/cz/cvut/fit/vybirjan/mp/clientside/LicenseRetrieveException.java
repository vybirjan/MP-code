package cz.cvut.fit.vybirjan.mp.clientside;

import cz.cvut.fit.vybirjan.mp.common.comm.ResponseType;

/**
 * Exeption thrown when retrieving license form server failed.
 * 
 * @author Jan Vybíral
 * 
 */
public class LicenseRetrieveException extends Exception {

	private static final long serialVersionUID = 1L;

	public LicenseRetrieveException(ResponseType type) {
		this.responseType = type;
	}

	private final ResponseType responseType;

	public ResponseType getResponseType() {
		return responseType;
	}
}
