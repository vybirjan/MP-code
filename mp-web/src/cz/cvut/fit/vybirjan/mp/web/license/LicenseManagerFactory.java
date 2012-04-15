package cz.cvut.fit.vybirjan.mp.web.license;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.google.inject.Inject;

import cz.cvut.fit.vybirjan.mp.serverside.core.DataSource;
import cz.cvut.fit.vybirjan.mp.serverside.core.LicenseManager;
import cz.cvut.fit.vybirjan.mp.serverside.core.ResponseKeyProvider;
import cz.cvut.fit.vybirjan.mp.serverside.domain.EntityFactory;
import cz.cvut.fit.vybirjan.mp.serverside.impl.LicenseManagerImpl;

@Provider
public class LicenseManagerFactory implements ContextResolver<LicenseManager> {

	@Inject
	private static DataSource ds;

	@Inject
	private static EntityFactory entityFactory;

	@Inject
	private static ResponseKeyProvider keyProvider;

	private static LicenseManager createLicenseManager() {
		return new LicenseManagerImpl(ds, entityFactory, keyProvider);
	}

	@Override
	public LicenseManager getContext(Class<?> arg0) {
		if (arg0.equals(LicenseManager.class)) {
			return createLicenseManager();
		} else {
			return null;
		}
	}

}
