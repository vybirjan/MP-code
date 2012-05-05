package cz.cvut.fit.vybirjan.mp.web.dao.impl;

import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
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
			q.declareParameters("String appIdParam");
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
			Query q = pm.newQuery(EncryptionKeyJDO.class);
			q.setFilter("id == idParam");
			q.declareParameters(Key.class.getName() + " idParam");
			q.deletePersistentAll(key.getId());
		} finally {
			pm.close();
		}
	}

	@Override
	public List<EncryptionKeyJDO> findAll() {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			Query q = pm.newQuery(EncryptionKeyJDO.class);
			q.setOrdering("appId desc");
			List<EncryptionKeyJDO> ret = (List<EncryptionKeyJDO>) q.execute();
			ret.size();
			return ret;
		} finally {
			pm.close();
		}
	}

	@Override
	public EncryptionKeyJDO findById(long id) {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			Key k = KeyFactory.createKey(EncryptionKeyJDO.class.getSimpleName(), id);
			return pm.getObjectById(EncryptionKeyJDO.class, k);
		} catch (JDOObjectNotFoundException e) {
			return null;
		} finally {
			pm.close();
		}
	}

}
