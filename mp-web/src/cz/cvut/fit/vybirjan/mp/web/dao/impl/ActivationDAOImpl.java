package cz.cvut.fit.vybirjan.mp.web.dao.impl;

import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.inject.Inject;

import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;
import cz.cvut.fit.vybirjan.mp.serverside.domain.Activation;
import cz.cvut.fit.vybirjan.mp.serverside.domain.License;
import cz.cvut.fit.vybirjan.mp.web.dao.ActivationDAO;
import cz.cvut.fit.vybirjan.mp.web.model.ActivationJDO;
import cz.cvut.fit.vybirjan.mp.web.model.LicenseJDO;

public class ActivationDAOImpl implements ActivationDAO {

	@Inject
	public ActivationDAOImpl(PersistenceManagerFactory pmf) {
		this.pmf = pmf;
	}

	private final PersistenceManagerFactory pmf;

	@Override
	public Activation findActiveActivationForLicense(License l, List<HardwareFingerprint> fingerprints) {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			Query q = pm.newQuery(ActivationJDO.class);
			q.setUnique(true);
			q.setFilter("license == licenseParam && active == true && serializedFingerprints == fingerprintsParam");
			q.declareParameters(LicenseJDO.class.getName() + " licenseParam, " +
					"String fingerprintsParam");
			return (ActivationJDO) q.execute(l, HardwareFingerprint.toMultiString(fingerprints));
		} finally {
			pm.close();
		}
	}

	@Override
	public int getNumberOfActiveActivations() {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			Query q = pm.newQuery("select id from " + ActivationJDO.class.getName());
			q.setFilter("active == true");
			List<?> result = (List<?>) q.execute();
			return result.size();
		} finally {
			pm.close();
		}
	}

	@Override
	public Date getLastActivationDate() {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			Query q = pm.newQuery("select dateCreated from " + ActivationJDO.class.getName());
			q.setOrdering("dateCreated desc");
			q.setRange(0, 1);
			q.setUnique(true);

			return (Date) q.execute();
		} finally {
			pm.close();
		}
	}

}
