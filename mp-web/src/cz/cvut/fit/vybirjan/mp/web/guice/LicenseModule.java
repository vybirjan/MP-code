package cz.cvut.fit.vybirjan.mp.web.guice;

import java.util.HashMap;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

import cz.cvut.fit.vybirjan.mp.serverside.core.DataSource;
import cz.cvut.fit.vybirjan.mp.serverside.core.ResponseKeyProvider;
import cz.cvut.fit.vybirjan.mp.serverside.domain.EntityFactory;
import cz.cvut.fit.vybirjan.mp.web.dao.ActivationDAO;
import cz.cvut.fit.vybirjan.mp.web.dao.EncryptionKeyDAO;
import cz.cvut.fit.vybirjan.mp.web.dao.LicenseDAO;
import cz.cvut.fit.vybirjan.mp.web.dao.impl.ActivationDAOImpl;
import cz.cvut.fit.vybirjan.mp.web.dao.impl.EncryptionKeyDAOImpl;
import cz.cvut.fit.vybirjan.mp.web.dao.impl.LicenseDAOimpl;
import cz.cvut.fit.vybirjan.mp.web.dao.impl.licensemanager.JDODatasourceImpl;
import cz.cvut.fit.vybirjan.mp.web.dao.impl.licensemanager.JDOKeyProvider;
import cz.cvut.fit.vybirjan.mp.web.dao.impl.licensemanager.WebEntityFactory;
import cz.cvut.fit.vybirjan.mp.web.license.LicenseManagerFactory;

public class LicenseModule extends ServletModule {

	@Override
	protected void configureServlets() {
		// persistence manager factory
		bind(PersistenceManagerFactory.class).toInstance(JDOHelper.getPersistenceManagerFactory("transactions-optional"));

		// license manager bindings
		bind(DataSource.class).to(JDODatasourceImpl.class);
		bind(ResponseKeyProvider.class).to(JDOKeyProvider.class);
		bind(EntityFactory.class).to(WebEntityFactory.class);

		// dao objects
		bind(LicenseDAO.class).to(LicenseDAOimpl.class);
		bind(ActivationDAO.class).to(ActivationDAOImpl.class);
		bind(EncryptionKeyDAO.class).to(EncryptionKeyDAOImpl.class);

		// inject data to factory
		requestStaticInjection(LicenseManagerFactory.class);

		Map<String, String> params = new HashMap<String, String>();

		params.put(
				"com.sun.jersey.config.property.packages",
				"cz.cvut.fit.vybirjan.mp.serverside.impl.jaxrs;cz.cvut.fit.vybirjan.mp.web.license;cz.cvut.fit.vybirjan.mp.web.controllers;cz.cvut.fit.vybirjan.mp.web.controllers.tml");
		params.put("com.sun.jersey.config.property.JSPTemplatesBasePath", "/WEB-INF/jsp");

		serve("/*").with(GuiceContainer.class, params);
	}
}
