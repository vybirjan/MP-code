package cz.cvut.fit.vybirjan.mp.clientside.ui;

import java.io.IOException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.StartupThreading.StartupRunnable;

import cz.cvut.fit.vybirjan.mp.clientside.LicenseCheckException.LicenseCheckErrorType;
import cz.cvut.fit.vybirjan.mp.clientside.LicenseHelper.LicenseNumberProvider;
import cz.cvut.fit.vybirjan.mp.common.comm.ResponseType;

public class LicenseActivationDialog extends TitleAreaDialog implements LicenseNumberProvider {

	public LicenseActivationDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		setBlockOnOpen(false);
		setHelpAvailable(false);
	}

	protected Composite content;
	protected Text txtLicenseNumber;
	protected Composite licenseInputWrapper;
	protected Composite progressWrapper;
	protected StackLayout stackLayout;

	protected String shellTitle = ""; //$NON-NLS-1$

	protected boolean releaseGetNameLoop = false;

	@Override
	public void create() {
		super.create();
		getShell().addShellListener(SHELL_CLOSE_LISTENER);
		getShell().setText(shellTitle);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		content = new Composite(parent, SWT.NONE);
		GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(content);
		GridLayoutFactory.swtDefaults().applyTo(content);
		{
			Composite wrapper = new Composite(content, SWT.NONE);
			GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(wrapper);
			stackLayout = new StackLayout();
			wrapper.setLayout(stackLayout);
			{
				licenseInputWrapper = new Composite(wrapper, SWT.NONE);
				GridLayoutFactory.swtDefaults().numColumns(2).applyTo(licenseInputWrapper);
				{
					Label lbl = new Label(licenseInputWrapper, SWT.NONE);
					lbl.setText(Messages.LicenseActivationDialog_LicenseNumber);
					GridDataFactory.swtDefaults().grab(false, false).align(SWT.BEGINNING, SWT.CENTER).applyTo(lbl);

					txtLicenseNumber = new Text(licenseInputWrapper, SWT.BORDER);
					GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(txtLicenseNumber);
				}

				progressWrapper = new Composite(wrapper, SWT.NONE);
				GridLayoutFactory.fillDefaults().applyTo(progressWrapper);
				{
					Label lbl = new Label(progressWrapper, SWT.NONE);
					lbl.setText(Messages.LicenseActivationDialog_RetrieveingLicense);

					ProgressBar progress = new ProgressBar(progressWrapper, SWT.INDETERMINATE);
					GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.FILL).applyTo(progress);
				}
			}
			setTopControl(licenseInputWrapper);
		}

		return content;
	}

	protected void toWaitMode() {
		setErrorMessage(null);
		setTopControl(progressWrapper);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
		getButton(IDialogConstants.CANCEL_ID).setEnabled(false);
	}

	protected void toInputMode() {
		setTopControl(licenseInputWrapper);
		getButton(IDialogConstants.OK_ID).setEnabled(true);
		getButton(IDialogConstants.CANCEL_ID).setEnabled(true);
	}

	protected void setTopControl(Control c) {
		stackLayout.topControl = c;
		c.getParent().layout();
	}

	@Override
	public String getLicenseNumber() {
		if (!isUiThread()) {
			RunnableWithResult<String> runnable = new RunnableWithResult<String>() {

				@Override
				public String doRun() {
					return getLicenseNumber();
				}
			};
			Display.getDefault().asyncExec(runnable);
			return runnable.waitFor();
		} else {
			createIfNeeded();

			toInputMode();
			while (!releaseGetNameLoop) {
				if (getShell() != null && !getShell().getDisplay().readAndDispatch()) {
					getShell().getDisplay().sleep();
				}
			}
			releaseGetNameLoop = false;
			toWaitMode();

			if (getReturnCode() == IDialogConstants.OK_ID) {
				return txtLicenseNumber.getText();
			} else {
				return null;
			}
		}
	}

	protected void createIfNeeded() {
		if (getShell() == null || getShell().isDisposed()) {
			open();
		}
	}

	@Override
	protected boolean canHandleShellCloseEvent() {
		return false;
	}

	@Override
	public void onIOException(final IOException e) {
		if (!isUiThread()) {
			Display.getDefault().asyncExec(new StartupRunnable() {

				@Override
				public void runWithException() throws Throwable {
					onIOException(e);
				}
			});
		} else {
			createIfNeeded();

			setErrorMessage(Messages.LicenseActivationDialog_Error_ConnectionFailed + e.getMessage());
			// TODO
		}
	}

	@Override
	public void onRetrieveException(final ResponseType response) {
		if (!isUiThread()) {
			Display.getDefault().asyncExec(new StartupRunnable() {

				@Override
				public void runWithException() throws Throwable {
					onRetrieveException(response);
				}
			});
		} else {
			createIfNeeded();
			// TODO
			setErrorMessage(Messages.LicenseActivationDialog_Error_RetrieveFailed + response);
		}
	}

	@Override
	public void onCheckException(final LicenseCheckErrorType type) {
		if (!isUiThread()) {
			Display.getDefault().asyncExec(new StartupRunnable() {

				@Override
				public void runWithException() throws Throwable {
					onCheckException(type);
				}
			});
		} else {
			createIfNeeded();

			setErrorMessage(Messages.LicenseActivationDialog_Error_VerificationFailed + type);
			// TODO
		}
	}

	protected static boolean isUiThread() {
		return Thread.currentThread() == Display.getDefault().getThread();
	}

	@SuppressWarnings("restriction")
	private abstract static class RunnableWithResult<T> extends StartupRunnable {
		protected volatile T result = null;
		protected Object notifier = new Object();

		@Override
		public void runWithException() throws Throwable {
			try {
				result = doRun();
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				synchronized (notifier) {
					notifier.notifyAll();
				}
			}
		}

		protected abstract T doRun();

		public T getResult() {
			return result;
		}

		public T waitFor() {
			synchronized (notifier) {
				try {
					notifier.wait();
				} catch (InterruptedException e) {
				}
			}

			return result;
		}
	}

	@Override
	protected void okPressed() {
		setReturnCode(IDialogConstants.OK_ID);
		releaseGetNameLoop = true;
	}

	@Override
	protected void cancelPressed() {
		setReturnCode(IDialogConstants.CANCEL_ID);
		releaseGetNameLoop = true;
	}

	protected final ShellListener SHELL_CLOSE_LISTENER = new ShellAdapter() {

		@Override
		public void shellClosed(ShellEvent e) {
			setReturnCode(IDialogConstants.CANCEL_ID);
			releaseGetNameLoop = true;
			e.doit = false;
		}

	};

	@Override
	public void destroy() {
		if (!isUiThread()) {
			Display.getDefault().asyncExec(new StartupRunnable() {

				@Override
				public void runWithException() throws Throwable {
					destroy();
				}
			});
		} else {
			if (getShell() != null && !getShell().isDisposed()) {
				getShell().dispose();
			}
		}
	}

	public void setShellTitle(String shellTitle) {
		this.shellTitle = shellTitle;
		if (getShell() != null && !getShell().isDisposed()) {
			getShell().setText(shellTitle);
		}
	}

}
