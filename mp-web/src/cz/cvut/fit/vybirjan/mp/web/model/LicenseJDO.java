package cz.cvut.fit.vybirjan.mp.web.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

import cz.cvut.fit.vybirjan.mp.serverside.domain.License;

@PersistenceCapable
public class LicenseJDO implements License, Serializable {

	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
	private boolean active;
	@Persistent
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
	private boolean allowedNewActivations;
	@Persistent
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
	private String description;
	@Persistent
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
	private Integer maxActivation;
	@Persistent
	private String number;
	@Persistent
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
	private Date validFrom;
	@Persistent
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
	private Date validTo;
	@Persistent
	private Date dateIssued;
	@Persistent(mappedBy = "license", defaultFetchGroup = "true")
	private List<ActivationJDO> activations;
	@Persistent(mappedBy = "license", defaultFetchGroup = "true")
	private List<AssignedFeatureJDO> features;

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
	public Integer getMaxActivations() {
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

	public List<AssignedFeatureJDO> getFeatures() {
		if (features == null) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(features);
		}
	}

	public void addFeature(AssignedFeatureJDO f) {
		if (features == null) {
			features = new LinkedList<AssignedFeatureJDO>();
		}

		features.add(f);
		f.setLicense(this);
	}

	public void removeFeature(AssignedFeatureJDO f) {
		if (features != null) {
			if (features.remove(f)) {
				f.setLicense(null);
			}
		}
	}

	public List<ActivationJDO> removeAllActivations() {
		if (activations == null) {
			return Collections.emptyList();
		} else {
			List<ActivationJDO> ret = new ArrayList<ActivationJDO>(activations);

			for (ActivationJDO activation : ret) {
				removeActivation(activation);
			}

			return ret;
		}
	}

	public List<AssignedFeatureJDO> removeAllFeatures() {
		if (features == null) {
			return Collections.emptyList();
		} else {
			List<AssignedFeatureJDO> ret = new ArrayList<AssignedFeatureJDO>(features);

			for (AssignedFeatureJDO feature : ret) {
				removeFeature(feature);
			}

			return ret;
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

	public void setMaxActivation(Integer maxActivation) {
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

	public Date getDateIssued() {
		return dateIssued;
	}

	public void setDateIssued(Date dateIssued) {
		this.dateIssued = dateIssued;
	}

	public AssignedFeatureJDO findByCode(String code) {
		if (features != null) {
			for (AssignedFeatureJDO feature : features) {
				if (feature.getCode().equals(code)) {
					return feature;
				}
			}
		}

		return null;
	}

	public ActivationJDO findActivaionById(long id) {
		for (ActivationJDO activation : activations) {
			if (activation.getId().getId() == id) {
				return activation;
			}
		}
		return null;
	}
}
