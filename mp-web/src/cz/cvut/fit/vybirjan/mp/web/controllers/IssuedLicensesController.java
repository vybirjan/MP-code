package cz.cvut.fit.vybirjan.mp.web.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.inject.Inject;
import com.sun.jersey.api.view.Viewable;

import cz.cvut.fit.vybirjan.mp.common.Utils;
import cz.cvut.fit.vybirjan.mp.web.dao.LicenseDAO;
import cz.cvut.fit.vybirjan.mp.web.dto.DTO;
import cz.cvut.fit.vybirjan.mp.web.dto.IssuedLicensesDTO;
import cz.cvut.fit.vybirjan.mp.web.dto.LicenseEditDTO;
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
	public Response getLicenses(@QueryParam("createOk") String createOk,
			@QueryParam("updateOk") String updateOk,
			@QueryParam("del") String del,
			@QueryParam("err") String err) {
		IssuedLicensesDTO dto = createIssuedLicensesDTO();
		dto.setErrorMessage(err);
		if (createOk != null) {
			dto.setOkMessage("License " + createOk + " created successfully");
		} else if (updateOk != null) {
			dto.setOkMessage("License " + updateOk + " updated successfully");
		} else if (del != null) {
			dto.setOkMessage("License " + del + " deleted successfully");
		}

		return Response.ok(new Viewable("/licenses", dto)).build();
	}

	protected IssuedLicensesDTO createIssuedLicensesDTO() {
		List<LicenseJDO> licenses = licDao.findAll();

		IssuedLicensesDTO data = new IssuedLicensesDTO();
		for (LicenseJDO license : licenses) {
			data.addTableRecord(license);
		}

		return data;
	}

	@GET
	@Path("edit/{id}")
	public Response edit(@PathParam("id") long id) {
		LicenseJDO l = licDao.findById(id);

		if (l != null) {
			LicenseEditDTO dto = new LicenseEditDTO(l);
			return Response.ok(new Viewable("/license-form", dto)).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	@POST
	@Path("edit/{id}")
	@Consumes("application/x-www-form-urlencoded")
	public Response editSave(@PathParam("id") long id,
			@FormParam("number") String number,
			@FormParam("active") String active,
			@FormParam("description") String description,
			@FormParam("numOfActivations") String numOfActivations,
			@FormParam("allowActivations") String allowActivations) throws URISyntaxException {
		LicenseJDO l = licDao.findById(id);

		if (l != null) {
			/*
			 * Update license number
			 */
			if (DTO.isNullOrEmpty(number)) {
				// error empty number
				LicenseEditDTO dto = new LicenseEditDTO(id, number, active != null, allowActivations != null, description, numOfActivations);
				dto.setNumberError("License number must not be empty");
				return Response.ok(new Viewable("/license-form", dto)).build();

			} else {
				if (!number.equals(l.getNumber())) {
					LicenseJDO byNumber = licDao.findByNumber(number);
					if (byNumber != null && !byNumber.getId().equals(l)) {
						// error number taken
						LicenseEditDTO dto = new LicenseEditDTO(id, number, active != null, allowActivations != null, description, numOfActivations);
						dto.setNumberError("License with given number already exists");
						return Response.ok(new Viewable("/license-form", dto)).build();
					} else {
						l.setNumber(number);
					}
				}
			}
			/*
			 * Update description
			 */
			l.setDescription(description == null ? "" : description);
			/*
			 * Update Active state
			 */
			l.setActive(active != null);
			/*
			 * Update allow activations
			 */
			l.setAllowedNewActivations(allowActivations != null);
			/*
			 * Update number of licenses
			 */
			if (DTO.isNullOrEmpty(numOfActivations)) {
				l.setMaxActivation(null);
			} else {
				try {
					l.setMaxActivation(Integer.parseInt(numOfActivations));
				} catch (NumberFormatException e) {
					// error invalid number
					LicenseEditDTO dto = new LicenseEditDTO(id, number, active != null, allowActivations != null, description, numOfActivations);
					dto.setMaxActivationsError("Invalid numeric value");
					return Response.ok(new Viewable("/license-form", dto)).build();
				}
			}

			licDao.persist(l);
			return Response.seeOther(createActivationsUri("updateOk", l.getNumber())).build();
		} else {
			// no license found
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	protected static URI createActivationsUri(String propName, String message) throws URISyntaxException {
		try {
			String encoded = URLEncoder.encode(message, Utils.UTF_8);
			return new URI("/web/licenses?" + propName + "=" + encoded);
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError("UTF8 encoding not available");
		}
	}

	@GET
	@Path("new")
	public Response createNew() {
		LicenseEditDTO dto = new LicenseEditDTO();
		return Response.ok(new Viewable("/license-form", dto)).build();
	}

	@POST
	@Path("new")
	@Consumes("application/x-www-form-urlencoded")
	public Response saveNew(
			@FormParam("number") String number,
			@FormParam("active") String active,
			@FormParam("description") String description,
			@FormParam("numOfActivations") String numOfActivations,
			@FormParam("allowActivations") String allowActivations) throws URISyntaxException {

		LicenseJDO newLicense = new LicenseJDO();

		// validate number
		if (DTO.isNullOrEmpty(number)) {
			LicenseEditDTO dto = new LicenseEditDTO(null, number, active != null, allowActivations != null, description, numOfActivations);
			dto.setNumberError("Number must not be empty");
			return Response.ok(new Viewable("/license-form", dto)).build();
		} else {
			LicenseJDO existing = licDao.findByNumber(number);
			if (existing != null) {
				LicenseEditDTO dto = new LicenseEditDTO(null, number, active != null, allowActivations != null, description, numOfActivations);
				dto.setNumberError("License with given number already exists");
				return Response.ok(new Viewable("/license-form", dto)).build();
			} else {
				newLicense.setNumber(number);
			}
		}

		// validate max activations
		if (!DTO.isNullOrEmpty(numOfActivations)) {
			try {
				newLicense.setMaxActivation(Integer.parseInt(numOfActivations));
			} catch (NumberFormatException e) {
				LicenseEditDTO dto = new LicenseEditDTO(null, number, active != null, allowActivations != null, description, numOfActivations);
				dto.setMaxActivationsError("Invalid numeric value");
				return Response.ok(new Viewable("/license-form", dto)).build();
			}
		}

		newLicense.setActive(active != null);
		newLicense.setAllowedNewActivations(allowActivations != null);
		newLicense.setDescription(description);

		newLicense = licDao.persist(newLicense);

		return Response.seeOther(createActivationsUri("createOk", newLicense.getNumber())).build();
	}

	@GET
	@Path("delete/{id}")
	public Response delete(@PathParam("id") long id) throws URISyntaxException {
		LicenseJDO license = licDao.findById(id);

		if (license == null) {
			return Response.seeOther(createActivationsUri("err", "License " + id + " not found")).build();
		} else {
			licDao.delete(license);
			return Response.seeOther(createActivationsUri("del", String.valueOf(id))).build();
		}
	}

}
