package cz.cvut.fit.vybirjan.mp.serverside.domain;

public interface EntityFactory {

	License createLicense();

	Feature createFeature();

	Activation createActivation();

}
