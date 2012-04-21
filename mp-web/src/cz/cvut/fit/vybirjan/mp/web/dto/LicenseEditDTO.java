package cz.cvut.fit.vybirjan.mp.web.dto;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import cz.cvut.fit.vybirjan.mp.web.model.AssignedFeatureJDO;
import cz.cvut.fit.vybirjan.mp.web.model.FeatureJDO;
import cz.cvut.fit.vybirjan.mp.web.model.LicenseJDO;

public class LicenseEditDTO {

	public LicenseEditDTO(LicenseJDO l) {
		this.id = l.getId().getId();
		this.name = l.getNumber();
		this.active = l.isActive();
		this.allowActivations = l.isAllowNewActivations();
		this.description = l.getDescription();
		this.maxActivations = l.getMaxActivations() == null ? null : l.getMaxActivations().toString();
		this.validFrom = l.getValidFrom() == null ? "" : DTO.format(l.getValidFrom());
		this.validTo = l.getValidTo() == null ? "" : DTO.format(l.getValidTo());

		addAssignedFeatures(l.getFeatures());
	}

	public LicenseEditDTO(Long id, String name, boolean active, boolean allowActivations, String description, String maxActivations, String validFrom,
			String validTo) {
		this.id = id;
		this.name = name;
		this.active = active;
		this.allowActivations = allowActivations;
		this.description = description;
		this.maxActivations = maxActivations;
		this.validFrom = validFrom;
		this.validTo = validTo;
	}

	public static class FeatureDTO {

		private long id;
		private String description;
		private Date validFrom;
		private Date validTo;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Date getValidFrom() {
			return validFrom;
		}

		public void setValidFrom(Date validFrom) {
			this.validFrom = validFrom;
		}

		public Date getValidTo() {
			return validTo;
		}

		public void setValidTo(Date validTo) {
			this.validTo = validTo;
		}

	}

	public LicenseEditDTO() {

	}

	private String numberError;
	private String maxActivationsError;
	private String validFromError;
	private String validToError;

	private Long id;
	private String name;
	private boolean active = true;
	private boolean allowActivations = true;
	private String description = "";
	private String maxActivations;
	private String validFrom;
	private String validTo;

	private final List<FeatureDTO> featureComboItems = new LinkedList<FeatureDTO>();
	private final List<FeatureDTO> assignedFeatures = new LinkedList<FeatureDTO>();

	public void addFeatureItems(Collection<FeatureJDO> features) {
		for (FeatureJDO f : features) {
			FeatureDTO item = new FeatureDTO();
			item.setDescription(f.getDescription());
			item.setId(f.getId().getId());
			featureComboItems.add(item);
		}
	}

	public void addAssignedFeatures(Iterable<AssignedFeatureJDO> features) {
		for (AssignedFeatureJDO feature : features) {
			addAssignedFeature(feature);
		}
	}

	public void addAssignedFeature(AssignedFeatureJDO f) {
		FeatureDTO feature = new FeatureDTO();
		feature.setDescription(f.getDescription());
		feature.setId(f.getFeature().getId().getId());
		feature.setValidFrom(f.getValidFrom());
		feature.setValidTo(f.getValidTo());
		assignedFeatures.add(feature);
	}

	public List<FeatureDTO> getAssignedFeatures() {
		return assignedFeatures;
	}

	public List<FeatureDTO> getFeatureComboItems() {
		return featureComboItems;
	}

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

	public String getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom == null ? "" : DTO.format(validFrom);
	}

	public String getValidFromError() {
		return validFromError;
	}

	public void setValidFromError(String validFromError) {
		this.validFromError = validFromError;
	}

	public String getValidTo() {
		return validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo == null ? "" : DTO.format(validTo);
	}

	public String getValidToError() {
		return validToError;
	}

	public void setValidToError(String validToError) {
		this.validToError = validToError;
	}

	public String getNow() {
		return DTO.format(new Date());
	}
}
