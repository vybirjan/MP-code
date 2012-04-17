package cz.cvut.fit.vybirjan.mp.clientside.internal.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import cz.cvut.fit.vybirjan.mp.clientside.internal.core.LicenseServiceClient;
import cz.cvut.fit.vybirjan.mp.common.Utils;
import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseRequest;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseResponse;

public class RESTServiceClient implements LicenseServiceClient {

	private static final String ACTIVATIONS_SUFFIX = "activations";

	private static final String HTTP_PREFIX = "http://";
	private static final String HTTPS_PREFIX = "https://";

	private static final String PATH_SEPARATOR = "/";

	private static final Marshaller MARSHALLER;
	private static final Unmarshaller UNMARSHALLER;
	private static final JAXBContext JAXB_CONTEXT;

	private static final String GET = "GET";
	private static final String POST = "POST";

	private static final String HEADER_ACCEPT = "Accept";
	private static final String HEADER_CONTENT_TYPE = "Content-Type";

	private static final String MIME_TEXT_XML_UTF8 = "text/xml; charset=utf-8";

	static {
		try {
			JAXB_CONTEXT = JAXBContext.newInstance(LicenseRequest.class, LicenseResponse.class);
			MARSHALLER = JAXB_CONTEXT.createMarshaller();
			UNMARSHALLER = JAXB_CONTEXT.createUnmarshaller();
		} catch (JAXBException e) {
			throw new AssertionError("Could not create JAXB context");
		}
	}

	public RESTServiceClient(String baseurl, boolean secure) {
		this.baseUrl = baseurl;
		this.secure = secure;
	}

	private final String baseUrl;
	private final boolean secure;

	@Override
	public LicenseResponse activateLicense(String licenseNumber, List<HardwareFingerprint> fingerprints) throws IOException {
		LicenseRequest request = new LicenseRequest(licenseNumber);
		request.addFingerprints(fingerprints);

		HttpURLConnection connection = createConnection(POST);
		try {
			sendRequest(request, connection);
			return readResponse(connection);
		} finally {
			connection.disconnect();
		}
	}

	private static void sendRequest(LicenseRequest o, HttpURLConnection connection) throws IOException {
		OutputStream out = connection.getOutputStream();
		try {
			MARSHALLER.marshal(o, new OutputStreamWriter(out, Utils.UTF_8));
		} catch (JAXBException e) {
			throw new IOException("Failed to serialize request", e);
		} finally {
			out.close();
		}
	}

	private static LicenseResponse readResponse(HttpURLConnection connection) throws IOException {
		if (connection.getResponseCode() >= HttpURLConnection.HTTP_INTERNAL_ERROR) {
			return LicenseResponse.internalError();
		}

		InputStream in = null;
		try {
			in = connection.getInputStream();
		} catch (IOException e) {
			// on 404 getInputStream throws FileNotFound exception
			in = connection.getErrorStream();
		}

		try {
			return (LicenseResponse) UNMARSHALLER.unmarshal(in);
		} catch (ClassCastException e) {
			return LicenseResponse.communicationError();
		} catch (JAXBException e) {
			return LicenseResponse.communicationError();
		} finally {
			in.close();
		}
	}

	private HttpURLConnection createConnection(String method) throws IOException {
		URL url = createUrl(baseUrl, secure);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod(method);
		connection.addRequestProperty(HEADER_ACCEPT, MIME_TEXT_XML_UTF8);
		connection.addRequestProperty(HEADER_CONTENT_TYPE, MIME_TEXT_XML_UTF8);

		return connection;
	}

	@Override
	public LicenseResponse getLicense(String licenseNumber, List<HardwareFingerprint> fingerprints) throws IOException {
		LicenseRequest request = new LicenseRequest(licenseNumber);
		request.addFingerprints(fingerprints);

		HttpURLConnection connection = createConnection(GET);
		try {
			sendRequest(request, connection);
			return readResponse(connection);
		} finally {
			connection.disconnect();
		}
	}

	private static URL createUrl(String baseUrl, boolean secure) throws MalformedURLException {
		StringBuilder sb = new StringBuilder();
		if (secure) {
			sb.append(HTTPS_PREFIX);
		} else {
			sb.append(HTTP_PREFIX);
		}
		sb.append(baseUrl);
		if (!baseUrl.endsWith(PATH_SEPARATOR)) {
			sb.append(PATH_SEPARATOR);
		}
		sb.append(ACTIVATIONS_SUFFIX);

		return new URL(sb.toString());
	}
}
