package cz.cvut.fit.vybirjan.mp.web.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import org.datanucleus.exceptions.NucleusObjectNotFoundException;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.inject.Inject;

import cz.cvut.fit.vybirjan.mp.web.dao.LicenseDAO;
import cz.cvut.fit.vybirjan.mp.web.model.ActivationJDO;
import cz.cvut.fit.vybirjan.mp.web.model.AssignedFeatureJDO;
import cz.cvut.fit.vybirjan.mp.web.model.LicenseJDO;

public class LicenseDAOimpl implements LicenseDAO {

	@Inject
	public LicenseDAOimpl(PersistenceManagerFactory pmf) {
		this.pmf = pmf;
	}

	private final PersistenceManagerFactory pmf;

	@Override
	public int getNumberOfLicenses() {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			Query q = pm.newQuery("select id from " + LicenseJDO.class.getName());
			List<?> ids = (List<?>) q.execute();
			return ids.size();
		} finally {
			pm.close();
		}
	}

	@Override
	public LicenseJDO findByNumber(String licenseNumber) {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			Query q = pm.newQuery(LicenseJDO.class);
			q.setFilter("number == nameParam");
			q.declareParameters("String nameParam");
			q.setUnique(true);
			return (LicenseJDO) q.execute(licenseNumber);
		} finally {
			pm.close();
		}
	}

	@Override
	public List<LicenseJDO> findAll() {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			Query q = pm.newQuery(LicenseJDO.class);
			q.setOrdering("dateIssued asc");
			List<LicenseJDO> result = (List<LicenseJDO>) q.execute();
			return new ArrayList<LicenseJDO>(result);
		} finally {
			pm.close();
		}
	}

	@Override
	public LicenseJDO findById(long id) {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			Key k = KeyFactory.createKey(LicenseJDO.class.getSimpleName(), id);
			return pm.getObjectById(LicenseJDO.class, k);
		} catch (NucleusObjectNotFoundException e) {
			return null;
		} finally {
			pm.close();
		}
	}

	@Override
	public LicenseJDO persist(LicenseJDO jdo) {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			LicenseJDO license = pm.makePersistent(jdo);
			// also persist features
			for (AssignedFeatureJDO feature : license.getFeatures()) {
				pm.makePersistent(feature);
			}
			return license;
		} finally {
			pm.close();
		}
	}

	@Override
	public void delete(LicenseJDO lic) {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			Query deleteActivations = pm.newQuery(ActivationJDO.class);
			deleteActivations.setFilter("license == licenseParam");
			deleteActivations.declareParameters(Key.class.getName() + " licenseParam");

			Query deleteFeatures = pm.newQuery(AssignedFeatureJDO.class);
			deleteFeatures.setFilter("license == licenseParam");
			deleteFeatures.declareParameters(Key.class.getName() + " licenseParam");

			Query deleteLicense = pm.newQuery(LicenseJDO.class);
			deleteLicense.setFilter("id == idParam");
			deleteLicense.declareParameters(Key.class.getName() + " idParam");

			deleteActivations.deletePersistentAll(lic.getId());
			deleteFeatures.deletePersistentAll(lic.getId());
			deleteLicense.deletePersistentAll(lic.getId());
		} finally {
			pm.close();
		}
	}

}
