package cz.cvut.fit.vybirjan.mp.serverside.impl;

import java.util.Iterator;

import cz.cvut.fit.vybirjan.mp.common.comm.LicenseRequest;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseResponse;
import cz.cvut.fit.vybirjan.mp.serverside.core.LicenseEventHandler;
import cz.cvut.fit.vybirjan.mp.serverside.core.RequestProcessingContext;

class RequestProcessingContenxtImpl implements RequestProcessingContext {

	public RequestProcessingContenxtImpl(Iterator<? extends LicenseEventHandler> eventHandlers, LicenseEventHandler base) {
		this.eventHandlers = eventHandlers;
		this.base = base;
	}

	private final Iterator<? extends LicenseEventHandler> eventHandlers;
	private final LicenseEventHandler base;

	@Override
	public LicenseResponse processActivateLicense(LicenseRequest request) {
		if (eventHandlers == null || !eventHandlers.hasNext()) {
			return base.handleActivateLicense(request, null);
		}

		return eventHandlers.next().handleActivateLicense(request, this);
	}

	@Override
	public LicenseResponse processGetExistingLicense(LicenseRequest request) {
		if (eventHandlers == null || !eventHandlers.hasNext()) {
			return base.handleGetExistingLicense(request, null);
		}

		return eventHandlers.next().handleGetExistingLicense(request, this);
	}

}
