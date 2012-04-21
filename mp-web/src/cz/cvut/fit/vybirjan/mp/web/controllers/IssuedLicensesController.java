package cz.cvut.fit.vybirjan.mp.web.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedList;
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
import cz.cvut.fit.vybirjan.mp.web.dao.AssignedFeatureDAO;
import cz.cvut.fit.vybirjan.mp.web.dao.FeatureDAO;
import cz.cvut.fit.vybirjan.mp.web.dao.LicenseDAO;
import cz.cvut.fit.vybirjan.mp.web.dto.DTO;
import cz.cvut.fit.vybirjan.mp.web.dto.IssuedLicensesDTO;
import cz.cvut.fit.vybirjan.mp.web.dto.LicenseEditDTO;
import cz.cvut.fit.vybirjan.mp.web.model.AssignedFeatureJDO;
import cz.cvut.fit.vybirjan.mp.web.model.FeatureJDO;
import cz.cvut.fit.vybirjan.mp.web.model.LicenseJDO;

@Produces("text/html")
@Path("/web/licenses")
public class IssuedLicensesController {

	@Inject
	public IssuedLicensesController(LicenseDAO licDao, FeatureDAO feDao, AssignedFeatureDAO assfDao) {
		this.licDao = licDao;
		this.feDao = feDao;
		this.assfDao = assfDao;
	}

