package cz.cvut.fit.vybirjan.mp.testapp;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import cz.cvut.fit.vybirjan.mp.clientside.LicenseService;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseInformation;
import cz.cvut.fit.vybirjan.mp.testapp.ui.ApplicationActionBarAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	@Override
	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(600, 400));
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(false);
	}

	@Override
	public void postWindowCreate() {
		LicenseInformation info = LicenseService.getInstance().getCurrent();

		if (info == null || info.containsFeature("TEST-APP") == null) {
			MessageBox box = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_ERROR);
			box.setText("Error");
			box.setMessage("Failed to load license information");
			box.open();
			PlatformUI.getWorkbench().close();
		}
	}

}
