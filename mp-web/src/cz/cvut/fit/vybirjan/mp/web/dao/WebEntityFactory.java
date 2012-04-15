package cz.cvut.fit.vybirjan.mp.web.dao;

import java.util.Date;
import java.util.List;

import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;
import cz.cvut.fit.vybirjan.mp.serverside.domain.Activation;
import cz.cvut.fit.vybirjan.mp.serverside.domain.EntityFactory;
import cz.cvut.fit.vybirjan.mp.serverside.domain.Feature;
import cz.cvut.fit.vybirjan.mp.serverside.domain.License;
import cz.cvut.fit.vybirjan.mp.web.model.ActivationJDO;
import cz.cvut.fit.vybirjan.mp.web.model.FeatureJDO;
import cz.cvut.fit.vybirjan.mp.web.model.LicenseJDO;

public class WebEntityFactory implements EntityFactory {

	@Override
	public License createLicense() {
		return new LicenseJDO();
	}

	@Override
	public Feature createFeature() {
		return new FeatureJDO();
	}

	@Override
	public Activation createActivation(List<HardwareFingerprint> fingerprints) {
		ActivationJDO ret = new ActivationJDO();
		ret.setFingerprints(fingerprints);
		ret.setActive(true);
		ret.setDateCreated(new Date());
		return ret;
	}
}
