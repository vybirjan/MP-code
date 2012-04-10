package cz.cvut.fit.vybirjan.mp.serverside.core;

import java.security.Key;

import cz.cvut.fit.vybirjan.mp.common.comm.LicenseRequest;

public interface ResponseKeyProvider {

	Key getResponseEncryptionKey(LicenseRequest request);

}
