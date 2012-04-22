package cz.cvut.fit.vybirjan.mp.web.dao;

import java.util.Date;
import java.util.List;

import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;
import cz.cvut.fit.vybirjan.mp.serverside.domain.License;
import cz.cvut.fit.vybirjan.mp.web.model.ActivationJDO;

public interface ActivationDAO {

	ActivationJDO findActiveActivationForLicense(License l, List<HardwareFingerprint> fingerprints);

	int getNumberOfActiveActivations();

	Date getLastActivationDate();

	ActivationJDO findById(long licenseId, long id);

	ActivationJDO persist(ActivationJDO act);

	void delete(ActivationJDO act);

}
