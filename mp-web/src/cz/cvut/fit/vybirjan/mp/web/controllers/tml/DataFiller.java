package cz.cvut.fit.vybirjan.mp.web.controllers.tml;

import java.util.Date;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.google.inject.Inject;

import cz.cvut.fit.vybirjan.mp.web.model.FeatureJDO;
import cz.cvut.fit.vybirjan.mp.web.model.LicenseJDO;

@Path("/test")
public class DataFiller {

	@Inject
	public DataFiller(PersistenceManagerFactory pmf) {
		this.pmf = pmf;
	}

	private final PersistenceManagerFactory pmf;

	@GET
	@Produces("text/plain")
	@Path("fillData")
	public String fillDb() {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			LicenseJDO license = new LicenseJDO();
			license.setActive(true);
			license.setAllowedNewActivations(true);
			license.setDateIssued(new Date());
			license.setDescription("Test license");
			license.setMaxActivation(5);
			license.setNumber("TEST-123");
			license.setValidFrom(new Date());
			license = pm.makePersistent(license);

			FeatureJDO feature1 = new FeatureJDO();
			feature1.setCode("FFFF");
			feature1.setDescription("test feature");
			feature1 = pm.makePersistent(feature1);

			FeatureJDO feature2 = new FeatureJDO();
			feature2.setCode("XXXX");
			feature2.setDescription("another feature");
			feature2 = pm.makePersistent(feature2);

			license.addFeature(feature1);
			license.addFeature(feature2);

			pm.makePersistent(license);
		} finally {
			pm.close();
		}

		return "ook!";
	}
}
