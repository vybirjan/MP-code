package cz.cvut.fit.vybirjan.mp.clientside.internal.hook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.osgi.baseadaptor.HookConfigurator;
import org.eclipse.osgi.baseadaptor.HookRegistry;
import org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry;
import org.eclipse.osgi.baseadaptor.loader.ClasspathEntry;
import org.eclipse.osgi.baseadaptor.loader.ClasspathManager;
import org.eclipse.osgi.internal.baseadaptor.BaseClassLoadingHook;

import cz.cvut.fit.vybirjan.mp.common.Utils;
import cz.cvut.fit.vybirjan.mp.common.crypto.FileEncryptor;
import cz.cvut.fit.vybirjan.mp.common.crypto.FileEncryptor.ProcessStrategy;
import cz.cvut.fit.vybirjan.mp.common.crypto.TaggedKey;

@SuppressWarnings("restriction")
public class SecurityHook extends BaseClassLoadingHook implements
		HookConfigurator {

	private static final File MSG_FOLDER = new File(System.getProperty(
			"eclipse.home.location").substring(5)
			+ File.separator + "workspace" + File.separator + ".metadata");
	private static final File MSG_FILE = new File(MSG_FOLDER, ".hook.tmp");
	private static final int MSG_ADDKEY = 0xAC;
	private static final int MSG_CLEARKEYS = 0xCC;

	private static Map<Integer, ProcessStrategy<Object>> keys = new HashMap<Integer, ProcessStrategy<Object>>();

	private static final TaggedKey DEFAULT_KEY = FileEncryptor
			.deserializeKey(Utils
					.decode("AAAAAAAAAANBRVPKa6fgQr2m+cnt8PwBSpxk"));

	public SecurityHook() {
		synchronized (keys) {
			keys.put(DEFAULT_KEY.getTag(),
					FileEncryptor.createDefaultDecryptStrategy(DEFAULT_KEY));
		}
	}

	private static class InstanceHolder {
		static final SecurityHook INSTANCE = new SecurityHook();
	}

	public static void addKeys(Iterable<? extends TaggedKey> keys) {
		synchronized (SecurityHook.keys) {
			for (TaggedKey key : keys) {
				addKey(key);
			}
		}
	}

	public static void addKey(TaggedKey key) {
		sendKey(key);
	}

	public static void clearKeys() {
		sendClearKeys();
	}

	private static void clearKeysInternal() {
		synchronized (keys) {
			Iterator<Integer> iterator = keys.keySet().iterator();

			while (iterator.hasNext()) {
				Integer next = iterator.next();
				if (next != 0) { // delete all but default
					iterator.remove();
				}
			}
		}
	}

	@Override
	public void addHooks(HookRegistry registry) {
		try {
			startListener();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		registry.addClassLoadingHook(InstanceHolder.INSTANCE);
	}

	@Override
	public byte[] processClass(String name, byte[] classbytes,
			ClasspathEntry classpathEntry, BundleEntry entry,
			ClasspathManager manager) {
		if (classbytes[0] == FileEncryptor.HEAD) {
			Integer tag = Integer.valueOf(Utils.toInt(classbytes, 1));
			ProcessStrategy<Object> strategy = getStrategy(tag);
			if (strategy != null) {
				byte[] ret = decrypt(classbytes, strategy);
				return ret;
			} else {
				throw new IllegalStateException("Cannot decrypt class " + name
						+ " - no strategy available for tag " + tag);
			}

		} else {
			return classbytes;
		}
	}

	private ProcessStrategy<Object> getStrategy(int tag) {
		synchronized (keys) {
			return keys.get(tag);
		}
	}

	protected static byte[] decrypt(byte[] data,
			ProcessStrategy<Object> strategy) {

		ByteArrayInputStream in = new ByteArrayInputStream(data, 5,
				data.length - 5);
		ByteArrayOutputStream out = new ByteArrayOutputStream(data.length - 5
				- FileEncryptor.DEFAULT_IV_SIZE);

		try {
			strategy.process(null, in, out);
			return out.toByteArray();
		} catch (IOException e) {
			// won't happen
			throw new AssertionError("Exception during decryption");
		}
	}

	private static void waitForFile(File f) throws IOException {
		try {
			int tries = 0;
			while (MSG_FILE.length() > 0) {
				if (tries > 100) {
					throw new IOException("Wait failed");
				} else {
					Thread.sleep(100);
					tries++;
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private synchronized static void sendKey(TaggedKey key) {
		try {
			byte[] data = FileEncryptor.serializeKey(key);

			waitForFile(MSG_FILE);

			FileOutputStream out = new FileOutputStream(MSG_FILE);
			try {
				out.write(MSG_ADDKEY);
				out.write(data);
			} finally {
				out.close();
			}
			waitForFile(MSG_FILE);

		} catch (IOException e) {
		}
	}

	private synchronized static void sendClearKeys() {
		try {
			waitForFile(MSG_FILE);

			FileOutputStream in = new FileOutputStream(MSG_FILE);
			try {
				in.write(MSG_CLEARKEYS);
			} finally {
				in.close();
			}

			waitForFile(MSG_FILE);
		} catch (IOException e) {
		}
	}

	public static void startListener() throws IOException {
		Listener l = new Listener();
		Thread listenerThread = new Thread(l, "Hook listener thread");
		listenerThread.setDaemon(true);
		listenerThread.start();
	}

	private static class Listener implements Runnable {

		public Listener() throws IOException {
			MSG_FOLDER.mkdirs();

			f = MSG_FILE;
			if (f.exists()) {
				clearFile(f);
			} else {
				f.createNewFile();
			}
			f.deleteOnExit();
		}

		private final File f;
		private final byte[] buffer = new byte[256];

		@Override
		public void run() {
			try {
				while (!Thread.interrupted()) {
					if (f.length() > 0) {
						synchronized (keys) {
							FileInputStream in = new FileInputStream(f);
							try {
								int read = in.read();
								switch (read) {
									case MSG_ADDKEY:
										handleAddKey(in);
										break;
									case MSG_CLEARKEYS:
										clearKeysInternal();
								}
							} finally {
								in.close();
							}
							clearFile(MSG_FILE);
						}
					} else {
						Thread.sleep(100);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private static void clearFile(File f) throws IOException {
			FileOutputStream out = new FileOutputStream(f);
			out.close();
		}

		private void handleAddKey(InputStream in) throws IOException {
			int read = in.read(buffer);
			try {
				TaggedKey key = FileEncryptor.deserializeKey(buffer, read);
				keys.put(key.getTag(),
						FileEncryptor.createDefaultDecryptStrategy(key));
			} catch (Exception e) {
			}
		}

	}
}
