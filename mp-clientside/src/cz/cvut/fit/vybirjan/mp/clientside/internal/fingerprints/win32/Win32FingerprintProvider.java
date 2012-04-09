package cz.cvut.fit.vybirjan.mp.clientside.internal.fingerprints.win32;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import cz.cvut.fit.vybirjan.mp.clientside.internal.core.HardwareFingerprintProvider;
import cz.cvut.fit.vybirjan.mp.common.Utils;
import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;

public class Win32FingerprintProvider implements HardwareFingerprintProvider {

	@Override
	public List<HardwareFingerprint> collectFingerprints() {
		List<HardwareFingerprint> fps = new LinkedList<HardwareFingerprint>();

		WindowsFingerprintProvider fpProvider = new WindowsFingerprintProvider();

		try {
			fpProvider.createServices();

			try {
				String productId = fpProvider.getWindowsSN();
				if (!productId.isEmpty()) {
					fps.add(createFingerprint("W01", productId));
				}
			} catch (PropertyReadException e) {
				e.printStackTrace();
			}

			try {
				String boardSn = fpProvider.getBaseBoardSN();
				if (!boardSn.isEmpty()) {
					fps.add(createFingerprint("W02", boardSn));
				}
			} catch (PropertyReadException e) {
				e.printStackTrace();
			}

			try {
				String biosSn = fpProvider.getBiosSN();
				if (!biosSn.isEmpty()) {
					fps.add(createFingerprint("W03", biosSn));
				}
			} catch (PropertyReadException e) {
				e.printStackTrace();
			}

			try {
				String cpuId = fpProvider.getCPUId();
				if (!cpuId.isEmpty()) {
					fps.add(createFingerprint("W04", cpuId));
				}
			} catch (PropertyReadException e) {
				e.printStackTrace();
			}

			try {
				String diskSn = fpProvider.getDiskSN();
				if (!diskSn.isEmpty()) {
					fps.add(createFingerprint("W05", diskSn));
				}
			} catch (PropertyReadException e) {
				e.printStackTrace();
			}

		} catch (ServiceInitializationException e) {
			// failed to inititalize service
		} finally {
			fpProvider.disposeServices();
		}

		return fps;
	}

	private static HardwareFingerprint createFingerprint(String name, String value) {
		try {
			value = Utils.encode(Utils.hash((name + value).getBytes("UTF8")));
			return new HardwareFingerprint(name, value);
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError("UTF8 encoding not found");
		}
	}

	@Override
	public void inititalize() {
		WindowsFingerprintProvider.initializeWmi();
	}

	@Override
	public void destroy() {
		WindowsFingerprintProvider.destroy();
	}
}
