package cz.cvut.fit.vybirjan.mp.testapp.about;

import java.text.DateFormat;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import cz.cvut.fit.vybirjan.mp.clientside.LicenseCheckException;
import cz.cvut.fit.vybirjan.mp.clientside.LicenseHelper;
import cz.cvut.fit.vybirjan.mp.clientside.LicenseService;
import cz.cvut.fit.vybirjan.mp.clientside.LicenseService.LicenseChangedListener;
import cz.cvut.fit.vybirjan.mp.clientside.ui.JobExecutor;
import cz.cvut.fit.vybirjan.mp.clientside.ui.LicenseActivationDialog;
import cz.cvut.fit.vybirjan.mp.common.comm.Feature;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseInformation;

public class TestappAboutDialog extends TitleAreaDialog implements LicenseChangedListener {

	private static DateFormat DATE_FORMAT = DateFormat.getDateInstance();

	public TestappAboutDialog(Shell parentShell) {
		super(parentShell);
	}

	private Text txtLicense;
	private TableViewer features;

	@Override
	public void create() {
		super.create();
		setTitle("Test App");
		setMessage("License information");
		getShell().setText("About");
		try {
			setLicenseInfo(LicenseService.getInstance().getCurrent());
		} catch (LicenseCheckException e) {
			PlatformUI.getWorkbench().close();
		}
		LicenseService.getInstance().addLicenseChangedListener(this);
		getShell().addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				LicenseService.getInstance().removeLicenseChangedListener(TestappAboutDialog.this);
			}
		});
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite wrapper = new Composite(parent, SWT.NONE);
		GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(wrapper);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(wrapper);
		{
			Label lbl = new Label(wrapper, SWT.NONE);
			lbl.setText("License number:");

			txtLicense = new Text(wrapper, SWT.READ_ONLY);
			txtLicense.setBackground(wrapper.getBackground());
			GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(txtLicense);

			lbl = new Label(wrapper, SWT.NONE);
			lbl.setText("Available features:");
			GridDataFactory.swtDefaults().span(2, 1).applyTo(lbl);

			features = new TableViewer(wrapper);
			GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).span(2, 1).applyTo(features.getControl());
			features.setContentProvider(FEATURES_CONTENT_PROVIDER);
			features.getTable().setHeaderVisible(true);
			createColumns(features);
		}

		return wrapper;
	}

	protected void createColumns(TableViewer table) {
		TableViewerColumn col = new TableViewerColumn(table, SWT.NONE);
		col.getColumn().setText("Code");
		col.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell arg0) {
				Feature feature = (Feature) arg0.getElement();
				arg0.setText(feature.getCode());
			}
		});

		col = new TableViewerColumn(table, SWT.NONE);
		col.getColumn().setText("Description");
		col.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell arg0) {
				Feature feature = (Feature) arg0.getElement();
				arg0.setText(feature.getDescription());
			}
		});

		col = new TableViewerColumn(table, SWT.NONE);
		col.getColumn().setText("Valid from");
		col.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell arg0) {
				Feature feature = (Feature) arg0.getElement();
				arg0.setText(feature.getValidFrom() == null ? "-" : DATE_FORMAT.format(feature.getValidFrom()));
			}
		});

		col = new TableViewerColumn(table, SWT.NONE);
		col.getColumn().setText("Valid to");
		col.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell arg0) {
				Feature feature = (Feature) arg0.getElement();
				arg0.setText(feature.getValidTo() == null ? "-" : DATE_FORMAT.format(feature.getValidTo()));
			}
		});
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		Button changeLicense = createButton(parent, -1, "Change license", false);
		changeLicense.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeLicense();
			}
		});
	}

	protected void changeLicense() {
		LicenseService service = LicenseService.getInstance();

		LicenseInformation currentInfo = null;
		try {
			currentInfo = service.getCurrent();
		} catch (LicenseCheckException e1) {
		}

		String oldNumber = null;

		if (currentInfo != null) {
			oldNumber = currentInfo.getLicenseNumber();
		}

		service.clearCurrentLicense();

		LicenseActivationDialog dlg = new LicenseActivationDialog(getShell());
		dlg.setTitle("Provide license number");
		dlg.setMessage("Please insert your license number");
		dlg.setShellTitle("License activation");
		LicenseInformation newInfo = null;
		try {
			newInfo = LicenseHelper.getValidLicenseFromUI(dlg, new JobExecutor("Getting license"));
		} finally {
			if (newInfo == null) {
				try {
					service.activateLicense(oldNumber);
				} catch (Exception e) {
					MessageBox err = new MessageBox(getShell(), SWT.ICON_ERROR);
					err.setMessage("Failed to restore old license, application will now close");
					err.setText("Error");
					err.open();
					PlatformUI.getWorkbench().close();
				}

			}
		}
	}

	protected void setLicenseInfo(LicenseInformation info) {
		txtLicense.setText(info.getLicenseNumber());
		features.setInput(info);
		for (TableColumn col : features.getTable().getColumns()) {
			col.pack();
		}
	}

	private static final IContentProvider FEATURES_CONTENT_PROVIDER = new IStructuredContentProvider() {

		@Override
		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {

		}

		@Override
		public void dispose() {

		}

		@Override
		public Object[] getElements(Object arg0) {
			LicenseInformation info = (LicenseInformation) arg0;

			return info.getFeatures().toArray();
		}
	};

	@Override
	public void onLicenseChanged(final LicenseInformation newInfo) {
		if (Thread.currentThread() != Display.getDefault().getThread()) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					onLicenseChanged(newInfo);
				}
			});
		} else {
			setLicenseInfo(newInfo);
		}
	}
}
