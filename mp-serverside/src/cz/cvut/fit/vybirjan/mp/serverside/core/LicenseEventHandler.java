package cz.cvut.fit.vybirjan.mp.serverside.core;

import cz.cvut.fit.vybirjan.mp.common.comm.LicenseRequest;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseResponse;

public interface LicenseEventHandler {

	LicenseResponse handleRequest(LicenseRequest request, LicenseEventHandler nextInChain);

}
