package cz.cvut.fit.vybirjan.mp.clientside;

public class LicenseCheckException extends Exception {

	public static enum LicenseCheckErrorType {
		NOT_FOUND,
		EXPIRED,
		INVALID
	}

	private static final long serialVersionUID = 1L;

	public LicenseCheckException(LicenseCheckErrorType error) {
		this.error = error;
	}

	private final LicenseCheckErrorType error;

	public LicenseCheckErrorType getErrorType() {
		return error;
	}

}
