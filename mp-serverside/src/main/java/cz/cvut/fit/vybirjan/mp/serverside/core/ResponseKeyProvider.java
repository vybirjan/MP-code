package cz.cvut.fit.vybirjan.mp.serverside.core;

import java.security.Key;

import cz.cvut.fit.vybirjan.mp.common.comm.LicenseRequest;

/**
 * Interface to get encryption key for request. Response to this request will be
 * encrypteed by provided key.
 * 
 * @author Jan Vybíral
 * 
 */
public interface ResponseKeyProvider {

	/**
	 * Returns key which should be used to encrypt response to this request
	 * 
	 * @param request
	 *            Request, whose response should be encrypted
	 * @return Key which should be used to encrypt response, or null if no
	 *         encryption should be done.
	 */
	Key getResponseEncryptionKey(LicenseRequest request);

}
