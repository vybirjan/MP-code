package cz.cvut.fit.vybirjan.mp.common.comm;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import cz.cvut.fit.vybirjan.mp.common.Utils;
import cz.cvut.fit.vybirjan.mp.common.comm.xml.FeatureAdapter;
import cz.cvut.fit.vybirjan.mp.common.comm.xml.TaggedKeyAdapter;
import cz.cvut.fit.vybirjan.mp.common.crypto.Signing;
import cz.cvut.fit.vybirjan.mp.common.crypto.TaggedKey;

@XmlRootElement(name = "licenseInformation")
public final class LicenseInformation implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	private String licenseNumber;

	@XmlJavaTypeAdapter(FeatureAdapter.class)
	@XmlElement(name = "feature")
	@XmlElementWrapper(name = "features")
	private Set<Feature> features;

	@XmlElement(name = "fingerprint")
	@XmlElementWrapper(name = "fingerprints")
	private Set<HardwareFingerprint> fingerPrints;

	@XmlJavaTypeAdapter(TaggedKeyAdapter.class)
	@XmlElement(name = "keys")
	@XmlElementWrapper(name = "key")
	private List<TaggedKey> keys;

	@XmlElement(name = "signature")
	private String signature;

	public void sign(Key privateKey) {
		Signing s = new Signing(privateKey);
		signature = Utils.encode(s.sign(getFingerprint()));
	}

	private byte[] getFingerprint() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(out);
		try {
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

	private static void write(DataOutputStream out, Set<Feature> keys) throws IOException {
		if (keys == null) {
			out.writeByte(0x0);
			return;
		} else if (keys.isEmpty()) {
			out.writeByte((byte) -1);
			return;
		}

		Feature[] keysOrdered = new Feature[keys.size()];
		keys.toArray(keysOrdered);
		Arrays.sort(keysOrdered, Feature.CODE_COMPARATOR);

		out.writeChar('<');
		out.write(Utils.toUtf8(keysOrdered[0].toString()));
		for (int i = 1; i < keysOrdered.length; i++) {
			out.writeChar(',');
			out.write(Utils.toUtf8(keysOrdered[0].toString()));
		}

		out.writeChar('>');
	}

	public void addFeature(Feature feature) {
		if (features == null) {
			features = new HashSet<Feature>();
		}

		features.add(feature);
	}

	public void removeFeature(Feature feature) {
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

	public Set<Feature> getFeatures() {
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

	@Override
	public Object clone() {
		LicenseInformation clone = null;
		try {
			clone = (LicenseInformation) super.clone();
		} catch (CloneNotSupportedException e) {
			// never happens
			throw new AssertionError("LicenseInformation superclass does not support cloning");
		}
		clone.features = features == null ? null : new HashSet<Feature>(features);
		clone.fingerPrints = fingerPrints == null ? null : new HashSet<HardwareFingerprint>(fingerPrints);
		clone.keys = keys == null ? null : new ArrayList<TaggedKey>(keys);

		return clone;
	}

	public static void main(String[] args) throws JAXBException {
		// KeyPair keys = Signing.generateKeyPair();

		JAXBContext context = JAXBContext.newInstance(LicenseRequest.class, LicenseResponse.class, LicenseInformation.class);
		Marshaller marsh = context.createMarshaller();

		LicenseRequest request = new LicenseRequest("AAAA-0112");
		request.addFingerprint(new HardwareFingerprint("XXX", "fooo"));
		marsh.marshal(request, System.out);

		// LicenseInformation info = new LicenseInformation();
		// info.setLicenseNumber("sdafasdf-dddd-ddd");
		// info.addFeature(new Feature("asssss", null, new Date()));
		// info.addFeature(new Feature("another feature", new Date(), new
		// Date()));
		// info.addFingerPrint(new HardwareFingerprint("W01",
		// "dfgsdfgaerQWERf="));
		// info.addKey(new TaggedKeyImpl(55, new SecretKeySpec(new byte[] { 1 },
		// "foo")));
		// info.sign(keys.getPrivate());
		//
		// LicenseResponse resp = LicenseResponse.createdNew(info);
		//
		// ByteArrayOutputStream out = new ByteArrayOutputStream();
		// marsh.marshal(resp, System.out);
		// ByteArrayInputStream in = new
		// ByteArrayInputStream(out.toByteArray());
		//
		// Unmarshaller unmarsh = context.createUnmarshaller();
		// LicenseResponse response = (LicenseResponse) unmarsh.unmarshal(in);
		//
		// System.out.println(response.getLicenseInformation().verify(keys.getPublic()));

	}
}
