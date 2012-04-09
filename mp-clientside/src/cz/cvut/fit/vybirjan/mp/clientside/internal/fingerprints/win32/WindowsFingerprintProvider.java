package cz.cvut.fit.vybirjan.mp.clientside.internal.fingerprints.win32;

import java.util.concurrent.atomic.AtomicBoolean;

public class WindowsFingerprintProvider {

	private static final String SERIAL_NUMBER = "SerialNumber";
	private static final String PROCESSOR_ID = "ProcessorId";

	private static final String OS = "Win32_OperatingSystem";
	private static final String BIOS = "Win32_BIOS";
	private static final String BOARD = "Win32_BaseBoard";
	private static final String DISK = "Win32_DiskDrive";
	private static final String PROCESSOR = "Win32_Processor";

	static {
		System.loadLibrary("fplib");
	}

	public static void initializeWmi() {
		initialized.set(initialize());
	}

	public static void destroy() {
		if (initialized.get()) {
			uninitialize();
			initialized.set(false);
		}
	}

	private static native boolean initialize();

	private static native void uninitialize();

	private native Handles createServiceHandles();

	private native void disposeServiceHandles(Handles h);

	private Handles handles = null;

	private static AtomicBoolean initialized = new AtomicBoolean(false);

	public void createServices() throws ServiceInitializationException {
		if (!initialized.get()) {
			initialized.set(initialize());
		}

		if (handles == null) {
			handles = createServiceHandles();
		}
	}

	public void disposeServices() {
		if (handles != null) {
			disposeServiceHandles(handles);
			handles = null;
		}
		if (initialized.get()) {
			uninitialize();
			initialized.set(Boolean.FALSE);
		}
	}

	private String queryWMI(String clazz, String property) throws PropertyReadException {
		if (handles == null) {
			throw new IllegalStateException("Services not created");
		} else {
			return queryWMIInternal(handles, clazz, property);
		}
	}

	private native String queryWMIInternal(Handles handles, String clazz, String property);

	public String getWindowsSN() throws PropertyReadException {
		return queryWMI(OS, SERIAL_NUMBER).trim();
	}

	public String getBiosSN() throws PropertyReadException {
		return queryWMI(BIOS, SERIAL_NUMBER).trim();
	}

	public String getBaseBoardSN() throws PropertyReadException {
		return queryWMI(BOARD, SERIAL_NUMBER).trim();
	}

	public String getDiskSN() throws PropertyReadException {
		return queryWMI(DISK, SERIAL_NUMBER).trim();
	}

	public String getCPUId() throws PropertyReadException {
		return queryWMI(PROCESSOR, PROCESSOR_ID);
	}

	public static void main(String[] args) throws ServiceInitializationException, PropertyReadException {
		WindowsFingerprintProvider provider = new WindowsFingerprintProvider();
		provider.createServices();
		try {
			System.out.format("Windows: '%s'\n", provider.getWindowsSN());
			System.out.format("Disk: '%s'\n", provider.getDiskSN());
			System.out.format("Bios: '%s'\n", provider.getBiosSN());
			System.out.format("Board: '%s'\n", provider.getBaseBoardSN());
			System.out.format("CPU: '%s'\n", provider.getCPUId());

		} finally {
			provider.disposeServices();
		}
	}
}
