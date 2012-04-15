package cz.cvut.fit.vybirjan.mp.clientside.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "cz.cvut.fit.vybirjan.mp.clientside.ui.messages"; //$NON-NLS-1$
	public static String LicenseActivationDialog_Error_ConnectionFailed;
	public static String LicenseActivationDialog_Error_RetrieveFailed;
	public static String LicenseActivationDialog_Error_VerificationFailed;
	public static String LicenseActivationDialog_LicenseNumber;
	public static String LicenseActivationDialog_RetrieveingLicense;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
