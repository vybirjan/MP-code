package cz.cvut.fit.vybirjan.mp.serverside.domain;

import java.util.List;

import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;

public interface EntityFactory {

	License createLicense();

	Feature createFeature();

	Activation createActivation(List<HardwareFingerprint> fingerprints);

}
