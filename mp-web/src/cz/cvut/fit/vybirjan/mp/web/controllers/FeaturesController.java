package cz.cvut.fit.vybirjan.mp.web.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.sun.jersey.api.view.Viewable;

import cz.cvut.fit.vybirjan.mp.common.Utils;
import cz.cvut.fit.vybirjan.mp.common.crypto.FileEncryptor;
import cz.cvut.fit.vybirjan.mp.common.crypto.TaggedKey;
import cz.cvut.fit.vybirjan.mp.web.dao.FeatureDAO;
import cz.cvut.fit.vybirjan.mp.web.dto.DTO;
import cz.cvut.fit.vybirjan.mp.web.dto.FeaturesDTO;
import cz.cvut.fit.vybirjan.mp.web.model.FeatureJDO;

@Produces("text/html")
@Path("/web/features")
public class FeaturesController {

	@Inject
	public FeaturesController(FeatureDAO dao) {
		this.dao = dao;
	}

	private final FeatureDAO dao;

	private FeaturesDTO createDto() {
		FeaturesDTO dto = new FeaturesDTO();
		for (FeatureJDO feature : dao.findAll()) {
			dto.addTableItem(feature);
		}

		return dto;
	}

	@GET
	public Response getFeatures(@QueryParam("okMessage") String okMessage, @QueryParam("errorMessage") String errorMessage) {
		FeaturesDTO dto = createDto();
		if (okMessage != null) {
			dto.setOkMessage(okMessage);
		} else if (errorMessage != null) {
			dto.setErrorMessage(errorMessage);
		}

		return Response.ok(new Viewable("/features", dto)).build();
	}

	@POST
	@Consumes("application/x-www-form-urlencoded")
	public Response saveFeatures(
			@FormParam("code") String code,
			@FormParam("description") String description,
			@FormParam("key") String key,
			@FormParam("generate") String generate) throws UnsupportedEncodingException, URISyntaxException {

		if (DTO.isNullOrEmpty(code) || DTO.isNullOrEmpty(description) || (DTO.isNullOrEmpty(key) && generate == null)) {
			return Response.seeOther(createKeysUri("errorMessage", "Saving failed - all fields must be filled")).build();
		}

		FeatureJDO feature = dao.findByCode(code);
		if (feature != null) {
			return Response.seeOther(createKeysUri("errorMessage", "Saving failed - feature with code " + code + " already exists")).build();
		}

		TaggedKey taggedKey = null;
		if (generate != null) {
			taggedKey = FileEncryptor.generateDefaultKey(1);// TODO
		} else {
			try {
				taggedKey = FileEncryptor.deserializeKey(Utils.decode(key));
			} catch (Exception e) {
				return Response.seeOther(createKeysUri("errorMessage", "Saving failed - invalid key format")).build();
			}
		}

		feature = new FeatureJDO();
		feature.setCode(code);
		feature.setDescription(description);
		feature.setTaggedKey(taggedKey);

		dao.persist(feature);

		return Response.seeOther(createKeysUri("okMessage", "Feature " + code + " saved successfully")).build();
	}

	@GET
	@Path("delete/{id}")
	public Response deleteFeature(@PathParam("id") long id) throws UnsupportedEncodingException, URISyntaxException {
		FeatureJDO feature = dao.findById(id);
		if (feature == null) {
			return Response.seeOther(createKeysUri("errorMessage", "Feature " + id + " not found")).build();
		} else {
			dao.delete(feature);
			return Response.seeOther(createKeysUri("okMessage", "Feature " + feature.getCode() + " deleted successfully")).build();
		}
	}

	protected static URI createKeysUri(String propName, String value) throws UnsupportedEncodingException, URISyntaxException {
		String encoded = URLEncoder.encode(value, Utils.UTF_8);
		return new URI("/web/features?" + propName + "=" + encoded);
	}
}
