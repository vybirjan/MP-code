package cz.cvut.fit.vybirjan.mp.web.model;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

import cz.cvut.fit.vybirjan.mp.serverside.domain.License;

@PersistenceCapable
public class LicenseJDO implements License {

	public static LicenseJDO findByNumber(String number, PersistenceManager pm) {
		Query q = pm.newQuery(LicenseJDO.class);
		q.setFilter("number == nameParam");
		q.declareParameters("String nameParam");
		q.setUnique(true);
		return (LicenseJDO) q.execute(number);
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	private boolean active;
	@Persistent
	private boolean allowedNewActivations;
	@Persistent
	private String description;
	@Persistent
	private int maxActivation;
	@Persistent
	private String number;
	@Persistent
	private Date validFrom;
	@Persistent
	private Date validTo;
	@Persistent(mappedBy = "license")
	private List<ActivationJDO> activations;
	@Persistent(mappedBy = "license", defaultFetchGroup = "true")
	private List<FeatureJDO> features;

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public boolean isAllowNewActivations() {
		return allowedNewActivations;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public int getMaxActivations() {
		return maxActivation;
	}

	@Override
	public String getNumber() {
		return number;
	}

	@Override
	public Date getValidFrom() {
		return validFrom;
	}

	@Override
	public Date getValidTo() {
		return validTo;
	}

	public Key getId() {
		return id;
	}

	public List<ActivationJDO> getActivations() {
		if (activations == null) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(activations);
		}
	}

	public void addActivation(ActivationJDO a) {
		if (activations == null) {
			activations = new LinkedList<ActivationJDO>();
		}

		activations.add(a);
		a.setLicense(this);
	}

	public void removeActivation(ActivationJDO a) {
		if (activations != null) {
			if (activations.remove(a)) {
				a.setLicense(null);
			}
		}
	}

	public List<FeatureJDO> getFeatures() {
		if (features == null) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(features);
		}
	}

	public void addFeature(FeatureJDO f) {
		if (features == null) {
			features = new LinkedList<FeatureJDO>();
		}

		features.add(f);
		f.setLicense(this);
	}

	public void removeFeature(FeatureJDO f) {
		if (features != null) {
			if (features.remove(f)) {
				f.setLicense(null);
			}
		}
	}

	public void setId(Key id) {
		this.id = id;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setAllowedNewActivations(boolean allowedNewActivations) {
		this.allowedNewActivations = allowedNewActivations;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setMaxActivation(int maxActivation) {
		this.maxActivation = maxActivation;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

}
