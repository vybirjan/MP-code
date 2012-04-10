package cz.cvut.fit.vybirjan.mp.serverside.impl.comm.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cz.cvut.fit.vybirjan.mp.common.comm.LicenseInformation;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseRequest;

public interface LicenseMarshaller {

	public static class LicenseMarshallException extends Exception {

		private static final long serialVersionUID = 1L;

		public LicenseMarshallException(String message) {
			super(message);
		}

		public LicenseMarshallException(String message, Throwable cause) {
			super(message, cause);
		}

	}

	boolean canUnmarshall(String mimeType);

	boolean canMarshall(String mimeType);

	LicenseRequest unmarshall(String mimeType, String charset, InputStream in) throws LicenseMarshallException, IOException;

	void marshall(LicenseInformation licenseResponse, String mimeType, String[] charsets, OutputStream out) throws LicenseMarshallException, IOException;
}
