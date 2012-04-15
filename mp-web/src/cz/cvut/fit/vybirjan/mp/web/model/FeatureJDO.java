package cz.cvut.fit.vybirjan.mp.web.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

import cz.cvut.fit.vybirjan.mp.common.crypto.TaggedKey;
import cz.cvut.fit.vybirjan.mp.serverside.domain.Feature;

@PersistenceCapable
public class FeatureJDO implements Feature {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	@Persistent
	private String code;
	@Persistent
	private String description;
	@Persistent
	private Date validFrom;
	@Persistent
	private Date validTo;
	@Persistent
	private LicenseJDO license;

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getDescription() {
		return description;
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
	public Collection<TaggedKey> getKeys() {
		// TODO Auto-generated method stub
		return Collections.emptyList();
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

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

}
