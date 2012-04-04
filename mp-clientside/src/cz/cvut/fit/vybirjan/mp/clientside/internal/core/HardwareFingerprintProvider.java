package cz.cvut.fit.vybirjan.mp.clientside.internal.core;

import java.util.List;

import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;

public interface HardwareFingerprintProvider {

	List<HardwareFingerprint> collectFingerprints();

}
