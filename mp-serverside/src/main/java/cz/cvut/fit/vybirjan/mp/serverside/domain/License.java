package cz.cvut.fit.vybirjan.mp.serverside.domain;

import java.util.Date;

/**
 * Entity representing one license on software.
 * 
 * @author Jan Vybíral
 * 
 */
public interface License {

	/**
	 * Indicates whether license is active
	 */
	boolean isActive();

	/**
	 * Indicates whether license permits to create new activations
	 */
	boolean isAllowNewActivations();

	/**
	 * License description to be displayed to user
	 */
	String getDescription();

	/**
	 * Returns maximal number of issued activations.
	 * 
	 * @return Maximal number of activations or null if number of activations is
	 *         not limited
	 */
	Integer getMaxActivations();

	/**
	 * Returns license number
	 */
	String getNumber();

	/**
	 * Returns start date of license validity
	 * 
	 * @return Start date or null for unlimited validity
	 */
	Date getValidFrom();

	/**
	 * Returns end date of license validity
	 * 
	 * @return End date or null for unlimited validity
	 */
	Date getValidTo();
}
