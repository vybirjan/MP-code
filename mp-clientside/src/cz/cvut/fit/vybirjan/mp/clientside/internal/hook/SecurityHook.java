package cz.cvut.fit.vybirjan.mp.clientside.internal.hook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
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

	private static final byte HEAD = (byte) 0xEC;

	private static Map<Integer, ProcessStrategy<Object>> keys = new HashMap<Integer, ProcessStrategy<Object>>();

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

	@Override
	public void addHooks(HookRegistry arg0) {

	}

	@Override
	public byte[] processClass(String name, byte[] classbytes, ClasspathEntry classpathEntry, BundleEntry entry, ClasspathManager manager) {
		if (classbytes[0] == HEAD) {
			Integer tag = Integer.valueOf(Utils.toInt(classbytes, 1));
			ProcessStrategy<Object> strategy = keys.get(tag);
			if (strategy != null) {
				return decrypt(classbytes, strategy);
			} else {
				throw new IllegalStateException("No strategy available for tag " + tag);
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
