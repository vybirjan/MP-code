package cz.cvut.fit.vybirjan.mp.serverside.domain;

import java.util.Date;
import java.util.List;

import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;

public interface Activation {

	boolean isActive();

	Date getDateCreated();

	List<HardwareFingerprint> getFingerprints();

}
