package cz.cvut.fit.vybirjan.mp.testapp.splashHandlers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.splash.AbstractSplashHandler;

import cz.cvut.fit.vybirjan.mp.clientside.LicenseHelper;
import cz.cvut.fit.vybirjan.mp.clientside.ui.LicenseActivationDialog;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseInformation;

/**
 * @since 3.3
 * 
 */
public class InteractiveSplashHandler extends AbstractSplashHandler {

	@Override
	public void init(Shell splash) {
		super.init(splash);
		LicenseActivationDialog dlg = new LicenseActivationDialog(splash);
		dlg.setTitle("Provide license number");
		dlg.setMessage("Please insert your license number");
		dlg.setShellTitle("License activation");

		LicenseInformation info = null;
		ExecutorService exec = Executors.newSingleThreadExecutor();
		try {
			info = LicenseHelper.getValidLicenseFromUI(dlg, exec, "TEST-APP");
		} finally {
			exec.shutdown();
			if (info == null) {
				System.exit(1);
			}
		}
	}
}
