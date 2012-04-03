package cz.cvut.fit.vybirjan.mp.common.comm;

import java.io.Serializable;

public class LicenseResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum ResponseType {
		OK_VERIFIED,
		OK_CREATED,
		ERROR_SERVER_ERROR,
		ERROR_TOO_MANY_ACTIVATIONS,
		ERROR_NOT_FOUND
	}

	private ResponseType type;
	private LicenseInformation licenseInformation;

	public ResponseType getType() {
		return type;
	}

	public LicenseInformation getLicenseInformation() {
		return licenseInformation;
	}
}
