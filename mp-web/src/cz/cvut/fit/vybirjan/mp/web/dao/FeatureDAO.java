package cz.cvut.fit.vybirjan.mp.web.dao;

import java.util.List;

import cz.cvut.fit.vybirjan.mp.web.model.FeatureJDO;

public interface FeatureDAO {

	FeatureJDO findByCode(String code);

	FeatureJDO findById(long id);

	List<FeatureJDO> findAll();

	FeatureJDO persist(FeatureJDO feature);

	void delete(FeatureJDO feature);
}
