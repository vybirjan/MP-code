package cz.cvut.fit.vybirjan.mp.clientside.internal.hook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
public class SecurityHook extends BaseClassLoadingHook implements HookConfigurator {

	private static Map<Integer, ProcessStrategy<Object>> keys = new HashMap<Integer, ProcessStrategy<Object>>();

	private static final TaggedKey DEFAULT_KEY = FileEncryptor.deserializeKey(Utils.decode("AAAAAAAAAANBRVPKa6fgQr2m+cnt8PwBSpxk"));

	public SecurityHook() {
		keys.put(0, FileEncryptor.createDefaultDecryptStrategy(DEFAULT_KEY));
	}

	private static class InstanceHolder {
		static final SecurityHook INSTANCE = new SecurityHook();
	}

	public static void addKeys(Iterable<? extends TaggedKey> keys) {
		synchronized (keys) {
			for (TaggedKey key : keys) {
				addKey(key);
			}
		}
	}

	public static void addKey(TaggedKey key) {
		synchronized (keys) {
			keys.put(key.getTag(), FileEncryptor.createDefaultDecryptStrategy(key));
		}
	}

	public static void removeKey(TaggedKey key) {
		synchronized (keys) {
			keys.remove(key.getTag());
		}
	}

	public static void clearKeys() {
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
		registry.addClassLoadingHook(InstanceHolder.INSTANCE);
	}

	@Override
	public byte[] processClass(String name, byte[] classbytes, ClasspathEntry classpathEntry, BundleEntry entry, ClasspathManager manager) {
		if (classbytes[0] == FileEncryptor.HEAD) {
			Integer tag = Integer.valueOf(Utils.toInt(classbytes, 1));
			ProcessStrategy<Object> strategy = keys.get(tag);
			if (strategy != null) {
				byte[] ret = decrypt(classbytes, strategy);
				return ret;
			} else {
				throw new IllegalStateException("Cannot decrypt class " + name + " - no strategy available for tag " + tag);
			}

		} else {
			return classbytes;
		}
	}

	protected static byte[] decrypt(byte[] data, ProcessStrategy<Object> strategy) {

		ByteArrayInputStream in = new ByteArrayInputStream(data, 5, data.length - 5);
		ByteArrayOutputStream out = new ByteArrayOutputStream(data.length - 5 - FileEncryptor.DEFAULT_IV_SIZE);

		try {
			strategy.process(null, in, out);
			return out.toByteArray();
		} catch (IOException e) {
			// won't happen
			throw new AssertionError("Exception during decryption");
		}
	}
}
