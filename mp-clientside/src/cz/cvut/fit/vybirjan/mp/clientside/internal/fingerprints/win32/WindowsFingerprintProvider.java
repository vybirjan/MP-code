package cz.cvut.fit.vybirjan.mp.clientside.internal.fingerprints.win32;

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

	private final ThreadLocal<Handles> handles = new ThreadLocal<Handles>();

	private static ThreadLocal<Boolean> initialized = new ThreadLocal<Boolean>() {
		@Override
		protected Boolean initialValue() {
			return Boolean.FALSE;
		}
	};

	public void createServices() throws ServiceInitializationException {
		if (!initialized.get()) {
			initialized.set(initialize());
		}

		if (handles.get() == null) {
			handles.set(createServiceHandles());
		}
	}

	public void disposeServices() {
		if (handles.get() != null) {
			disposeServiceHandles(handles.get());
			handles.set(null);
		}
	}

	private String queryWMI(String clazz, String property) throws PropertyReadException {
		if (handles.get() == null) {
			throw new IllegalStateException("Services not created");
		} else {
			return queryWMIInternal(handles.get(), clazz, property);
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
