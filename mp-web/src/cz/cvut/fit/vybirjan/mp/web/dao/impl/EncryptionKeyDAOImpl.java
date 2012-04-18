package cz.cvut.fit.vybirjan.mp.web.dao.impl;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.inject.Inject;

import cz.cvut.fit.vybirjan.mp.web.dao.EncryptionKeyDAO;
import cz.cvut.fit.vybirjan.mp.web.model.EncryptionKeyJDO;

public class EncryptionKeyDAOImpl implements EncryptionKeyDAO {

	@Inject
	public EncryptionKeyDAOImpl(PersistenceManagerFactory pmf) {
		this.pmf = pmf;
	}

	private final PersistenceManagerFactory pmf;

	@Override
	public EncryptionKeyJDO findByAppId(String id) {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			Query q = pm.newQuery(EncryptionKeyJDO.class);
			q.setFilter("appId == appIdParam");
			q.declareParameters("String appId");
			q.setUnique(true);
			return (EncryptionKeyJDO) q.execute(id);
		} finally {
			pm.close();
		}
	}

	@Override
	public EncryptionKeyJDO persist(EncryptionKeyJDO key) {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			return pm.makePersistent(key);
		} finally {
			pm.close();
		}
	}

	@Override
	public void delete(EncryptionKeyJDO key) {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			key = pm.makePersistent(key);
			pm.deletePersistent(key);
		} finally {
			pm.close();
		}
	}

}
