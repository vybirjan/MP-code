package cz.cvut.fit.vybirjan.mp.web.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.KeyPair;

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
import cz.cvut.fit.vybirjan.mp.common.crypto.Signing;
import cz.cvut.fit.vybirjan.mp.web.dao.EncryptionKeyDAO;
import cz.cvut.fit.vybirjan.mp.web.dto.EncryptionKeysDTO;
import cz.cvut.fit.vybirjan.mp.web.model.EncryptionKeyJDO;

@Path("/web/keys")
@Produces("text/html")
public class KeysController {

	@Inject
	public KeysController(EncryptionKeyDAO dao) {
		this.dao = dao;
	}

	private final EncryptionKeyDAO dao;

	@GET
	public Response getKeys(@QueryParam("created") String created,
			@QueryParam("existing") String existing,
			@QueryParam("notFound") Long id,
			@QueryParam("deleted") Long deleted) {
		EncryptionKeysDTO dto = createDto();

		if (existing != null) {
			dto.setError("Key for application " + existing + "already exists");
		} else if (created != null) {
			dto.setSuccess("Key for application " + created + " was created successfully");
		} else if (deleted != null) {
			dto.setSuccess("Key " + deleted + " was deleted successfully");
		} else if (id != null) {
			dto.setError("Key with id " + id.toString() + " not found");
		}
		return Response.ok(new Viewable("/keys", dto)).build();
	}

	private EncryptionKeysDTO createDto() {
		EncryptionKeysDTO dto = new EncryptionKeysDTO();

		for (EncryptionKeyJDO key : dao.findAll()) {
			dto.addTableItem(key);
		}

		return dto;
	}

	@POST
	@Consumes("application/x-www-form-urlencoded")
	public Response generateNew(@FormParam("appId") String appId) throws UnsupportedEncodingException, URISyntaxException {
		EncryptionKeyJDO key = dao.findByAppId(appId);
		if (key != null) {
			return Response.seeOther(createKeysUri("error", appId)).build();
		} else {
			KeyPair pair = Signing.generateKeyPair();
			EncryptionKeyJDO newKey = new EncryptionKeyJDO();
			newKey.setAppId(appId);
			newKey.setPrivateKey(pair.getPrivate());
			newKey.setPublicKey(pair.getPublic());
			dao.persist(newKey);

			return Response.seeOther(createKeysUri("created", appId)).build();
		}
	}

	@GET
	@Path("delete/{id}")
	public Response delete(@PathParam("id") long id) throws UnsupportedEncodingException, URISyntaxException {
		EncryptionKeyJDO key = dao.findById(id);
		if (key == null) {
			return Response.seeOther(createKeysUri("notFound", String.valueOf(id))).build();
		} else {
			dao.delete(key);
			return Response.seeOther(createKeysUri("deleted", String.valueOf(id))).build();
		}
	}

	protected static URI createKeysUri(String propName, String value) throws UnsupportedEncodingException, URISyntaxException {
		String encoded = URLEncoder.encode(value, Utils.UTF_8);
		return new URI("/web/keys?" + propName + "=" + encoded);
	}

}
