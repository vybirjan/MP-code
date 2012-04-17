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

	}

	private final List<TableRecord> tableRecords = new ArrayList<IssuedLicensesDTO.TableRecord>();

	public List<TableRecord> getTableRecords() {
		return tableRecords;
	}

	public void addTableRecord(LicenseJDO license) {
		TableRecord rec = new TableRecord();
		rec.setActive(license.isActive());
		rec.setDateFrom(license.getValidFrom());
		rec.setDateTo(license.getValidTo());
		rec.setDescription(license.getDescription());

		tableRecords.add(rec);
	}

}
