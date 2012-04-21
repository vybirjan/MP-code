package cz.cvut.fit.vybirjan.mp.web.dao.impl;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.inject.Inject;

import cz.cvut.fit.vybirjan.mp.web.dao.AssignedFeatureDAO;
import cz.cvut.fit.vybirjan.mp.web.model.AssignedFeatureJDO;

public class AssignedFeatureDAOimpl implements AssignedFeatureDAO {

	@Inject
	public AssignedFeatureDAOimpl(PersistenceManagerFactory pmf) {
		this.pmf = pmf;
	}

	private final PersistenceManagerFactory pmf;

	@Override
	public AssignedFeatureJDO persist(AssignedFeatureJDO feature) {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			return pm.makePersistent(feature);
		} finally {
			pm.close();
		}
	}

	@Override
	public void delete(AssignedFeatureJDO feature) {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			Query q = pm.newQuery(AssignedFeatureJDO.class);
			q.setFilter("id == idParam");
			q.declareParameters(Key.class.getName() + " idParam");
			q.deletePersistentAll(feature.getId());
		} finally {
			pm.close();
		}
	}

}
