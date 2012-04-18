package cz.cvut.fit.vybirjan.mp.web.controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/web/keys")
@Produces("text/html")
public class KeysController {

	@GET
	public Response getKeys() {
		return null;
	}

}
