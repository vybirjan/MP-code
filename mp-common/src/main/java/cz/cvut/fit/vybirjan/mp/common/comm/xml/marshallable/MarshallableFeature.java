package cz.cvut.fit.vybirjan.mp.common.comm.xml.marshallable;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MarshallableFeature {

	public MarshallableFeature() {
	}

	public MarshallableFeature(String code, Date validFrom, Date validTo) {
		this.code = code;
		this.validFrom = validFrom;
		this.validTo = validTo;
	}

	public String code;
	public Date validFrom;
	public Date validTo;
}
