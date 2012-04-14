package cz.cvut.fit.vybirjan.mp.common.comm.xml;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import cz.cvut.fit.vybirjan.mp.common.Utils;
import cz.cvut.fit.vybirjan.mp.common.comm.xml.marshallable.MarshallableTaggedKey;
import cz.cvut.fit.vybirjan.mp.common.crypto.TaggedKey;
import cz.cvut.fit.vybirjan.mp.common.crypto.TaggedKeyImpl;

public class TaggedKeyAdapter extends XmlAdapter<MarshallableTaggedKey, TaggedKey> {

	@Override
	public TaggedKey unmarshal(MarshallableTaggedKey v) throws Exception {
		byte[] data = Utils.decode(v.value);
		int tag = Utils.toInt(data, 0);
		int algLen = Utils.toInt(data, 4);
		String alg = Utils.fromUtf8(data, 8, algLen);
		int dataOffset = 4 + 4 + algLen;

		return new TaggedKeyImpl(tag, new SecretKeySpec(Arrays.copyOfRange(data, dataOffset, data.length), alg));
	}

	@Override
	public MarshallableTaggedKey marshal(TaggedKey v) throws Exception {
		byte[] tag = Utils.toByteArray(v.getTag());
		byte[] alg = Utils.toUtf8(v.getAlgorithm());
		byte[] value = v.getEncoded();

		ByteArrayOutputStream out = new ByteArrayOutputStream(tag.length + alg.length + value.length + 4);
		out.write(tag);
		out.write(Utils.toByteArray(alg.length));
		out.write(alg);
		out.write(value);

		return new MarshallableTaggedKey(Utils.encode(out.toByteArray()));
	}

}
