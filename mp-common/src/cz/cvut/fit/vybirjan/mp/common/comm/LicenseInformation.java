package cz.cvut.fit.vybirjan.mp.common.comm;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.Key;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import cz.cvut.fit.vybirjan.mp.common.Utils;
import cz.cvut.fit.vybirjan.mp.common.crypto.Signing;
import cz.cvut.fit.vybirjan.mp.common.crypto.TaggedKey;

public final class LicenseInformation implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date validFrom;
	private Date validTo;
	private String licenseNumber;
	private Set<String> features;
	private Set<HardwareFingerprint> fingerPrints;
	private List<TaggedKey> keys;

	private String signature;

	public void sign(Key privateKey) {
		Signing s = new Signing(privateKey);
		signature = Utils.encode(s.sign(getFingerprint()));
	}

	private byte[] getFingerprint() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(out);
		try {
			write(dataOut, validFrom);
			dataOut.writeChar(',');
			write(dataOut, validTo);
			dataOut.writeChar(',');
			dataOut.write(Utils.toUtf8(licenseNumber));
			dataOut.writeChar(',');
			if (fingerPrints == null) {
				dataOut.writeByte(0x0);
			} else {
				dataOut.write(Utils.toUtf8(HardwareFingerprint.toMultiString(fingerPrints)));
			}

			write(dataOut, features);

			dataOut.close();
		} catch (IOException e) {
			// won't happen
		}

		return out.toByteArray();
	}

	private static void write(DataOutputStream out, Date d) throws IOException {
		if (d == null) {
			out.writeByte(0x0);
		} else {
			out.writeLong(d.getTime());
		}
	}

	private static void write(DataOutputStream out, Set<String> keys) throws IOException {
		if (keys == null) {
			out.writeByte(0x0);
			return;
		} else if (keys.isEmpty()) {
			out.writeByte((byte) -1);
			return;
		}

		String[] keysOrdered = new String[keys.size()];
		keys.toArray(keysOrdered);
		Arrays.sort(keysOrdered);

		out.writeChar('<');
		out.write(Utils.toUtf8(keysOrdered[0]));
		for (int i = 1; i < keysOrdered.length; i++) {
			out.writeChar(',');
			out.write(Utils.toUtf8(keysOrdered[0]));
		}

		out.writeChar('>');
	}

	public void addFeature(String feature) {
		if (features == null) {
			features = new HashSet<String>();
		}

		features.add(feature);
	}

	public void removeFeature(String feature) {
		if (features != null) {
			features.remove(feature);
		}
	}

	public void addFingerPrint(HardwareFingerprint fp) {
		if (fingerPrints == null) {
			fingerPrints = new HashSet<HardwareFingerprint>();
		}

		fingerPrints.add(fp);
	}

	public void removeFingerPrint(HardwareFingerprint fp) {
		if (fingerPrints != null) {
			fingerPrints.remove(fp);
		}
	}

	public Set<HardwareFingerprint> getFingerPrints() {
		if (fingerPrints == null) {
			return Collections.emptySet();
		} else {
			return Collections.unmodifiableSet(fingerPrints);
		}
	}

	public Set<String> getFeatures() {
		if (features == null) {
			return Collections.emptySet();
		} else {
			return Collections.unmodifiableSet(features);
		}
	}

	public void addKey(TaggedKey key) {
		if (keys == null) {
			keys = new LinkedList<TaggedKey>();
		}

		keys.add(key);
	}

	public void removeKey(TaggedKey key) {
		if (keys != null) {
			keys.remove(key);
		}
	}

	public List<TaggedKey> getKeys() {
		if (keys == null) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(keys);
		}
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidTo() {
		return validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	public String getLicenseNumber() {
		return licenseNumber;
	}

	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	public boolean isSigned() {
		return signature != null;
	}

	public boolean verify(Key publicKey) {
		if (!isSigned()) {
			throw new IllegalStateException("License information is not signed");
		}
		Signing s = new Signing(publicKey);
		return s.verify(getFingerprint(), Utils.decode(signature));
	}
}
