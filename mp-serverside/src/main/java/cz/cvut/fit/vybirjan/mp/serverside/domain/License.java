package cz.cvut.fit.vybirjan.mp.serverside.domain;

import java.util.Date;

public interface License {

	boolean isActive();

	boolean isAllowNewActivations();

	String getDescription();

	Integer getMaxActivations();

	String getNumber();

	Date getValidFrom();

	Date getValidTo();
}
