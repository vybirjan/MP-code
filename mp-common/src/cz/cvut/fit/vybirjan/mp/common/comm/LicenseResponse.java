package cz.cvut.fit.vybirjan.mp.common.comm;

import java.io.Serializable;

public class LicenseResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum ResponseType {
		OK_EXISTING_VERIFIED,
		OK_NEW_CREATED,
		ERROR_NEW_ACTIVATIONS_NOT_ALLOWED,
		ERROR_INACTIVE,
		ERROR_INTERNAL_ERROR,
		ERROR_TOO_MANY_ACTIVATIONS,
		ERROR_LICENSE_NOT_FOUND,
		ERROR_EXPIRED
	}

	private final ResponseType type;
	private final LicenseInformation licenseInformation;

	private LicenseResponse(ResponseType type, LicenseInformation licenseInfo) {
		this.type = type;
		this.licenseInformation = licenseInfo;
	}

	public ResponseType getType() {
		return type;
	}

	public LicenseInformation getLicenseInformation() {
		return licenseInformation;
	}

	public static LicenseResponse createdNew(LicenseInformation info) {
		return new LicenseResponse(ResponseType.OK_NEW_CREATED, info);
	}

	public static LicenseResponse foundExisting(LicenseInformation info) {
		return new LicenseResponse(ResponseType.OK_EXISTING_VERIFIED, info);
	}

	public static LicenseResponse internalError() {
		return new LicenseResponse(ResponseType.ERROR_INTERNAL_ERROR, null);
	}

	public static LicenseResponse expired() {
		return new LicenseResponse(ResponseType.ERROR_EXPIRED, null);
	}

	public static LicenseResponse licenseNotFound() {
		return new LicenseResponse(ResponseType.ERROR_LICENSE_NOT_FOUND, null);
	}

	public static LicenseResponse inactive() {
		return new LicenseResponse(ResponseType.ERROR_INACTIVE, null);
	}

	public static LicenseResponse newActivationsNotAllowed() {
		return new LicenseResponse(ResponseType.ERROR_NEW_ACTIVATIONS_NOT_ALLOWED, null);
	}
}
