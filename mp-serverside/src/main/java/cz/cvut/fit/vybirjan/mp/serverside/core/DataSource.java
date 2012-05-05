package cz.cvut.fit.vybirjan.mp.serverside.core;

import java.util.List;

import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;
import cz.cvut.fit.vybirjan.mp.serverside.domain.Activation;
import cz.cvut.fit.vybirjan.mp.serverside.domain.Feature;
import cz.cvut.fit.vybirjan.mp.serverside.domain.License;

/**
 * Interface for accessing persistent storage.
 * 
 * @author Jan Vybíral
 * 
 */
public interface DataSource {

	/**
	 * Returns all features for this particular license.
	 * 
	 * @param l
	 *            License to find features for
	 * @return List of all features for this license, or empty list if no
	 *         features were found
	 */
	List<Feature> findFeaturesForLicense(License l);

	/**
	 * Returns all activations of given license which are active
	 * 
	 * @param l
	 *            License to find activations for
	 * @return List of all active activations, or emtpy list
	 */
	List<Activation> findActiveActivationsForLicense(License l);

	/**
	 * Finds activation which is active, was activated with provided
	 * fingerprints and belongs to provided license
	 * 
	 * @param l
	 *            License to which activation should belong to
	 * @param fingerprints
	 *            List of fingerprints
	 * @return Found activation or null if none found
	 */
	Activation findActiveActivationForLicense(License l, List<HardwareFingerprint> fingerprints);

	/**
	 * Finds license by number
	 * 
	 * @param licenseNumber
	 *            Number of license
	 * @return License with given number, or null
	 */
	License findByNumber(String licenseNumber);

	/**
	 * Adds new activation to license. After returning from this method, new
	 * activation should be persisted and any consecutive calls to
	 * {@link DataSource#findActiveActivationForLicense(License, List)} must
	 * return this activation (until it is deleted or deactivated)
	 * 
	 * @param license
	 *            License to add activation to
	 * @param activation
	 *            Activation to create
	 */
	void addActivationToLicense(License license, Activation activation);
}
