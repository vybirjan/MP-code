package cz.cvut.fit.vybirjan.mp.web.dao;

import cz.cvut.fit.vybirjan.mp.web.model.AssignedFeatureJDO;

public interface AssignedFeatureDAO {

	AssignedFeatureJDO persist(AssignedFeatureJDO feature);

	void delete(AssignedFeatureJDO feature);

}
