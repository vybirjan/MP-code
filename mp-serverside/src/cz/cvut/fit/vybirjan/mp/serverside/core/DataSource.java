package cz.cvut.fit.vybirjan.mp.serverside.core;

import java.util.Collection;
import java.util.List;

import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;
import cz.cvut.fit.vybirjan.mp.serverside.domain.Activation;
import cz.cvut.fit.vybirjan.mp.serverside.domain.Feature;
import cz.cvut.fit.vybirjan.mp.serverside.domain.License;

public interface DataSource {

	Collection<Feature> findFeaturesForLicense(License l);

	Collection<Activation> findActiveActivationsForLicense(License l);

	Activation findActiveActivationForLicense(License l, List<HardwareFingerprint> fingerprints);

	License findByNumber(String licenseNumber);

	void addActivationToLicense(License license, Activation activation);
}
