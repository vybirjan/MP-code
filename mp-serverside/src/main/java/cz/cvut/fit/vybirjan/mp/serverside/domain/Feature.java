package cz.cvut.fit.vybirjan.mp.serverside.domain;

import java.util.Date;

import cz.cvut.fit.vybirjan.mp.common.crypto.TaggedKey;

public interface Feature {

	String getCode();

	String getDescription();

	Date getValidFrom();

	Date getValidTo();

	TaggedKey getKey();

}
