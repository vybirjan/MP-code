package cz.cvut.fit.vybirjan.mp.common.crypto;

import javax.crypto.SecretKey;

public interface TaggedKey extends SecretKey {

	int getTag();

}
