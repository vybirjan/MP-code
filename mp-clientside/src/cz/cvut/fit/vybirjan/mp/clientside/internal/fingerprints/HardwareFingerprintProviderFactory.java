package cz.cvut.fit.vybirjan.mp.clientside.internal.fingerprints;

import cz.cvut.fit.vybirjan.mp.clientside.internal.core.HardwareFingerprintProvider;
import cz.cvut.fit.vybirjan.mp.clientside.internal.fingerprints.win32.Win32FingerprintProvider;

public class HardwareFingerprintProviderFactory {

	private static final String PROVIDER_CLASS = "cz.cvut.fit.vybirjan.mp.clientside.internal.fingerprints.provided.HardwareFingerprintProviderImpl";

	private static final String OS_NAME = "os.name";

	public static HardwareFingerprintProvider getProvider() {
		return InstanceHolder.INSTANCE;
	}

	private static class InstanceHolder {
		private static HardwareFingerprintProvider INSTANCE = createInstance();
	}

	private static HardwareFingerprintProvider createInstance() {
		if (isWindows()) {
			return createWin32Instance();
		} else if (isLinux()) {
			return createLinuxinstance();
		} else {
			throw new AssertionError("Unsupported OS: " + System.getProperty(OS_NAME));
		}
	}

	private static HardwareFingerprintProvider createWin32Instance() {
		return new Win32FingerprintProvider();
	}

	private static HardwareFingerprintProvider createLinuxinstance() {
		throw new AssertionError("Not implemented");
	}

	public static boolean isWindows() {
		return System.getProperty(OS_NAME).toLowerCase().contains("windows");
	}

	public static boolean isLinux() {
		return System.getProperty(OS_NAME).toLowerCase().contains("linux");
	}
}
