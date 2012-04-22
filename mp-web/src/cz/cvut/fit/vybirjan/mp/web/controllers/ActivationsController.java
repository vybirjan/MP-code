package cz.cvut.fit.vybirjan.mp.web.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.inject.Inject;
import com.sun.jersey.api.view.Viewable;

import cz.cvut.fit.vybirjan.mp.common.Utils;
import cz.cvut.fit.vybirjan.mp.web.dao.ActivationDAO;
import cz.cvut.fit.vybirjan.mp.web.dao.LicenseDAO;
import cz.cvut.fit.vybirjan.mp.web.dto.ActivationsDTO;
import cz.cvut.fit.vybirjan.mp.web.dto.DTO;
import cz.cvut.fit.vybirjan.mp.web.model.ActivationJDO;
import cz.cvut.fit.vybirjan.mp.web.model.LicenseJDO;

@Path("/web/activations")
@Produces("text/html")
public class ActivationsController {

	@Inject
	public ActivationsController(LicenseDAO licDao, ActivationDAO actiDao) {
		this.licDao = licDao;
		this.actiDao = actiDao;
	}

	private final LicenseDAO licDao;
	private final ActivationDAO actiDao;

	@GET
	@Path("/{id}")
	public Response showActivations(
			@PathParam("id") long id,
			@QueryParam("okMessage") String okMessage,
			@QueryParam("errorMessage") String errorMessage) {
		LicenseJDO license = licDao.findById(id);

		if (license == null) {
			return Response.status(Status.NOT_FOUND).build();
		}

		ActivationsDTO dto = new ActivationsDTO(license);

		if (!DTO.isNullOrEmpty(okMessage)) {
			dto.setOkMessage(okMessage);
		} else if (!DTO.isNullOrEmpty(errorMessage)) {
			dto.setErrorMessage(errorMessage);
		}

		return Response.ok(new Viewable("/activations", dto)).build();
	}

	@GET
	@Path("/{licenseId}/toggleactive/{id}")
	public Response toggleActive(@PathParam("licenseId") long licenseId, @PathParam("id") long id) throws UnsupportedEncodingException, URISyntaxException {
		ActivationJDO activation = actiDao.findById(licenseId, id);

		if (activation == null) {
			return Response.status(Status.NOT_FOUND).build();
		} else {
			activation.setActive(!activation.isActive());
			actiDao.persist(activation);
			return Response.seeOther(createUri(activation.getLicense().getId().getId(), "okMessage", "Activation status changed successfully")).build();
		}
	}

	@GET
	@Path("/{licenseId}/delete/{id}")
	public Response delete(@PathParam("licenseId") long licenseId, @PathParam("id") long id) throws UnsupportedEncodingException, URISyntaxException {
		ActivationJDO activation = actiDao.findById(licenseId, id);

		if (activation == null) {
			return Response.status(Status.NOT_FOUND).build();
		} else {
			actiDao.delete(activation);
			return Response.seeOther(createUri(activation.getLicense().getId().getId(), "okMessage", "Activation deleted successfully")).build();
		}
	}

	private static URI createUri(long licenseId, String prop, String val) throws UnsupportedEncodingException, URISyntaxException {
		return new URI("/web/activations/" + licenseId + "?" + prop + "=" + URLEncoder.encode(val, Utils.UTF_8));
	}
}
