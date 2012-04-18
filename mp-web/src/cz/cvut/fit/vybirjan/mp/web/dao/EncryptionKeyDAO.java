package cz.cvut.fit.vybirjan.mp.web.dao;

import cz.cvut.fit.vybirjan.mp.web.model.EncryptionKeyJDO;

public interface EncryptionKeyDAO {

	EncryptionKeyJDO findByAppId(String id);

	EncryptionKeyJDO persist(EncryptionKeyJDO key);

	void delete(EncryptionKeyJDO key);

}
