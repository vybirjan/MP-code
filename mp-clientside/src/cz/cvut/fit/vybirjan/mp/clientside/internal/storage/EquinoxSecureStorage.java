package cz.cvut.fit.vybirjan.mp.clientside.internal.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

import cz.cvut.fit.vybirjan.mp.clientside.internal.core.SecureStorage;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseInformation;

public class EquinoxSecureStorage implements SecureStorage {

	private static final String OBJECT_NAME = "security.license";

	@Override
	public void save(LicenseInformation info) throws IOException {
		ISecurePreferences preferences = SecurePreferencesFactory.getDefault();

		try {
			preferences.putByteArray(OBJECT_NAME, serialize(info), true);
			preferences.flush();
		} catch (StorageException e) {
			throw new IOException("Faield to store license information", e);
		}
	}

	@Override
	public LicenseInformation loadInfo() throws IOException {
		ISecurePreferences preferences = SecurePreferencesFactory.getDefault();

		try {
			byte[] data = preferences.getByteArray(OBJECT_NAME, null);
			return deserialize(data);
		} catch (StorageException e) {
			throw new IOException("Failed to load license info", e);
		}
	}

	@Override
	public void clear() throws IOException {
		ISecurePreferences preferences = SecurePreferencesFactory.getDefault();

		try {
			preferences.putByteArray(OBJECT_NAME, null, true);
		} catch (StorageException e) {
			throw new IOException("Clearing preferences failed", e);
		}
	}

	private static byte[] serialize(Serializable o) throws IOException {
		if (o == null) {
			return null;
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream objOut = new ObjectOutputStream(out);
		objOut.writeObject(o);
		objOut.close();

		return out.toByteArray();
	}

	public static LicenseInformation deserialize(byte[] data) throws IOException {
		if (data == null) {
			return null;
		}

		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream objIn = new ObjectInputStream(in);

		try {
			return (LicenseInformation) objIn.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException("Failed to deserialize license", e);
		}
	}
}
