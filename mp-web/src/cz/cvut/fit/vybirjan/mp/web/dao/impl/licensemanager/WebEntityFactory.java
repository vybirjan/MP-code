package cz.cvut.fit.vybirjan.mp.web.dao.impl.licensemanager;

import java.util.Date;
import java.util.List;

import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;
import cz.cvut.fit.vybirjan.mp.serverside.domain.Activation;
import cz.cvut.fit.vybirjan.mp.serverside.domain.EntityFactory;
import cz.cvut.fit.vybirjan.mp.web.model.ActivationJDO;

public class WebEntityFactory implements EntityFactory {

	@Override
	public Activation createActivation(List<HardwareFingerprint> fingerprints) {
		ActivationJDO ret = new ActivationJDO();
		ret.setFingerprints(fingerprints);
		ret.setActive(false);
		ret.setDateCreated(new Date());
		return ret;
	}
}
