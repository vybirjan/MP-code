package cz.cvut.fit.vybirjan.mp.serverside.domain;

import java.util.Date;

public interface License {

	public static final int UNLIMITED = -1;

	boolean isActive();

	boolean isAllowNewActivations();

	String getDescription();

	int getMaxActivations();

	String getNumber();

	Date getValidFrom();

	Date getValidTo();
}
