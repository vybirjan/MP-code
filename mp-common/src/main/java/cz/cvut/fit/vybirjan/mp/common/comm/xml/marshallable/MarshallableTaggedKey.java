package cz.cvut.fit.vybirjan.mp.common.comm.xml.marshallable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MarshallableTaggedKey {

	public MarshallableTaggedKey() {

	}

	public MarshallableTaggedKey(String value) {
		this.value = value;
	}

	@XmlAttribute
	public String value;

}
