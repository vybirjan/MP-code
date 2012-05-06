package cz.cvut.fit.vybirjan.mp.testapp.extension;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;

import cz.cvut.fit.vybirjan.mp.clientside.LicenseService;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseInformation;
import cz.cvut.fit.vybirjan.mp.testapp.extension.internal.LicensedClass;

public class Command implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		LicensedClass.doLicensedStuff();
		return null;
	}

	@Override
	public boolean isEnabled() {
		LicenseInformation info = LicenseService.getInstance().getCurrent();
		return info != null && info.containsFeature("EXTENDED-FEATURE") != null;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

}
