package cz.cvut.fit.vybirjan.mp.serverside.impl.jaxrs;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;

import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseRequest;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseResponse;
import cz.cvut.fit.vybirjan.mp.serverside.core.LicenseManager;

@Path("/activations")
/**
 * JAX-RS resource exposing REST API for clients to use to obtain license informaion.
 * 
 * @author Jan Vybíral
 *
 */
public class LicenseResource {

	@Context
	private Providers providers;

	private final Object mgrLock = new Object();
	private LicenseManager mgr;

	@POST
	public Response createNewActivation(@Context UriInfo uriInfo, LicenseRequest request) {
		LicenseResponse response = getLicenseManager().activateLicense(request);

		return createResponse(uriInfo, response);
	}

	@GET
	@Path("/{licenseNumber}/{fingerprints}")
	public Response getExistingActivation(@Context UriInfo uriInfo,
			@PathParam("licenseNumber") String licenseNumber,
			@PathParam("fingerprints") String fingerPrints,
			@QueryParam("appid") String appId) {

		if (appId == null) {
			return Response.status(Status.BAD_REQUEST).entity("Missing parameter appid").build();
		}

		List<HardwareFingerprint> fingerprints = null;
		try {
			fingerprints = HardwareFingerprint.fromMultiString(fingerPrints);
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity("Invalid fingerprints").build();
		}

		LicenseRequest req = new LicenseRequest(appId, licenseNumber);
		req.addFingerprints(fingerprints);

		LicenseResponse response = getLicenseManager().getLicense(req);

		return createResponse(uriInfo, response);
	}

	private Response createResponse(UriInfo info, LicenseResponse resp) {
		switch (resp.getType()) {
			case ERROR_EXPIRED:
			case ERROR_INACTIVE:
			case ERROR_NEW_ACTIVATIONS_NOT_ALLOWED:
			case ERROR_TOO_MANY_ACTIVATIONS:
				return Response.status(Status.FORBIDDEN).entity(resp).build();
			case ERROR_BAD_REQUEST:
				return Response.status(Status.BAD_REQUEST).entity(resp).build();
			case ERROR_LICENSE_NOT_FOUND:
				return Response.status(Status.NOT_FOUND).entity(resp).build();
			case OK_EXISTING_VERIFIED:
				return Response.ok(resp).build();
			case OK_NEW_CREATED:
				return Response.created(info.getRequestUri()).entity(resp).build();
			default:
				return Response.serverError().build();
		}
	}

	private LicenseManager getLicenseManager() {
		if (mgr == null) {
			synchronized (mgrLock) {
				if (mgr == null) {
					mgr = findLicenseManager();
				}
			}
		}

		return mgr;
	}

	private LicenseManager findLicenseManager() {
		ContextResolver<LicenseManager> resolver = providers.getContextResolver(LicenseManager.class, MediaType.WILDCARD_TYPE);

		if (resolver == null) {
			throw new IllegalStateException(String.format("LicenseManager not found. Please register ContextResolver capable of resolving '%s'",
					LicenseManager.class.getName()));
		} else {
			return resolver.getContext(LicenseManager.class);
		}
	}

}
