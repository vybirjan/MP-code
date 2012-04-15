package cz.cvut.fit.vybirjan.mp.web.model;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;
import cz.cvut.fit.vybirjan.mp.serverside.domain.Activation;

@PersistenceCapable
public class ActivationJDO implements Activation {

	public static ActivationJDO findActiveForLicense(LicenseJDO license, List<HardwareFingerprint> fingerprints, PersistenceManager pm) {
		Query q = pm.newQuery(ActivationJDO.class);
		q.setUnique(true);
		q.setFilter("license == licenseParam && active == true && serializedFingerprints == fingerprintsParam");
		q.declareParameters(LicenseJDO.class.getName() + " licenseParam, " +
				"String fingerprintsParam");
		return (ActivationJDO) q.execute(license, HardwareFingerprint.toMultiString(fingerprints));
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	@Persistent
	private boolean active;
	@Persistent
	private Date dateCreated;
	@Persistent
	private String serializedFingerprints;
	@Persistent
	private LicenseJDO license;

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public Date getDateCreated() {
		return dateCreated;
	}

	@Override
	public List<HardwareFingerprint> getFingerprints() {
		if (serializedFingerprints == null) {
			return Collections.emptyList();
		} else {
			return HardwareFingerprint.fromMultiString(serializedFingerprints);
		}
	}

	public void setFingerprints(List<HardwareFingerprint> fingerprints) {
		if (fingerprints == null || fingerprints.isEmpty()) {
			serializedFingerprints = null;
		} else {
			serializedFingerprints = HardwareFingerprint.toMultiString(fingerprints);
		}
	}

	public LicenseJDO getLicense() {
		return license;
	}

	void setLicense(LicenseJDO license) {
		this.license = license;
	}

	public Key getId() {
		return id;
	}

	public String getSerializedFingerprints() {
		return serializedFingerprints;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
}
