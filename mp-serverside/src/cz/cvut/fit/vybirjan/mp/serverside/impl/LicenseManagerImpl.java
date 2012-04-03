package cz.cvut.fit.vybirjan.mp.serverside.impl;

import java.util.LinkedList;
import java.util.List;

import cz.cvut.fit.vybirjan.mp.common.comm.LicenseRequest;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseResponse;
import cz.cvut.fit.vybirjan.mp.serverside.core.DataSource;
import cz.cvut.fit.vybirjan.mp.serverside.core.LicenseEventHandler;
import cz.cvut.fit.vybirjan.mp.serverside.core.LicenseManager;
import cz.cvut.fit.vybirjan.mp.serverside.domain.EntityFactory;

public class LicenseManagerImpl implements LicenseManager {

	public LicenseManagerImpl(DataSource dataSource, EntityFactory entityFactory) {
		this.dataSource = dataSource;
		this.entityFactory = entityFactory;
	}

	protected final DataSource dataSource;
	protected final EntityFactory entityFactory;

	protected List<LicenseEventHandler> eventHandlers = new LinkedList<LicenseEventHandler>();

	@Override
	public LicenseResponse requestLicense(LicenseRequest response) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addLicenseEventHandler(LicenseEventHandler handler) {
		synchronized (eventHandlers) {
			if (!eventHandlers.contains(handler)) {
				eventHandlers.add(handler);
			}
		}
	}

	public boolean removeLicenseEventHandler(LicenseEventHandler handler) {
		synchronized (eventHandlers) {
			return eventHandlers.remove(handler);
		}
	}

}
