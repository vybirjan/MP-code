package cz.cvut.fit.vybirjan.mp.web.dto;

import cz.cvut.fit.vybirjan.mp.web.model.LicenseJDO;

public class LicenseEditDTO {

	public LicenseEditDTO(LicenseJDO l) {
		this.id = l.getId().getId();
		this.name = l.getNumber();
		this.active = l.isActive();
		this.allowActivations = l.isAllowNewActivations();
		this.description = l.getDescription();
	}

	public LicenseEditDTO() {

	}

	private Long id;
	private String name;
	private boolean active = true;
	private boolean allowActivations = true;
	private String description = "";

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
}
