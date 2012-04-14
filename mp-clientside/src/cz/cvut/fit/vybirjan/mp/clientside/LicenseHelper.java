package cz.cvut.fit.vybirjan.mp.clientside;

import java.io.IOException;
import java.util.concurrent.Executor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;

import cz.cvut.fit.vybirjan.mp.clientside.LicenseCheckException.LicenseCheckErrorType;
import cz.cvut.fit.vybirjan.mp.common.RunnableWithResult;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseInformation;
import cz.cvut.fit.vybirjan.mp.common.comm.ResponseType;

public class LicenseHelper {

	public static abstract class LicenseNumberProviderAdapter implements LicenseNumberProvider {

		@Override
		public void onIOException(IOException e) {
		}

		@Override
		public void onRetrieveException(ResponseType response) {
		}

		@Override
		public void onCheckException(LicenseCheckErrorType type) {
		}

	}

	public interface LicenseNumberProvider {

		String getLicenseNumber();

		void onIOException(IOException e);

		void onRetrieveException(ResponseType response);

		void onCheckException(LicenseCheckErrorType type);

		void destroy();
	}

	public static LicenseInformation getValidLicense(LicenseNumberProvider numberProvider) {
		LicenseService service = LicenseService.getInstance();

		try {
			return service.checkOnline();
		} catch (LicenseRetrieveException e) {
			numberProvider.onRetrieveException(e.getResponseType());
			return loop(numberProvider);
		} catch (LicenseCheckException e) {
			if (e.getErrorType() != LicenseCheckErrorType.NOT_FOUND) {
				numberProvider.onCheckException(e.getErrorType());
			}
			return loop(numberProvider);
		} catch (IOException e) {
			try {
				return service.checkOffline();
			} catch (LicenseCheckException e1) {
				numberProvider.onCheckException(e1.getErrorType());
				return loop(numberProvider);
			}
		} finally {
			numberProvider.destroy();
		}
	}

	public static LicenseInformation getValidLicenseFromUI(final LicenseNumberProvider p, Executor e) {
		if (!isUiThread()) {
			throw new SWTException(SWT.ERROR_THREAD_INVALID_ACCESS);
		}

		RunnableWithResult<LicenseInformation> rwr = new RunnableWithResult<LicenseInformation>() {

			@Override
			protected LicenseInformation runWithResult() {
				return getValidLicense(p);
			}
		};

		e.execute(rwr);

		while (!rwr.isFinished()) {
			if (!Display.getCurrent().readAndDispatch()) {
				Display.getCurrent().sleep();
			}
		}

		return rwr.getResult();
	}

	private static LicenseInformation loop(LicenseNumberProvider provider) {
		String licenseNumber = null;
		LicenseService service = LicenseService.getInstance();
		while ((licenseNumber = provider.getLicenseNumber()) != null) {
			try {
				return service.activateLicense(licenseNumber);
			} catch (LicenseRetrieveException e) {
				provider.onRetrieveException(e.getResponseType());
			} catch (LicenseCheckException e) {
				provider.onCheckException(e.getErrorType());
			} catch (IOException e) {
				provider.onIOException(e);
			}
		}

		return null;
	}

	private static boolean isUiThread() {
		return Thread.currentThread() == Display.getDefault().getThread();
	}

}
