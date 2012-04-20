package cz.cvut.fit.vybirjan.mp.web.model;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import cz.cvut.fit.vybirjan.mp.common.crypto.TaggedKey;
import cz.cvut.fit.vybirjan.mp.serverside.domain.Feature;

@PersistenceCapable
public class AssignedFeatureJDO implements Feature {

	public AssignedFeatureJDO(FeatureJDO feature) {
		this.feature = feature;
	}

	protected AssignedFeatureJDO() {

	}

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
}
