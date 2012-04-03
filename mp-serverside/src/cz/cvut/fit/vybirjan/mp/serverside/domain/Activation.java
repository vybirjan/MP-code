package cz.cvut.fit.vybirjan.mp.serverside.domain;

import java.util.Date;

public interface Activation {

	boolean isActive();

	Date getDateCreated();

	String getFingerprint();

}
