package cz.cvut.fit.vybirjan.mp.web.dao;

import java.util.List;

import cz.cvut.fit.vybirjan.mp.web.model.LicenseJDO;

public interface LicenseDAO {

	int getNumberOfLicenses();

	LicenseJDO findByNumber(String number);

	List<LicenseJDO> findAll();

	LicenseJDO findById(long id);

	LicenseJDO persist(LicenseJDO jdo);

	void delete(LicenseJDO lic);

}
