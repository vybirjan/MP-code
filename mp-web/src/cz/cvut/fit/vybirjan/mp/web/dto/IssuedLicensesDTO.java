package cz.cvut.fit.vybirjan.mp.web.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.cvut.fit.vybirjan.mp.web.model.LicenseJDO;

public class IssuedLicensesDTO {

	public static class TableRecord {

		private String number;
		private String description;
		private String dateFrom;
		private String dateTo;
		private String active;
		private String key;
		private String maxAxtivations;
		private long id;

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = DTO.notNull(number);
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = DTO.notNull(description);
		}

		public String getDateFrom() {
			return dateFrom;
		}

		public void setDateFrom(Date dateFrom) {
			this.dateFrom = DTO.format(dateFrom);
		}

		public String getDateTo() {
			return dateTo;
		}

		public void setDateTo(Date dateTo) {
			this.dateTo = DTO.format(dateTo);
		}

		public String getActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = Boolean.toString(active);
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getMaxAxtivations() {
			return maxAxtivations;
		}

		public void setMaxAxtivations(Integer maxAxtivations) {
			if (maxAxtivations == null) {
				this.maxAxtivations = "unlimited";
			} else {
				this.maxAxtivations = maxAxtivations.toString();
			}
		}

	}

	private final List<TableRecord> tableRecords = new ArrayList<IssuedLicensesDTO.TableRecord>();
	private int totalCount;
	private String okMessage;
	private String errorMessage;

	public int getTotalCount() {
		return totalCount;
	}

	public List<TableRecord> getTableRecords() {
		return tableRecords;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getOkMessage() {
		return okMessage;
	}

	public void setOkMessage(String okMessage) {
		this.okMessage = okMessage;
	}

	public void addTableRecord(LicenseJDO license) {
		TableRecord rec = new TableRecord();
		rec.setActive(license.isActive());
		rec.setDateFrom(license.getValidFrom());
		rec.setDateTo(license.getValidTo());
		rec.setDescription(license.getDescription());
		rec.setNumber(license.getNumber());
		rec.setId(license.getId().getId());
		rec.setMaxAxtivations(license.getMaxActivations());

		tableRecords.add(rec);
		totalCount++;
	}

}
