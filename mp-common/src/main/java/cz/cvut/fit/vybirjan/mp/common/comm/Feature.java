package cz.cvut.fit.vybirjan.mp.common.comm;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

public class Feature implements Serializable {

	public static final Comparator<Feature> CODE_COMPARATOR = new Comparator<Feature>() {

		@Override
		public int compare(Feature o1, Feature o2) {
			return o1.getCode().compareTo(o2.getCode());
		}
	};

	private static final long serialVersionUID = 1L;

	public Feature(String code, Date validFrom, Date validTo) {
		this.code = code;
		this.validFrom = validFrom == null ? null : (Date) validFrom.clone();
		this.validTo = validTo == null ? null : (Date) validTo.clone();
	}

	private final String code;
	private final Date validFrom;
	private final Date validTo;

	public String getCode() {
		return code;
	}

	public Date getValidFrom() {
		return validFrom == null ? null : (Date) validFrom.clone();
	}

	public Date getValidTo() {
		return validTo == null ? null : (Date) validTo.clone();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Feature[").append(code);
		if (validFrom != null) {
			sb.append(", validFrom=").append(validFrom.getTime());
		}
		if (validTo != null) {
			sb.append(", validTo=").append(validTo.getTime());
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
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
		Feature other = (Feature) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

}
