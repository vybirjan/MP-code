package cz.cvut.fit.vybirjan.mp.serverside.domain;

import java.util.Date;

import cz.cvut.fit.vybirjan.mp.common.crypto.TaggedKey;

/**
 * Entity representing one software feature of client application
 * 
 * @author Jan Vybíral
 * 
 */
public interface Feature {

	/**
	 * Feature code, used for identification in client application
	 * 
	 * @return
	 */
	String getCode();

	/**
	 * Feature description to be presented to end user
	 * 
	 * @return
	 */
	String getDescription();

	/**
	 * Start date of validity of feature.
	 * 
	 * @return Date or null if start of validity is not limited
	 */
	Date getValidFrom();

	/**
	 * End date of validity of feature.
	 * 
	 * @return Date or null if end of validity is not limited
	 */
	Date getValidTo();

	/**
	 * Key to decipher classes with on client application.
	 * 
	 * @return Tagged key or null
	 */
	TaggedKey getKey();

}
