package cz.cvut.fit.vybirjan.mp.web.controllers;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.sun.jersey.api.view.Viewable;

import cz.cvut.fit.vybirjan.mp.web.dao.LicenseDAO;
import cz.cvut.fit.vybirjan.mp.web.dto.IssuedLicensesDTO;
import cz.cvut.fit.vybirjan.mp.web.model.LicenseJDO;

@Produces("text/html")
@Path("/web/licenses")
public class IssuedLicensesController {

	@Inject
	public IssuedLicensesController(LicenseDAO licDao) {
		this.licDao = licDao;
	}

	private final LicenseDAO licDao;

	@GET
	public Response getLicenses() {
		List<LicenseJDO> licenses = licDao.findAll();

		IssuedLicensesDTO data = new IssuedLicensesDTO();
		for (LicenseJDO license : licenses) {
			data.addTableRecord(license);
		}

		return Response.ok(new Viewable("/licenses", data)).build();
	}
}
