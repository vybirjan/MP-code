package cz.cvut.fit.vybirjan.mp.clientside.internal.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import cz.cvut.fit.vybirjan.mp.clientside.internal.core.LicenseServiceClient;
import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseResponse;

public class RESTServiceClient implements LicenseServiceClient {

	private static final String ACTIVATIONS_SUFFIX = "activations";

	private static final String HTTP_PREFIX = "http://";
	private static final String HTTPS_PREFIX = "https://";

	private static final String PATH_SEPARATOR = "/";

	public RESTServiceClient(String baseurl, boolean secure) {
		this.baseUrl = baseurl;
		this.secure = secure;
	}

	private final String baseUrl;
	private final boolean secure;

	@Override
	public LicenseResponse activateLicense(String licenseNumber, List<HardwareFingerprint> fingerprints) throws IOException {
		throw new IOException("Not implemented, yet... :(");
	}

	@Override
	public LicenseResponse getLicense(String licenseNumber, List<HardwareFingerprint> fingerprints) throws IOException {
		throw new IOException("Not implemented, yet... :(");
	}

	private static URL createActivateUrl(String baseUrl, boolean secure) throws MalformedURLException {
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

	private static URL createGetLicenseURL(String licenseNumber, String baseUrl, boolean secure) throws MalformedURLException {
		StringBuilder urlString = new StringBuilder(createActivateUrl(baseUrl, secure).toExternalForm());
		urlString.append(PATH_SEPARATOR);
		try {
			urlString.append(URLEncoder.encode(licenseNumber, "UTF8"));
			return new URL(urlString.toString());
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError("UTF8 encoding not found");
		}

	}
}
