package cz.cvut.fit.vybirjan.mp.clientside.internal.core;

import java.io.IOException;

import cz.cvut.fit.vybirjan.mp.common.comm.LicenseInformation;

public interface SecureStorage {

	void save(LicenseInformation info) throws IOException;

	LicenseInformation loadInfo() throws IOException;

	void clear() throws IOException;

}
