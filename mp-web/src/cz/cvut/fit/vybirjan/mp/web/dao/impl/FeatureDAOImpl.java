package cz.cvut.fit.vybirjan.mp.web.dao.impl;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.inject.Inject;

import cz.cvut.fit.vybirjan.mp.web.dao.FeatureDAO;
import cz.cvut.fit.vybirjan.mp.web.model.AssignedFeatureJDO;
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
	public List<FeatureJDO> findAll() {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			Query q = pm.newQuery(FeatureJDO.class);
			q.setOrdering("code asc");
			List<FeatureJDO> result = (List<FeatureJDO>) q.execute();
			result.size();
			return result;
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

			// also delete all assigned keys
			q = pm.newQuery(AssignedFeatureJDO.class);
			q.setFilter("code == codeParam");
			q.declareParameters("String codeParam");
			q.deletePersistentAll(feature.getCode());

		} finally {
			pm.close();
		}
	}

}
