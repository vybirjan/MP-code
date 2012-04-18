package cz.cvut.fit.vybirjan.mp.clientside.internal.core;

import java.util.List;

import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;

/**
 * Class used to obtain hardware fingerprints of local computer.
 * 
 * @author Jan Vybíral
 * 
 */
public interface HardwareFingerprintProvider {

	void inititalize();

	List<HardwareFingerprint> collectFingerprints();

	void destroy();

}
