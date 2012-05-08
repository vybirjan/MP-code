package cz.cvut.fit.vybirjan.mp.testapp;

import java.security.Key;
import java.util.Properties;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import cz.cvut.fit.vybirjan.mp.clientside.LicenseService;
import cz.cvut.fit.vybirjan.mp.clientside.LicenseServiceConfig;
import cz.cvut.fit.vybirjan.mp.common.Utils;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	private static final String CONFIG_FILE = "/config.properties";

	private static final String CONFIG_APPID = "appid";
	private static final String CONFIG_HOST = "host";
	private static final String CONFIG_HTTPS = "https";
	private static final String CONFIG_KEY = "key";

	// The plug-in ID
	public static final String PLUGIN_ID = "mp-testapp"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		Properties props = new Properties();
		props.load(Activator.class.getResourceAsStream(CONFIG_FILE));

		LicenseService
				.configure(new LicenseServiceConfig(props.getProperty(CONFIG_APPID),
						Utils.deserialize(Utils.decode(props.getProperty(CONFIG_KEY)), Key.class),
						props.getProperty(CONFIG_HOST), Boolean.parseBoolean(props.getProperty(CONFIG_HTTPS))));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
