package cz.cvut.fit.vybirjan.mp.web.model;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

import cz.cvut.fit.vybirjan.mp.common.crypto.TaggedKey;
import cz.cvut.fit.vybirjan.mp.serverside.domain.Feature;

@PersistenceCapable
public class AssignedFeatureJDO implements Feature {

	public AssignedFeatureJDO(FeatureJDO feature) {
		this.feature = feature;
	}

	protected AssignedFeatureJDO() {

	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	@Persistent(defaultFetchGroup = "true")
	private FeatureJDO feature;
	@Persistent(defaultFetchGroup = "true")
	private LicenseJDO license;
	@Persistent
	private Date validFrom;
	@Persistent
	private Date validTo;

	@Override
	public String getCode() {
		return feature.getCode();
	}

	@Override
	public String getDescription() {
		return feature.getDescription();
	}

	@Override
	public Date getValidFrom() {
		return validFrom;
	}

	@Override
	public Date getValidTo() {
		return validTo;
	}

	@Override
	public TaggedKey getKey() {
		return feature.getKey();
	}

	void setLicense(LicenseJDO license) {
		this.license = license;
	}

	public LicenseJDO getLicense() {
		return license;
	}

	public FeatureJDO getFeature() {
		return feature;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	public Key getId() {
		return id;
	}
}
