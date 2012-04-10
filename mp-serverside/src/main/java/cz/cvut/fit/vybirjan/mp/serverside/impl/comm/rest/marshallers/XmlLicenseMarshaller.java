package cz.cvut.fit.vybirjan.mp.serverside.impl.comm.rest.marshallers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cz.cvut.fit.vybirjan.mp.common.comm.LicenseInformation;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseRequest;
import cz.cvut.fit.vybirjan.mp.serverside.impl.comm.rest.LicenseMarshaller;

public class XmlLicenseMarshaller implements LicenseMarshaller {

	private static final String TEXT_XML = "text/xml";
	private static final String APP_XML = "application/xml";

	@Override
	public boolean canUnmarshall(String mimeType) {
		return APP_XML.equalsIgnoreCase(mimeType) || TEXT_XML.equalsIgnoreCase(TEXT_XML);
	}

	@Override
	public boolean canMarshall(String mimeType) {
		return APP_XML.equalsIgnoreCase(mimeType) || TEXT_XML.equalsIgnoreCase(TEXT_XML);
	}

	@Override
	public LicenseRequest unmarshall(String mimeType, String charset, InputStream in) throws LicenseMarshallException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void marshall(LicenseInformation licenseResponse, String mimeType, String[] charsets, OutputStream out) throws LicenseMarshallException, IOException {
		// TODO Auto-generated method stub

	}

}
