package cz.cvut.fit.vybirjan.mp.serverside.domain;

import java.util.List;

import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;

/**
 * Interface used to obtain new instances of entites
 * 
 * @author Jan Vybíral
 * 
 */
public interface EntityFactory {

	/**
	 * Creates new instance of activation. Created activation must return
	 * provided fingerprints
	 * 
	 * @param fingerprints
	 *            Activation fingerprints
	 * @return New activation
	 */
	Activation createActivation(List<HardwareFingerprint> fingerprints);

}
