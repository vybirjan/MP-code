package cz.cvut.fit.vybirjan.mp.web.dao;

import java.util.List;

import cz.cvut.fit.vybirjan.mp.serverside.domain.License;
import cz.cvut.fit.vybirjan.mp.web.model.LicenseJDO;

public interface LicenseDAO {

	int getNumberOfLicenses();

	License findByNumber(String number);

	List<LicenseJDO> findAll();

}
