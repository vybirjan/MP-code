package cz.cvut.fit.vybirjan.mp.web.dao;

import java.util.List;

import cz.cvut.fit.vybirjan.mp.web.model.EncryptionKeyJDO;

public interface EncryptionKeyDAO {

	EncryptionKeyJDO findById(long id);

	EncryptionKeyJDO findByAppId(String id);

	EncryptionKeyJDO persist(EncryptionKeyJDO key);

	void delete(EncryptionKeyJDO key);

	List<EncryptionKeyJDO> findAll();

}
