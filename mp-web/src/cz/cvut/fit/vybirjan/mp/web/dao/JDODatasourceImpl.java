package cz.cvut.fit.vybirjan.mp.web.dao;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import com.google.inject.Inject;

import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;
import cz.cvut.fit.vybirjan.mp.serverside.core.DataSource;
import cz.cvut.fit.vybirjan.mp.serverside.domain.Activation;
import cz.cvut.fit.vybirjan.mp.serverside.domain.Feature;
import cz.cvut.fit.vybirjan.mp.serverside.domain.License;
import cz.cvut.fit.vybirjan.mp.web.model.ActivationJDO;
import cz.cvut.fit.vybirjan.mp.web.model.LicenseJDO;

public class JDODatasourceImpl implements DataSource {

	@Inject
	private PersistenceManagerFactory pmf;

	@Override
	public List<? extends Feature> findFeaturesForLicense(License l) {
		LicenseJDO license = (LicenseJDO) l;
		return license.getFeatures();
	}

	@Override
	public List<? extends Activation> findActiveActivationsForLicense(License l) {
		LicenseJDO license = (LicenseJDO) l;
		return license.getActivations();
	}

	@Override
	public Activation findActiveActivationForLicense(License l, List<HardwareFingerprint> fingerprints) {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			return ActivationJDO.findActiveForLicense((LicenseJDO) l, fingerprints, pm);
		} finally {
			pm.close();
		}
	}

	@Override
	public License findByNumber(String licenseNumber) {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			return LicenseJDO.findByNumber(licenseNumber, pm);
		} finally {
			pm.close();
		}
	}

	@Override
	public void addActivationToLicense(License lic, Activation act) {
		PersistenceManager pm = pmf.getPersistenceManager();
		LicenseJDO license = (LicenseJDO) lic;
		ActivationJDO activation = (ActivationJDO) act;
		try {
			license = pm.makePersistent(license);
			activation = pm.makePersistent(activation);
			license.addActivation(activation);
		} finally {
			pm.close();
		}
	}

}
