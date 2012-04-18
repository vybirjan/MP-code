package cz.cvut.fit.vybirjan.mp.common.crypto;

import javax.crypto.SecretKey;

/**
 * Extended type of key which contains tag for easier identification.
 * 
 * @author Jan Vybíral
 * 
 */
public interface TaggedKey extends SecretKey {

	/**
	 * Returns tag of this key.
	 */
	int getTag();

}
