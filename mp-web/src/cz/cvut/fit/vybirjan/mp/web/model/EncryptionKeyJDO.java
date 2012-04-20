package cz.cvut.fit.vybirjan.mp.web.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;

import cz.cvut.fit.vybirjan.mp.common.Utils;

@PersistenceCapable
public class EncryptionKeyJDO {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	@Persistent
	private String appId;
	@Persistent
	private Blob publicKey;
	@Persistent
	private Blob privateKey;

	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getPublicKey() {
		return publicKey == null ? null : Utils.encode(publicKey.getBytes());
	}

	public void setPublicKey(java.security.Key publicKey) {
		if (publicKey == null) {
			this.publicKey = null;
		} else {
			this.publicKey = new Blob((Utils.serialize(publicKey)));
		}
	}

	public String getPrivateKey() {
		return privateKey == null ? null : Utils.encode(privateKey.getBytes());
	}

	public void setPrivateKey(java.security.Key privateKey) {
		if (privateKey == null) {
			this.privateKey = null;
		} else {
			this.privateKey = new Blob(Utils.serialize(privateKey));
		}
	}

	public java.security.Key deserializePublic() {
		if (publicKey == null) {
			return null;
		} else {
			return Utils.deserialize(publicKey.getBytes(), java.security.Key.class);
		}
	}

	public java.security.Key deserializePrivate() {
		if (privateKey == null) {
			return null;
		} else {
			return Utils.deserialize(privateKey.getBytes(), java.security.Key.class);
		}
	}

}
