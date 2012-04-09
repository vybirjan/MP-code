package cz.cvut.fit.vybirjan.mp.clientside.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import cz.cvut.fit.vybirjan.mp.clientside.internal.fingerprints.HardwareFingerprintProviderFactory;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext arg0) throws Exception {
		HardwareFingerprintProviderFactory.getProvider().inititalize();
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {

	}

}
