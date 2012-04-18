package cz.cvut.fit.vybirjan.mp.common.comm;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class representing fingerprint unique for computer which runs this
 * application.
 * 
 * @author Jan Vybíral
 * 
 */
@XmlRootElement(name = "hardwareFingerprint")
public class HardwareFingerprint implements Serializable, Comparable<HardwareFingerprint> {

	private static final long serialVersionUID = 1L;

	private HardwareFingerprint() {

	}

	/**
	 * Creates fingerprint with name and value
	 * 
	 * @param name
	 *            Name of fingerprint
	 * @param value
	 *            Fingerprint
	 */
	public HardwareFingerprint(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public static final String VALUE_SPLIT = ":";
	public static final String ENTRY_SPLIT = ";";

	@XmlAttribute
	private String name;
	@XmlAttribute
	private String value;

	/**
	 * Returns name which identifies type of this fingerprint.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the value of fingerprint as it was collected from machine.
	 * 
	 * @return
	 */
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return name + VALUE_SPLIT + value;
	}

	/**
	 * Loads fingerpring from string representation
	 * 
	 * @param str
	 * @return
	 */
	public static HardwareFingerprint fromString(String str) {
		String[] data = str.split(VALUE_SPLIT);
		if (data.length != 2) {
			throw new IllegalArgumentException("Invalid string");
		}

		HardwareFingerprint f = new HardwareFingerprint(data[0], data[1]);

		return f;
	}

	/**
	 * Saves multiple fingerprints into single String. Useful for fast
	 * comparision. Resulting string is not dependent on order of fingerprints
	 * in provided collection.
	 * 
	 * @param fingerprints
	 *            Collection of fingerprints to write to string
	 * @return Resulting string representation of fingerprints
	 */
	public static String toMultiString(Collection<? extends HardwareFingerprint> fingerprints) {
		List<HardwareFingerprint> copy = new LinkedList<HardwareFingerprint>(fingerprints);
		Collections.sort(copy);

		StringBuilder sb = new StringBuilder();
		for (HardwareFingerprint f : copy) {
			if (sb.length() > 0) {
				sb.append(ENTRY_SPLIT);
			}
			sb.append(f.toString());
		}
		return sb.toString();
	}

	/**
	 * Helper method to convert multiple fingerprints into string
	 * 
	 * @see HardwareFingerprint#toMultiString(Collection)
	 * 
	 */
	public static String toString(HardwareFingerprint... fingerprints) {
		return toMultiString(Arrays.asList(fingerprints));
	}

	/**
	 * Parses fingerprints previously serialized into string by
	 * {@link HardwareFingerprint#toMultiString(Collection)}
	 * 
	 * @param str
	 *            Serialized fingerprints
	 * @return
	 */
	public static List<HardwareFingerprint> fromMultiString(String str) {
		if (str.isEmpty()) {
			return Collections.emptyList();
		}

		String[] items = str.split(ENTRY_SPLIT);
		List<HardwareFingerprint> ret = new LinkedList<HardwareFingerprint>();
		for (String itemStr : items) {
			ret.add(HardwareFingerprint.fromString(itemStr));
		}
		Collections.sort(ret);
		return ret;
	}

	@Override
	public int compareTo(HardwareFingerprint o) {
		int val = name.compareTo(o.getName());
		if (val == 0) {
			val = value.compareTo(o.getValue());
		}
		return val;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HardwareFingerprint other = (HardwareFingerprint) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}