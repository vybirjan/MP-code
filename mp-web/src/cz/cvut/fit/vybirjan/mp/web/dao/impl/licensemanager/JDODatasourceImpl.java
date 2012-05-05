package cz.cvut.fit.vybirjan.mp.web.dao.impl.licensemanager;

import java.util.LinkedList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import com.google.inject.Inject;

import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;
import cz.cvut.fit.vybirjan.mp.serverside.core.DataSource;
import cz.cvut.fit.vybirjan.mp.serverside.domain.Activation;
import cz.cvut.fit.vybirjan.mp.serverside.domain.Feature;
import cz.cvut.fit.vybirjan.mp.serverside.domain.License;
import cz.cvut.fit.vybirjan.mp.web.dao.ActivationDAO;
import cz.cvut.fit.vybirjan.mp.web.dao.LicenseDAO;
import cz.cvut.fit.vybirjan.mp.web.model.ActivationJDO;
import cz.cvut.fit.vybirjan.mp.web.model.LicenseJDO;

public class JDODatasourceImpl implements DataSource {

	@Inject
	public JDODatasourceImpl(LicenseDAO licDao, ActivationDAO actDao, PersistenceManagerFactory pmf) {
		this.licDao = licDao;
		this.actDao = actDao;
		this.pmf = pmf;
	}

	private final LicenseDAO licDao;
	private final ActivationDAO actDao;
	private final PersistenceManagerFactory pmf;

	@Override
	public List<? extends Feature> findFeaturesForLicense(License l) {
		LicenseJDO license = (LicenseJDO) l;
		return license.getFeatures();
	}

	@Override
	public List<? extends Activation> findActiveActivationsForLicense(License l) {
		LicenseJDO license = (LicenseJDO) l;
		List<ActivationJDO> activations = new LinkedList<ActivationJDO>();

		for (ActivationJDO activation : license.getActivations()) {
			if (activation.isActive()) {
				activations.add(activation);
			}
		}

		return activations;
	}

	@Override
	public void addActivationToLicense(License lic, Activation act) {
		PersistenceManager pm = pmf.getPersistenceManager();
		LicenseJDO license = (LicenseJDO) lic;
		ActivationJDO activation = (ActivationJDO) act;
		try {
			license.addActivation(activation);
			pm.makePersistent(license);
		} finally {
			pm.close();
		}
	}

	@Override
	public Activation findActiveActivationForLicense(License l, List<HardwareFingerprint> fingerprints) {
		return actDao.findActiveActivationForLicense(l, fingerprints);
	}

	@Override
	public License findByNumber(String licenseNumber) {
		return licDao.findByNumber(licenseNumber);
	}

}
