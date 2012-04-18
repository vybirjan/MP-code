package cz.cvut.fit.vybirjan.mp.common.comm.xml.marshallable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import cz.cvut.fit.vybirjan.mp.common.crypto.TaggedKey;

/**
 * Marshallable representation of {@link TaggedKey}
 * 
 * @author Jan Vybíral
 * 
 */
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
