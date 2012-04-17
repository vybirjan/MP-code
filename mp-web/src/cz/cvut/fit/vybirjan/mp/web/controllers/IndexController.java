package cz.cvut.fit.vybirjan.mp.web.controllers;

import java.text.DateFormat;
import java.text.Format;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.sun.jersey.api.view.Viewable;

import cz.cvut.fit.vybirjan.mp.web.dao.ActivationDAO;
import cz.cvut.fit.vybirjan.mp.web.dao.LicenseDAO;
import cz.cvut.fit.vybirjan.mp.web.dto.OverviewDTO;

@Produces("text/html")
@Path("/web/index")
public class IndexController {

	private static final Format DATE_FORMAT = DateFormat.getDateTimeInstance();

	@Inject
	public IndexController(LicenseDAO licDao, ActivationDAO actDao) {
		this.licDao = licDao;
		this.actDao = actDao;
	}

	private final LicenseDAO licDao;
	private final ActivationDAO actDao;

	@GET
	public Response helloWorld() {
		OverviewDTO overview = new OverviewDTO();
		Date lastActivation = actDao.getLastActivationDate();
		if (lastActivation == null) {
			overview.setLastActivationDate("no activation was yet created");
		} else {
			overview.setLastActivationDate(DATE_FORMAT.format(lastActivation));
		}

		overview.setNumberOfActivations(actDao.getNumberOfActiveActivations());
		overview.setNumberOfLicenses(licDao.getNumberOfLicenses());

		Viewable v = new Viewable("/index", overview);
		return Response.ok(v).build();
	}
}
