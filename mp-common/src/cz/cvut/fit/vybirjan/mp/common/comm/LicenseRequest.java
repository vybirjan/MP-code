package cz.cvut.fit.vybirjan.mp.common.comm;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class LicenseRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private String licenseNumber;
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

}
