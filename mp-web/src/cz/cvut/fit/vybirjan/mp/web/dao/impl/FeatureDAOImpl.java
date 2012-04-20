package cz.cvut.fit.vybirjan.mp.web.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.inject.Inject;

import cz.cvut.fit.vybirjan.mp.web.dao.FeatureDAO;
import cz.cvut.fit.vybirjan.mp.web.model.FeatureJDO;

public class FeatureDAOImpl implements FeatureDAO {

	@Inject
	public FeatureDAOImpl(PersistenceManagerFactory pmf) {
		this.pmf = pmf;
	}

	private final PersistenceManagerFactory pmf;

	@Override
	public FeatureJDO findByCode(String code) {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			Query q = pm.newQuery(FeatureJDO.class);
			q.setFilter("code == codeParam");
			q.declareParameters("String codeParam");
			q.setUnique(true);
			return (FeatureJDO) q.execute(code);
		} finally {
			pm.close();
		}
	}

	@Override
	public FeatureJDO findById(long id) {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			Key k = KeyFactory.createKey(FeatureJDO.class.getSimpleName(), id);
			return pm.getObjectById(FeatureJDO.class, k);
		} catch (JDOObjectNotFoundException e) {
			return null;
		} finally {
			pm.close();
		}
	}

	@Override
	public List<FeatureJDO> findAll() {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			Query q = pm.newQuery(FeatureJDO.class);
			q.setOrdering("code asc");
			return new ArrayList<FeatureJDO>((List<FeatureJDO>) q.execute());
		} finally {
			pm.close();
		}
	}

	@Override
	public FeatureJDO persist(FeatureJDO feature) {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			return pm.makePersistent(feature);
		} finally {
			pm.close();
		}
	}

	@Override
	public void delete(FeatureJDO feature) {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			Query q = pm.newQuery(FeatureJDO.class);
			q.setFilter("id == idParam");
			q.declareParameters(Key.class.getName() + " idParam");
			q.deletePersistentAll(feature.getId());
		} finally {
			pm.close();
		}
	}

}
