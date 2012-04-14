package cz.cvut.fit.vybirjan.mp.common.comm.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import cz.cvut.fit.vybirjan.mp.common.comm.Feature;
import cz.cvut.fit.vybirjan.mp.common.comm.xml.marshallable.MarshallableFeature;

public class FeatureAdapter extends XmlAdapter<MarshallableFeature, Feature> {

	@Override
	public Feature unmarshal(MarshallableFeature v) throws Exception {
		return new Feature(v.code, v.validFrom, v.validTo);
	}

	@Override
	public MarshallableFeature marshal(Feature v) throws Exception {
		return new MarshallableFeature(v.getCode(), v.getValidFrom(), v.getValidTo());
	}
}
