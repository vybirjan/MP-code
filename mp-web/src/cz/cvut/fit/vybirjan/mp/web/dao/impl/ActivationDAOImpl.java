package cz.cvut.fit.vybirjan.mp.web.dao.impl;

import java.util.Date;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.inject.Inject;

import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;
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
	public ActivationJDO findActiveActivationForLicense(License l, List<HardwareFingerprint> fingerprints) {
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

	@Override
	public ActivationJDO findById(long licenseId, long id) {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			return pm.getObjectById(ActivationJDO.class,
					KeyFactory.createKey(KeyFactory.createKey(LicenseJDO.class.getSimpleName(), licenseId), ActivationJDO.class.getSimpleName(), id));
		} catch (JDOObjectNotFoundException e) {
			return null;
		} finally {
			pm.close();
		}
	}

	@Override
	public ActivationJDO persist(ActivationJDO act) {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			pm.makePersistent(act.getLicense());
			return act;
		} finally {
			pm.close();
		}
	}

	@Override
	public void delete(ActivationJDO act) {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			Query q = pm.newQuery(ActivationJDO.class);
			q.setFilter("id == idParam");
			q.declareParameters(Key.class.getName() + " idParam");
			q.deletePersistentAll(act.getId());
		} finally {
			pm.close();
		}
	}

}
