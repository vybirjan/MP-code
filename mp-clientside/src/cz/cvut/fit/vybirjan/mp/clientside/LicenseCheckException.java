package cz.cvut.fit.vybirjan.mp.clientside;

/**
 * Exception thrown during license validation.
 * 
 * @author Jan Vybíral
 * 
 */
public class LicenseCheckException extends Exception {

	public static enum LicenseCheckErrorType {
		/**
		 * No license to check was found
		 */
		NOT_FOUND,
		/**
		 * License is expired
		 */
		EXPIRED,
		/**
		 * License is not valid (invalid or missing signature)
		 */
		INVALID,
		/**
		 * License contains different fingerprints
		 */
		FINGERPRINT_MISMATCH
	}

	private static final long serialVersionUID = 1L;

	public LicenseCheckException(LicenseCheckErrorType error) {
		this.error = error;
	}

	private final LicenseCheckErrorType error;

	/**
	 * Returns type of error
	 * 
	 * @return
	 */
	public LicenseCheckErrorType getErrorType() {
		return error;
	}

}
