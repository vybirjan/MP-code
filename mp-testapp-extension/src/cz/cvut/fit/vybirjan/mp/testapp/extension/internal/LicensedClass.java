package cz.cvut.fit.vybirjan.mp.testapp.extension.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

/**
 * Classs performing some licensed operation
 * 
 * @author Jan Vybíral
 * 
 */
public class LicensedClass {

	public static void doLicensedStuff() {
		MessageBox box = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_INFORMATION);
		box.setText("Info");
		box.setMessage("Licensed feature is working");
		box.open();
	}

}
