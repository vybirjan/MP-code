package cz.cvut.fit.vybirjan.mp.common.comm;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class containing data sent from server as response to {@link LicenseRequest}
 * sent by client.
 * 
 * @author Jan Vybíral
 * 
 */
@XmlRootElement(name = "response")
public class LicenseResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "type")
	private ResponseType type;
	@XmlElement(name = "licenseInfo")
	private LicenseInformation licenseInformation;

	private LicenseResponse() {

	}

	private LicenseResponse(ResponseType type, LicenseInformation licenseInfo) {
		this.type = type;
		this.licenseInformation = licenseInfo;
	}

	/**
	 * Returns type of response
	 * 
	 * @return
	 */
	public ResponseType getType() {
		return type;
	}

	/**
	 * Returns information about requested license, or null if request failed
	 * for some reason.
	 * 
	 * @return
	 */
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

	public static LicenseResponse communicationError() {
		return new LicenseResponse(ResponseType.ERROR_COMMUNICATION_ERROR, null);
	}

	public static LicenseResponse tooManyActivations() {
		return new LicenseResponse(ResponseType.ERROR_TOO_MANY_ACTIVATIONS, null);
	}

	public static LicenseResponse notActivated() {
		return new LicenseResponse(ResponseType.ERROR_NOT_ACTIVATED, null);
	}

	/**
	 * Returns true if request was successful and license information can be
	 * read using {@link #getLicenseInformation()} method.
	 * 
	 * @return
	 */
	public boolean isOk() {
		return (type == ResponseType.OK_EXISTING_VERIFIED || type == ResponseType.OK_NEW_CREATED) && licenseInformation != null;
	}
}
