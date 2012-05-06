package cz.cvut.fit.vybirjan.mp.serverside.impl.jaxrs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import cz.cvut.fit.vybirjan.mp.common.Utils;
import cz.cvut.fit.vybirjan.mp.common.comm.Feature;
import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseInformation;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseResponse;
import cz.cvut.fit.vybirjan.mp.common.crypto.FileEncryptor;
import cz.cvut.fit.vybirjan.mp.common.crypto.TaggedKey;

/**
 * <p>
 * Writer which can write {@link LicenseResponse} entity as a simple HTML web
 * page.
 * </p>
 * 
 * <p>
 * Supports content types compatible with text/html
 * </p>
 * 
 * @author Jan Vybíral
 * 
 */
@Provider
public class SimpleHTMLResponseBodyWriter implements MessageBodyWriter<LicenseResponse> {

	@Override
	public long getSize(LicenseResponse arg0, Class<?> arg1, Type arg2, Annotation[] arg3, MediaType arg4) {
		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> arg0, Type arg1, Annotation[] arg2, MediaType arg3) {
		return LicenseResponse.class.isAssignableFrom(arg0) && arg3.isCompatible(MediaType.TEXT_HTML_TYPE);
	}

	@Override
	public void writeTo(LicenseResponse arg0, Class<?> arg1, Type arg2, Annotation[] arg3, MediaType arg4, MultivaluedMap<String, Object> arg5,
			OutputStream arg6) throws IOException, WebApplicationException {

		PrintWriter pw = new PrintWriter(new OutputStreamWriter(arg6, Utils.UTF_8));
		try {
			// write head
			pw.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\">" +
					"<html>" +
					"<head>" +
					"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
					"</head><body>");
			pw.write("<h1>License Response</h1>");
			pw.format("<strong>Type: </strong>%s</br></br>", arg0.getType());

			if (arg0.getLicenseInformation() != null) {
				LicenseInformation info = arg0.getLicenseInformation();

				pw.format("<strong>License number: </strong>%s</br>", info.getLicenseNumber());

				// features
				if (info.getFeatures() != null && !info.getFeatures().isEmpty()) {
					pw.write("<h2>Features</h2>");
					pw.write("<table border=\"1\"><thead><tr><th>Code</th><th>Description</th><th>Valid from</th><th>Valid to</th></tr></thead><tbody>");
					for (Feature f : info.getFeatures()) {
						pw.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", f.getCode(), f.getDescription(), f.getValidFrom(), f.getValidTo());
					}
					pw.write("</tbody></table>");
				}

				// fingerprints
				if (info.getFingerPrints() != null && !info.getFingerPrints().isEmpty()) {
					pw.write("<h2>Fingerprints</h2>");
					pw.write("<table border=\"1\"><thead><tr><th>Name</th><th>Value</th></tr></thead><tbody>");
					for (HardwareFingerprint f : info.getFingerPrints()) {
						pw.format("<tr><td>%s</td><td>%s</td></tr>", f.getName(), f.getValue());
					}
					pw.write("</tbody></table>");
				}

				// keys
				// fingerprints
				if (info.getKeys() != null && !info.getKeys().isEmpty()) {
					pw.write("<h2>Keys</h2>");
					pw.write("<table border=\"1\"><thead><tr><th>Tag</th><th>Value</th></tr></thead><tbody>");
					for (TaggedKey k : info.getKeys()) {
						pw.format("<tr><td>%d</td><td>%s</td></tr>", k.getTag(), Utils.encode(FileEncryptor.serializeKey(k)));
					}
					pw.write("</tbody></table>");
				}

			}

			pw.write("</body></html>");
		} finally {
			pw.close();
		}
	}
}
