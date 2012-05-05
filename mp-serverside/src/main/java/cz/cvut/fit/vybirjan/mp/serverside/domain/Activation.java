package cz.cvut.fit.vybirjan.mp.serverside.domain;

import java.util.List;

import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;

/**
 * Entity representing activation of license from client application
 * 
 * @author Jan Vybíral
 * 
 */
public interface Activation {

	/**
	 * Indicates whether is activation active. Inactive activations are ignored.
	 * 
	 */
	boolean isActive();

	/**
	 * Returns fingerprints of client application which created this activation
	 * 
	 * @return
	 */
	List<HardwareFingerprint> getFingerprints();

}
