package cz.cvut.fit.vybirjan.mp.serverside.domain;

import java.util.List;

import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;

public interface EntityFactory {

	Activation createActivation(List<HardwareFingerprint> fingerprints);

}
