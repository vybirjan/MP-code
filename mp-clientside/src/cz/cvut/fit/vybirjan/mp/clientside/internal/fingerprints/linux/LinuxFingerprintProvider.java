package cz.cvut.fit.vybirjan.mp.clientside.internal.fingerprints.linux;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cz.cvut.fit.vybirjan.mp.clientside.internal.core.HardwareFingerprintProvider;
import cz.cvut.fit.vybirjan.mp.common.Utils;
import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;

public class LinuxFingerprintProvider implements HardwareFingerprintProvider {

	@Override
	public void inititalize() {
		
	}
	
	private static void tryAddFromCommand(List<HardwareFingerprint> fps, String name, String... cmd) {
		try {
			String output = runCommand(cmd);
			if(output != null && !output.isEmpty()) {
				fps.add(new HardwareFingerprint(name, Utils.encode(Utils.hash(Utils.toUtf8(output)))));
			}
		} catch(Exception e) {
			//ignore
		}
	}

	@Override
	public List<HardwareFingerprint> collectFingerprints() {
		List<HardwareFingerprint> ret = new ArrayList<HardwareFingerprint>(5);
		
		tryAddFromCommand(ret, "L00", "hostid");
		tryAddFromCommand(ret, "L01", "lspci");
		
		return ret;
	}

	@Override
	public void destroy() {
		
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		LinuxFingerprintProvider p = new LinuxFingerprintProvider();
		System.out.println(p.collectFingerprints());
	}
	
	private static String runCommand(String... cmd) {
		String output = null;
		try {
			ProcessBuilder builder = new ProcessBuilder(cmd);
			Process p = builder.start();
			StringBuilder sb = new StringBuilder();
			InputStreamReader reader = new InputStreamReader(p.getInputStream());
			try {
				char[] buffer = new char[1024];
				int read = 0;
				while((read = reader.read(buffer)) != -1) {
					sb.append(buffer, 0, read);
				}
			} finally {
				reader.close();
			}
			output = sb.toString();
			
			if (p.waitFor() != 0) {
				// program exited with error
				return null;
			}

			return output;

		} catch (IOException e) {
			return null;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return output;
		}
	}

}
