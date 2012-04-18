package cz.cvut.fit.vybirjan.mp.web.dto;

import cz.cvut.fit.vybirjan.mp.web.model.LicenseJDO;

public class LicenseEditDTO {

	public LicenseEditDTO(LicenseJDO l) {
		this.id = l.getId().getId();
		this.name = l.getNumber();
		this.active = l.isActive();
		this.allowActivations = l.isAllowNewActivations();
		this.description = l.getDescription();
		this.maxActivations = l.getMaxActivations() == null ? null : l.getMaxActivations().toString();
	}

	public LicenseEditDTO(Long id, String name, boolean active, boolean allowActivations, String description, String maxActivations) {
		this.id = id;
		this.name = name;
		this.active = active;
		this.allowActivations = allowActivations;
		this.description = description;
		this.maxActivations = maxActivations;
	}

	public LicenseEditDTO() {

	}

	private String numberError;
	private String maxActivationsError;

	private Long id;
	private String name;
	private boolean active = true;
	private boolean allowActivations = true;
	private String description = "";
	private String maxActivations;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isAllowActivations() {
		return allowActivations;
	}

	public String getDescription() {
		return description;
	}

	public String getMaxActivations() {
		return maxActivations;
	}

	public void setMaxActivations(String maxActivations) {
		this.maxActivations = maxActivations;
	}

	public String getNumberError() {
		return numberError;
	}

	public String getMaxActivationsError() {
		return maxActivationsError;
	}

	public void setNumberError(String nameError) {
		this.numberError = nameError;
	}

	public void setMaxActivationsError(String maxActivationsError) {
		this.maxActivationsError = maxActivationsError;
	}
}
