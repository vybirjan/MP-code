package cz.cvut.fit.vybirjan.mp.web.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;

import cz.cvut.fit.vybirjan.mp.common.crypto.FileEncryptor;
import cz.cvut.fit.vybirjan.mp.common.crypto.TaggedKey;
import cz.cvut.fit.vybirjan.mp.serverside.domain.Feature;

@PersistenceCapable
public class AssignedFeatureJDO implements Feature, Serializable {

	private static final long serialVersionUID = 1L;

	public AssignedFeatureJDO(FeatureJDO feature) {
		this.code = feature.getCode();
		this.description = feature.getDescription();
		TaggedKey key = feature.getKey();
		if (key != null) {
			taggedKey = new Blob(FileEncryptor.serializeKey(key));
		}
	}

	protected AssignedFeatureJDO() {

	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	@Persistent(defaultFetchGroup = "true")
	private LicenseJDO license;
	@Persistent
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
	private Date validFrom;
	@Persistent
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
	private Date validTo;
	// fields from feature
	@Persistent
	private String code;
	@Persistent
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
	private String description;
	@Persistent
	private Blob taggedKey;

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
	public TaggedKey getKey() {
		return taggedKey == null ? null : FileEncryptor.deserializeKey(taggedKey.getBytes());
	}

	void setLicense(LicenseJDO license) {
		this.license = license;
	}

	public LicenseJDO getLicense() {
		return license;
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
