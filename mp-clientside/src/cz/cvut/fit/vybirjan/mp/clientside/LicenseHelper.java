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

		@Override
		public void onMissingRequiredFeatures() {
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

		/**
		 * Called when some of required features are missing
		 */
		void onMissingRequiredFeatures();
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
	public static LicenseInformation getValidLicense(LicenseNumberProvider numberProvider, String... requiredFeatures) {
		LicenseService service = LicenseService.getInstance();

		try {
			LicenseInformation info = service.checkOnline();
			if (!containsRequiredFeatures(info, requiredFeatures)) {
				service.clearCurrentLicense();
				numberProvider.onMissingRequiredFeatures();
				return loop(numberProvider, requiredFeatures);
			} else {
				return info;
			}
		} catch (LicenseRetrieveException e) {
			numberProvider.onRetrieveException(e.getResponseType());
			return loop(numberProvider, requiredFeatures);
		} catch (LicenseCheckException e) {
			if (e.getErrorType() != LicenseCheckErrorType.NOT_FOUND) {
				numberProvider.onCheckException(e.getErrorType());
			}
			return loop(numberProvider, requiredFeatures);
		} catch (IOException e) {
			try {
				LicenseInformation info = service.checkOffline();
				if (!containsRequiredFeatures(info, requiredFeatures)) {
					service.clearCurrentLicense();
					numberProvider.onMissingRequiredFeatures();
					return loop(numberProvider, requiredFeatures);
				} else {
					return info;
				}
			} catch (LicenseCheckException e1) {
				numberProvider.onCheckException(e1.getErrorType());
				return loop(numberProvider, requiredFeatures);
			}
		} finally {
			numberProvider.destroy();
		}
	}

	private static boolean containsRequiredFeatures(LicenseInformation info, String[] features) {
		if (features != null && features.length > 0) {
			if (info == null) {
				return false;
			} else {
				for (String code : features) {
					if (info.containsFeature(code) == null) {
						return false;
					}
				}
				return true;
			}
		} else {
			return info != null;
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
	public static LicenseInformation getValidLicenseFromUI(final LicenseNumberProvider p, Executor e, final String... requiredFeatures) {
		if (!isUiThread()) {
			throw new SWTException(SWT.ERROR_THREAD_INVALID_ACCESS);
		}

		RunnableWithResult<LicenseInformation> rwr = new RunnableWithResult<LicenseInformation>() {

			@Override
			protected LicenseInformation runWithResult() {
				return getValidLicense(p, requiredFeatures);
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

	private static LicenseInformation loop(LicenseNumberProvider provider, String[] reqFeatures) {
		String licenseNumber = null;
		LicenseService service = LicenseService.getInstance();
		while ((licenseNumber = provider.getLicenseNumber()) != null) {
			try {
				LicenseInformation info = service.activateLicense(licenseNumber);
				if (!containsRequiredFeatures(info, reqFeatures)) {
					service.clearCurrentLicense();
					provider.onMissingRequiredFeatures();
				} else {
					return info;
				}
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
