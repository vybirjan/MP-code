package cz.cvut.fit.vybirjan.mp.clientside.ui;

import org.eclipse.osgi.util.NLS;

import cz.cvut.fit.vybirjan.mp.clientside.LicenseCheckException.LicenseCheckErrorType;
import cz.cvut.fit.vybirjan.mp.common.comm.ResponseType;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "cz.cvut.fit.vybirjan.mp.clientside.ui.messages"; //$NON-NLS-1$
	public static String LicenseActivationDialog_Error_ConnectionFailed;
	public static String LicenseActivationDialog_Error_RetrieveFailed;
	public static String LicenseActivationDialog_Error_VerificationFailed;
	public static String LicenseActivationDialog_LicenseNumber;
	public static String LicenseActivationDialog_MissingRequiredFeatures;
	public static String LicenseActivationDialog_RetrieveingLicense;
	public static String LicenseCheckErrorType_EXPIRED;
	public static String LicenseCheckErrorType_FINGERPRINT_MISMATCH;
	public static String LicenseCheckErrorType_INVALID;
	public static String LicenseCheckErrorType_NOT_FOUND;
	public static String ResponseType_ERROR_BAD_REQUEST;
	public static String ResponseType_ERROR_COMMUNICATION_ERROR;
	public static String ResponseType_ERROR_EXPIRED;
	public static String ResponseType_ERROR_INACTIVE;
	public static String ResponseType_ERROR_INTERNAL_ERROR;
	public static String ResponseType_ERROR_LICENSE_NOT_FOUND;
	public static String ResponseType_ERROR_NEW_ACTIVATIONS_NOT_ALLOWED;
	public static String ResponseType_ERROR_NOT_ACTIVATED;
	public static String ResponseType_ERROR_TOO_MANY_ACTIVATIONS;
	public static String ResponseType_OK_EXISTING_VERIFIED;
	public static String ResponseType_OK_NEW_CREATED;

	public static String getMessageForResponseType(ResponseType type) {
		switch (type) {
			case ERROR_BAD_REQUEST:
				return Messages.ResponseType_ERROR_BAD_REQUEST;
			case ERROR_COMMUNICATION_ERROR:
				return Messages.ResponseType_ERROR_COMMUNICATION_ERROR;
			case ERROR_EXPIRED:
				return Messages.ResponseType_ERROR_EXPIRED;
			case ERROR_INACTIVE:
				return Messages.ResponseType_ERROR_INACTIVE;
			case ERROR_INTERNAL_ERROR:
				return Messages.ResponseType_ERROR_INTERNAL_ERROR;
			case ERROR_LICENSE_NOT_FOUND:
				return Messages.ResponseType_ERROR_LICENSE_NOT_FOUND;
			case ERROR_NEW_ACTIVATIONS_NOT_ALLOWED:
				return Messages.ResponseType_ERROR_NEW_ACTIVATIONS_NOT_ALLOWED;
			case ERROR_NOT_ACTIVATED:
				return Messages.ResponseType_ERROR_NOT_ACTIVATED;
			case ERROR_TOO_MANY_ACTIVATIONS:
				return Messages.ResponseType_ERROR_TOO_MANY_ACTIVATIONS;
			case OK_EXISTING_VERIFIED:
				return Messages.ResponseType_OK_EXISTING_VERIFIED;
			case OK_NEW_CREATED:
				return Messages.ResponseType_OK_NEW_CREATED;
			default:
				return type.toString();
		}
	}

	public static String getMessageForCheckError(LicenseCheckErrorType type) {
		switch (type) {
			case EXPIRED:
				return Messages.LicenseCheckErrorType_EXPIRED;
			case FINGERPRINT_MISMATCH:
				return Messages.LicenseCheckErrorType_FINGERPRINT_MISMATCH;
			case INVALID:
				return Messages.LicenseCheckErrorType_INVALID;
			case NOT_FOUND:
				return Messages.LicenseCheckErrorType_NOT_FOUND;
			default:
				return type.toString();
		}
	}

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
