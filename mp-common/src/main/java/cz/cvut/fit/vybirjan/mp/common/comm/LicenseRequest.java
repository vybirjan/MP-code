package cz.cvut.fit.vybirjan.mp.common.comm;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class representing request to license server in order to obtain license.
 * 
 * @author Jan Vybíral
 * 
 */
@XmlRootElement(name = "request")
public class LicenseRequest implements Serializable {

	public LicenseRequest() {
	}

	public LicenseRequest(String appId, String licenseNumber) {
		this.licenseNumber = licenseNumber;
		this.applicationId = appId;
	}

	private static final long serialVersionUID = 1L;

	@XmlAttribute
	private String applicationId;

	@XmlAttribute
	private String licenseNumber;

	@XmlElement(name = "fingerprint")
	private List<HardwareFingerprint> fingerprints;

	/**
	 * Number of licsense to obtain
	 * 
	 * @return
	 */
	public String getLicenseNumber() {
		return licenseNumber;
	}

	/**
	 * Adds fingerprint to this request
	 * 
	 * @param fp
	 */
	public void addFingerprint(HardwareFingerprint fp) {
		if (fingerprints == null) {
			fingerprints = new LinkedList<HardwareFingerprint>();
		}

		fingerprints.add(fp);
	}

	/**
	 * Removes fingerprint from this request
	 * 
	 * @param fp
	 */
	public void removeFingerprint(HardwareFingerprint fp) {
		if (fingerprints != null) {
			fingerprints.remove(fp);
		}
	}

	/**
	 * Returns unmodifiable collection of fingerprintsfrom this request.
	 * 
	 * @return
	 */
	public List<HardwareFingerprint> getFingerprints() {
		if (fingerprints == null) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(fingerprints);
		}
	}

	/**
	 * Adds all fingerprints to this request.
	 * 
	 * @param fingerprints
	 */
	public void addFingerprints(Iterable<? extends HardwareFingerprint> fingerprints) {
		for (HardwareFingerprint fp : fingerprints) {
			addFingerprint(fp);
		}
	}

	/**
	 * Identifiaction of application which made request. Used to find
	 * appropriate key to sign response.
	 * 
	 * @return
	 */
	public String getApplicationIdx() {
		return applicationId;
	}

	/**
	 * Sets identifiaction of application which made request. Used to find
	 * appropriate key to sign response.
	 * 
	 * @return
	 */
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
}
