package cz.cvut.fit.vybirjan.mp.common.comm.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import cz.cvut.fit.vybirjan.mp.common.Utils;
import cz.cvut.fit.vybirjan.mp.common.comm.xml.marshallable.MarshallableTaggedKey;
import cz.cvut.fit.vybirjan.mp.common.crypto.FileEncryptor;
import cz.cvut.fit.vybirjan.mp.common.crypto.TaggedKey;

/**
 * Helper class to serialize tagged key using jax-b
 * 
 * @author Jan Vybíral
 * 
 */
public class TaggedKeyAdapter extends XmlAdapter<MarshallableTaggedKey, TaggedKey> {

	@Override
	public TaggedKey unmarshal(MarshallableTaggedKey v) throws Exception {
		return FileEncryptor.deserializeKey(Utils.decode(v.value));
	}

	@Override
	public MarshallableTaggedKey marshal(TaggedKey v) throws Exception {
		return new MarshallableTaggedKey(Utils.encode(FileEncryptor.serializeKey(v)));
	}

}
