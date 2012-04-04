package cz.cvut.fit.vybirjan.mp.clientside.internal.core;

import cz.cvut.fit.vybirjan.mp.common.comm.LicenseInformation;

public interface SecureStorage {

	void save(LicenseInformation info);

	LicenseInformation loadInfo();

	void clear();

}
