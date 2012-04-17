package cz.cvut.fit.vybirjan.mp.web.dao;

import java.util.Date;
import java.util.List;

import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;
import cz.cvut.fit.vybirjan.mp.serverside.domain.Activation;
import cz.cvut.fit.vybirjan.mp.serverside.domain.License;

public interface ActivationDAO {

	Activation findActiveActivationForLicense(License l, List<HardwareFingerprint> fingerprints);

	int getNumberOfActiveActivations();

	Date getLastActivationDate();

}
