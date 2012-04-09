package cz.cvut.fit.vybirjan.mp.clientside;

import cz.cvut.fit.vybirjan.mp.common.comm.ResponseType;

public class LicenseRetrieveException extends Exception {

	private static final long serialVersionUID = 1L;

	public LicenseRetrieveException(ResponseType type) {
		this.response = type;
	}

	private final ResponseType response;
}
