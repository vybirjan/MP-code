package cz.cvut.fit.vybirjan.mp.common.comm;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "request")
public class LicenseRequest implements Serializable {

	public LicenseRequest() {
	}

	public LicenseRequest(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	private static final long serialVersionUID = 1L;

	@XmlAttribute
	private String licenseNumber;

	@XmlElement(name = "fingerprint")
	private List<HardwareFingerprint> fingerprints;

	public String getLicenseNumber() {
		return licenseNumber;
	}

	public void addFingerprint(HardwareFingerprint fp) {
		if (fingerprints == null) {
			fingerprints = new LinkedList<HardwareFingerprint>();
		}

		fingerprints.add(fp);
	}

	public void removeFingerprint(HardwareFingerprint fp) {
		if (fingerprints != null) {
			fingerprints.remove(fp);
		}
	}

	public List<HardwareFingerprint> getFingerprints() {
		if (fingerprints == null) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(fingerprints);
		}
	}

	public void addFingerprints(Iterable<? extends HardwareFingerprint> fingerprints) {
		for (HardwareFingerprint fp : fingerprints) {
			addFingerprint(fp);
		}
	}

}
