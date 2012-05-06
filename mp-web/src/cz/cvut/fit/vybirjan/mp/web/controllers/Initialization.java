package cz.cvut.fit.vybirjan.mp.web.controllers;

import java.io.Serializable;
import java.security.Key;
import java.util.Date;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.google.inject.Inject;

import cz.cvut.fit.vybirjan.mp.common.Utils;
import cz.cvut.fit.vybirjan.mp.common.crypto.FileEncryptor;
import cz.cvut.fit.vybirjan.mp.web.model.AssignedFeatureJDO;
import cz.cvut.fit.vybirjan.mp.web.model.EncryptionKeyJDO;
import cz.cvut.fit.vybirjan.mp.web.model.FeatureJDO;
import cz.cvut.fit.vybirjan.mp.web.model.LicenseJDO;

@Path("init")
public class Initialization {

	@PersistenceCapable
	public static class InitializationMade implements Serializable {

		private static final long serialVersionUID = 1L;

		@PrimaryKey
		@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
		private com.google.appengine.api.datastore.Key id;

		public com.google.appengine.api.datastore.Key getId() {
			return id;
		}

		public void setId(com.google.appengine.api.datastore.Key id) {
			this.id = id;
		}

	}

	@Inject
	public Initialization(PersistenceManagerFactory pmf) {
		this.pmf = pmf;
	}

	private final PersistenceManagerFactory pmf;

	@GET
	public String fillData() {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			Query q = pm.newQuery(InitializationMade.class);
			q.setUnique(true);
			if (q.execute() != null) {
				return "Already initialized";
			}

			// create key
			EncryptionKeyJDO key = new EncryptionKeyJDO();
			key.setAppId("mp-testapp");
			key.setPrivateKey(Utils.deserialize(
					Utils.decode("rO0ABXNyABRqYXZhLnNlY3VyaXR5LktleVJlcL35T7OImqVDAgAETAAJYWxnb3JpdGhtdAASTGphdmEvbGFuZy9TdHJpbmc7WwAHZW5jb2RlZHQAAltCTAAGZm9ybWF0cQB+AAFMAAR0eXBldAAbTGphdmEvc2VjdXJpdHkvS2V5UmVwJFR5cGU7eHB0AANSU0F1cgACW0Ks8xf4BghU4AIAAHhwAAACejCCAnYCAQAwDQYJKoZIhvcNAQEBBQAEggJgMIICXAIBAAKBgQCH6wvNamviS5taenB105zKdfuNAPlGQcabxSuki/QkX7OtZkJdjqo6ndIo+TQ5TWGXAXKredVfh3nRjeIfS8njJMQRfRJB9tEMrjOxoSRB1vZpU9WdLvUSlc5+L91cP9HXOovsjHXJJP2llghsMPVxZ920gBAiVDgkM4BzTaUrHQIDAQABAoGAHpLaxsxHlFHZklK0dWyyekBr413ytMEbAfMqTAtHUd9NNZYpG4558FGL8reL7c/gQe2Lc9MmEiURW/gEg3Gy6ddZhnh7I7Elw24hKZdfCQpJocNFCQKvJl74Uxg1Zb2ChVmlNMkQ9ZBU5CGWgXG/J09SeiCiOW4UKhYh/X3j5ykCQQDDn/rA2xCUYjJk7Xlr1/O5WGzf+fF0zKZ+PJnHHj7CaonChf9Ezi7exjGFAgpR5HGFYlv60JP6alIh9GNH81xjAkEAsd26P0vJA4YCDgNv8RN6g2axqWYjIt5A/TtgfiHSG8VfhqKW70T9wV0gzwaJvOGKABxd61DQ7J4h6MWeLVUyfwJAUES1dCqvC+Oa//S9DLYoiFWzHtgE7kMG0ed1xdEwOT/T1OB8lLPxYXF7YjMrHt0Yg32PdkPdZDP6DXqkviIqNwJAH/qUjOLMTlTes7RMgRSWd1+UW7egY5mAJ8dwLt1X0GRpK2S6LGTEFuruhipPw2Ttkd2HyVIrbg90W0yEhvQFcwJBAIDZFPBx0dwvii3/ifEmTkDfmjGRoeCKoPn9AVkIXHdmMKAS0OSdeC+cNMs0tQqjMDprEdT2HgwESik3UnCKLHl0AAZQS0NTIzh+cgAZamF2YS5zZWN1cml0eS5LZXlSZXAkVHlwZQAAAAAAAAAAEgAAeHIADmphdmEubGFuZy5FbnVtAAAAAAAAAAASAAB4cHQAB1BSSVZBVEU="),
					Key.class));
			key.setPublicKey(Utils.deserialize(
					Utils.decode("rO0ABXNyABRqYXZhLnNlY3VyaXR5LktleVJlcL35T7OImqVDAgAETAAJYWxnb3JpdGhtdAASTGphdmEvbGFuZy9TdHJpbmc7WwAHZW5jb2RlZHQAAltCTAAGZm9ybWF0cQB+AAFMAAR0eXBldAAbTGphdmEvc2VjdXJpdHkvS2V5UmVwJFR5cGU7eHB0AANSU0F1cgACW0Ks8xf4BghU4AIAAHhwAAAAojCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAh+sLzWpr4kubWnpwddOcynX7jQD5RkHGm8UrpIv0JF+zrWZCXY6qOp3SKPk0OU1hlwFyq3nVX4d50Y3iH0vJ4yTEEX0SQfbRDK4zsaEkQdb2aVPVnS71EpXOfi/dXD/R1zqL7Ix1yST9pZYIbDD1cWfdtIAQIlQ4JDOAc02lKx0CAwEAAXQABVguNTA5fnIAGWphdmEuc2VjdXJpdHkuS2V5UmVwJFR5cGUAAAAAAAAAABIAAHhyAA5qYXZhLmxhbmcuRW51bQAAAAAAAAAAEgAAeHB0AAZQVUJMSUM="),
					Key.class));
			pm.makePersistent(key);

			// create features
			FeatureJDO basicFeature = new FeatureJDO("TEST-APP");
			basicFeature.setDescription("Feature required for test application to run");
			basicFeature.setTaggedKey(FileEncryptor.deserializeKey(Utils.decode("AAAAAQAAAANBRVOd4/09j8n0ZQJbfl00xd4I")));
			pm.makePersistent(basicFeature);

			FeatureJDO extendedFeature = new FeatureJDO("EXTENDED-FEATURE");
			extendedFeature.setDescription("Feature for extension plugin to work");
			extendedFeature.setTaggedKey(FileEncryptor.deserializeKey(Utils.decode("AAAAAgAAAANBRVM3Hlj15I5DJoSwgIwBxqVN")));
			pm.makePersistent(extendedFeature);

			LicenseJDO license = new LicenseJDO();
			license.setActive(true);
			license.setAllowedNewActivations(true);
			license.setDateIssued(new Date());
			license.setNumber("license-testapp");
			license.addFeature(new AssignedFeatureJDO(basicFeature));
			license.setDescription("License only for test application");
			pm.makePersistent(license);

			license = new LicenseJDO();
			license.setActive(true);
			license.setAllowedNewActivations(true);
			license.setDateIssued(new Date());
			license.setNumber("license-testapp+plugin");
			license.addFeature(new AssignedFeatureJDO(basicFeature));
			license.addFeature(new AssignedFeatureJDO(extendedFeature));
			license.setDescription("License for test appplication and plugin");
			pm.makePersistent(license);

			pm.makePersistent(new InitializationMade());

		} finally {
			pm.close();
		}

		return "Data imported";
	}

}