	private final LicenseDAO licDao;
	private final FeatureDAO feDao;
	private final AssignedFeatureDAO assfDao;

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
			dto.addFeatureItems(feDao.findAll());
			return Response.ok(new Viewable("/license-form", dto)).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	private void updateLicense(LicenseJDO license, List<Long> features, List<String> dateFrom, List<String> dateTo) {
		List<AssignedFeatureJDO> toCreate = new LinkedList<AssignedFeatureJDO>();
		List<AssignedFeatureJDO> toDelete = new LinkedList<AssignedFeatureJDO>();

		Iterator<Long> idIt = features.iterator();
		Iterator<String> fromIt = dateFrom.iterator();
		Iterator<String> toIt = dateTo.iterator();
		// add/edit existing
		while (idIt.hasNext()) {
			Long id = idIt.next();
			String from = fromIt.next();
			String to = toIt.next();

			FeatureJDO feature = feDao.findById(id);
			if (feature != null) {
				AssignedFeatureJDO existing = license.findForFeatureId(id);

				if (existing == null) {
					existing = new AssignedFeatureJDO(feature);
					toCreate.add(existing);
				}
				try {
					existing.setValidFrom(DTO.parseDate(from));
					existing.setValidTo(DTO.parseDate(to));
					if (existing.getValidFrom() != null && existing.getValidTo() != null && existing.getValidFrom().after(existing.getValidTo())) {
						existing.setValidFrom(null);
					}
				} catch (ParseException e) {
				}
			}
		}

		// delete missing
		for (AssignedFeatureJDO assignedFeature : license.getFeatures()) {
			if (!features.contains(assignedFeature.getFeature().getId().getId())) {
				toDelete.add(assignedFeature);
			}
		}

		// delete
		for (AssignedFeatureJDO delete : toDelete) {
			assfDao.delete(delete);
			license.removeFeature(delete);
		}

		// add
		for (AssignedFeatureJDO create : toCreate) {
			license.addFeature(create);
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
			@FormParam("allowActivations") String allowActivations,
			@FormParam("validFrom") String validFrom,
			@FormParam("validTo") String validTo,
			@FormParam("featureId[]") List<Long> featuresIds,
			@FormParam("featureValidFrom[]") List<String> featureValidFrom,
			@FormParam("featureValidTo[]") List<String> featureValidTo) throws URISyntaxException {
		LicenseJDO l = licDao.findById(id);
		LicenseEditDTO dto = new LicenseEditDTO(id, number, active != null, allowActivations != null, description, numOfActivations, validFrom, validTo);
		dto.addFeatureItems(feDao.findAll());
		dto.addAssignedFeatures(l.getFeatures());
		if (l != null) {

			updateLicense(l, featuresIds, featureValidFrom, featureValidTo);

			/*
			 * Update license number
			 */
			if (DTO.isNullOrEmpty(number)) {
				// error empty number

				dto.setNumberError("License number must not be empty");
				return Response.ok(new Viewable("/license-form", dto)).build();

			} else {
				if (!number.equals(l.getNumber())) {
					LicenseJDO byNumber = licDao.findByNumber(number);
					if (byNumber != null && !byNumber.getId().equals(l)) {
						// error number taken
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
			 * Update valid from date
			 */
			if (DTO.isNullOrEmpty(validFrom)) {
				l.setValidFrom(null);
			} else {
				try {
					l.setValidFrom(DTO.parseDate(validFrom));
				} catch (ParseException e) {
					dto.setValidFromError("Invalid date format");
					return Response.ok(new Viewable("/license-form", dto)).build();
				}
			}

			/*
			 * Update valid to date
			 */
			if (DTO.isNullOrEmpty(validTo)) {
				l.setValidTo(null);
			} else {
				try {
					l.setValidTo(DTO.parseDate(validTo));
				} catch (ParseException e) {
					dto.setValidToError("Invalid date format");
					return Response.ok(new Viewable("/license-form", dto)).build();
				}
			}

			if (l.getValidFrom() != null && l.getValidTo() != null && l.getValidFrom().after(l.getValidTo())) {
				dto.setValidFromError("Valid from date must be before valid to date");
				dto.setValidToError("Valid to date must be after valid from date");
				return Response.ok(new Viewable("/license-form", dto)).build();
			}

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
			@FormParam("allowActivations") String allowActivations,
			@FormParam("validFrom") String validFrom,
			@FormParam("validTo") String validTo,
			@FormParam("featureId[]") List<Long> featuresIds,
			@FormParam("featureValidFrom[]") List<String> featureValidFrom,
			@FormParam("featureValidTo[]") List<String> featureValidTo) throws URISyntaxException {

		LicenseJDO newLicense = new LicenseJDO();
		LicenseEditDTO dto = new LicenseEditDTO(null, number, active != null, allowActivations != null, description, numOfActivations, validFrom, validTo);
		dto.addFeatureItems(feDao.findAll());
		updateLicense(newLicense, featuresIds, featureValidFrom, featureValidTo);
		// validate number
		if (DTO.isNullOrEmpty(number)) {
			dto.setNumberError("Number must not be empty");
			return Response.ok(new Viewable("/license-form", dto)).build();
		} else {
			LicenseJDO existing = licDao.findByNumber(number);
			if (existing != null) {
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
				dto.setMaxActivationsError("Invalid numeric value");
				return Response.ok(new Viewable("/license-form", dto)).build();
			}
		}

		/*
		 * Update valid from date
		 */
		if (DTO.isNullOrEmpty(validFrom)) {
			newLicense.setValidFrom(null);
		} else {
			try {
				newLicense.setValidFrom(DTO.parseDate(validFrom));
			} catch (ParseException e) {
				dto.setValidFromError("Invalid date format");
				return Response.ok(new Viewable("/license-form", dto)).build();
			}
		}

		/*
		 * Update valid to date
		 */
		if (DTO.isNullOrEmpty(validTo)) {
			newLicense.setValidTo(null);
		} else {
			try {
				newLicense.setValidTo(DTO.parseDate(validTo));
			} catch (ParseException e) {
				dto.setValidToError("Invalid date format");
				return Response.ok(new Viewable("/license-form", dto)).build();
			}
		}

		if (newLicense.getValidFrom() != null && newLicense.getValidTo() != null && newLicense.getValidFrom().after(newLicense.getValidTo())) {
			dto.setValidFromError("Valid from date must be before valid to date");
			dto.setValidToError("Valid to date must be after valid from date");
			return Response.ok(new Viewable("/license-form", dto)).build();
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
