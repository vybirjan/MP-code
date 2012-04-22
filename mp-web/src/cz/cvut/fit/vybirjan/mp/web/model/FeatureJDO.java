package cz.cvut.fit.vybirjan.mp.web.model;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import cz.cvut.fit.vybirjan.mp.common.Utils;
import cz.cvut.fit.vybirjan.mp.common.crypto.FileEncryptor;
import cz.cvut.fit.vybirjan.mp.common.crypto.TaggedKey;

@PersistenceCapable
public class FeatureJDO {

	public FeatureJDO(String code) {
		this.code = code;
		this.id = KeyFactory.createKey(FeatureJDO.class.getSimpleName(), code);
	}

	protected FeatureJDO() {

	}

	@PrimaryKey
	private Key id;
	@Persistent
	private String code;
	@Persistent
	private String description;
	@Persistent
	private Blob taggedKey;

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public TaggedKey getKey() {
		return taggedKey == null ? null : FileEncryptor.deserializeKey(taggedKey.getBytes());
	}

	public Key getId() {
		return id;
	}

	public void setTaggedKey(TaggedKey key) {
		if (key == null) {
			taggedKey = null;
		} else {
			taggedKey = new Blob(FileEncryptor.serializeKey(key));
		}
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEncodedKey() {
		if (taggedKey == null) {
			return "";
		} else {
			return Utils.encode(taggedKey.getBytes());
		}
	}

}
