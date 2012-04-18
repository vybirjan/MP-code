package cz.cvut.fit.vybirjan.mp.common.crypto;

import javax.crypto.SecretKey;

/**
 * Implementation of tagged key.
 * 
 * @author Jan Vybíral
 * 
 */
public class TaggedKeyImpl implements TaggedKey {

	private static final long serialVersionUID = 1L;

	public TaggedKeyImpl(int tag, SecretKey key) {
		this.tag = tag;
		this.key = key;
	}

	private final int tag;
	private final SecretKey key;

	@Override
	public String getAlgorithm() {
		return key.getAlgorithm();
	}

	@Override
	public String getFormat() {
		return key.getFormat();
	}

	@Override
	public byte[] getEncoded() {
		return key.getEncoded();
	}

	@Override
	public int getTag() {
		return tag;
	}
}
