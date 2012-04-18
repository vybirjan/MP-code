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

/**
 * Helper class to ease working with license service.
 * 
 * @author Jan Vybíral
 * 
 */
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

	/**
	 * Callback interface used during license verification.
	 * 
	 * @author Jan Vybíral
	 * 
	 */
	public interface LicenseNumberProvider {

		/**
		 * Called when license number is needed in order to get license
		 * information.
		 * 
		 * @return License number or null to stop further requests.
		 */
		String getLicenseNumber();

		/**
		 * Called when IOException occured when requesting license.
		 * 
		 * @param e
		 */
		void onIOException(IOException e);

		/**
		 * Called when retrieving license failed
		 * 
		 * @param response
		 */
		void onRetrieveException(ResponseType response);

		/**
		 * Check when verification of received or locally stored license
		 * occured.
		 * 
		 * @param type
		 */
		void onCheckException(LicenseCheckErrorType type);

		/**
		 * Called when provider is no longer needed.
		 */
		void destroy();
	}

	/**
	 * Tries to obtain valid license. May call provided
	 * {@link LicenseNumberProvider} to obtain additional information.
	 * 
	 * @param numberProvider
	 * @return Valid license information or null, if provided
	 *         LicenseNumberProvider returned null on
	 *         {@link LicenseNumberProvider#getLicenseNumber()} call.
	 */
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

	/**
	 * <p>
	 * Tries to obtain valid license. May call provided
	 * {@link LicenseNumberProvider} to obtain additional information.
	 * </p>
	 * <p>
	 * Method is expected to be called from UI thread, all calls to
	 * LicenseNumberProvider are asynchronous, dispatching UI events is not
	 * interrupted when waiting for response.
	 * </p>
	 * 
	 * @param p
	 * @param e
	 * @return
	 */
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

	/**
	 * Indicates whether current thread is SWT ui thread.
	 */
	private static boolean isUiThread() {
		return Thread.currentThread() == Display.getDefault().getThread();
	}

}
