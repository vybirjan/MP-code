package cz.cvut.fit.vybirjan.mp.clientside;

import java.security.Key;

/**
 * Class for configuring license service
 * 
 * @author Jan Vybíral
 * 
 */
public class LicenseServiceConfig {

	/**
	 * Creates new instance of configuration.
	 * 
	 * @param encryptionKey
	 *            Key used to verify license integrity
	 * @param serviceBaseurl
	 *            Base url of license service
	 * @param useEncryption
	 *            Whether to use encrypted connection (https)
	 * @param applicationId
	 *            identifier of application
	 */
	public LicenseServiceConfig(String applicationId, Key encryptionKey, String serviceBaseurl, boolean useEncryption) {
		this.encryptionKey = encryptionKey;
		this.serviceBaseurl = serviceBaseurl;
		this.useEncryption = useEncryption;
		this.applicationId = applicationId;
	}

	private final String applicationId;
	private final Key encryptionKey;
	private final String serviceBaseurl;
	private final boolean useEncryption;

	public String getApplicationId() {
		return applicationId;
	}

	public Key getEncryptionKey() {
		return encryptionKey;
	}

	public String getServiceBaseurl() {
		return serviceBaseurl;
	}

	public boolean isUseEncryption() {
		return useEncryption;
	}

}
