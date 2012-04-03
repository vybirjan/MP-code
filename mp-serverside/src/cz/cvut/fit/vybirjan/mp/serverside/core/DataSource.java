package cz.cvut.fit.vybirjan.mp.serverside.core;

import java.util.Collection;

import cz.cvut.fit.vybirjan.mp.serverside.domain.Activation;
import cz.cvut.fit.vybirjan.mp.serverside.domain.Feature;
import cz.cvut.fit.vybirjan.mp.serverside.domain.License;

public interface DataSource {

	Collection<? extends Feature> getFeaturesForLicense(License l);

	Collection<? extends Activation> getActivationsForLicense(License l);
	
	License findByNumber(String licenseNumber);
}
