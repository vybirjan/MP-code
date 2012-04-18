package cz.cvut.fit.vybirjan.mp.web.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class EncryptionKeyJDO {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	@Persistent
	private String appId;
	@Persistent
	private String publicKey;
	@Persistent
	private String privateKey;

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
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

}
