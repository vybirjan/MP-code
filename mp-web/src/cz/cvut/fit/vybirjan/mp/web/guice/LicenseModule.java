package cz.cvut.fit.vybirjan.mp.web.guice;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import com.google.inject.AbstractModule;

import cz.cvut.fit.vybirjan.mp.serverside.core.DataSource;
import cz.cvut.fit.vybirjan.mp.serverside.core.ResponseKeyProvider;
import cz.cvut.fit.vybirjan.mp.serverside.domain.EntityFactory;
import cz.cvut.fit.vybirjan.mp.web.dao.JDODatasourceImpl;
import cz.cvut.fit.vybirjan.mp.web.dao.JDOKeyProvider;
import cz.cvut.fit.vybirjan.mp.web.dao.WebEntityFactory;
import cz.cvut.fit.vybirjan.mp.web.license.LicenseManagerFactory;

public class LicenseModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(DataSource.class).to(JDODatasourceImpl.class);
		bind(ResponseKeyProvider.class).to(JDOKeyProvider.class);
		bind(EntityFactory.class).to(WebEntityFactory.class);

		bind(PersistenceManagerFactory.class).toInstance(JDOHelper.getPersistenceManagerFactory("transactions-optional"));

		requestStaticInjection(LicenseManagerFactory.class);
	}

}
