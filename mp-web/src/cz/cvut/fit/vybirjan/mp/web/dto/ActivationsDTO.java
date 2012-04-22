package cz.cvut.fit.vybirjan.mp.web.dto;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;
import cz.cvut.fit.vybirjan.mp.web.model.ActivationJDO;
import cz.cvut.fit.vybirjan.mp.web.model.LicenseJDO;

public class ActivationsDTO {

	public static class TableItem {
		private long id;
		private Date dateActivated;
		private boolean active;
		private List<HardwareFingerprint> fingerprints;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getDateActivated() {
			return dateActivated == null ? DTO.NONE : DTO.format(dateActivated);
		}

		public void setDateActivated(Date dateActivated) {
			this.dateActivated = dateActivated;
		}

		public String getActive() {
			return String.valueOf(active);
		}

		public void setActive(boolean active) {
			this.active = active;
		}

		public List<HardwareFingerprint> getFingerprints() {
			return fingerprints;
		}

		public void setFingerprints(List<HardwareFingerprint> fingerprints) {
			this.fingerprints = fingerprints;
		}

	}

	public ActivationsDTO(LicenseJDO license) {
		licenseName = license.getNumber();
		licenseId = license.getId().getId();
		for (ActivationJDO activation : license.getActivations()) {
			TableItem item = new TableItem();
			item.setActive(activation.isActive());
			item.setDateActivated(activation.getDateCreated());
			item.setFingerprints(activation.getFingerprints());
			item.setId(activation.getId().getId());
			tableItems.add(item);
		}
		Collections.sort(tableItems, ITEM_COMPARATOR);
	}

	private static final Comparator<TableItem> ITEM_COMPARATOR = new Comparator<TableItem>() {

		@Override
		public int compare(TableItem o1, TableItem o2) {
			if (o1.dateActivated == null) {
				return -1;
			} else {
				return o1.dateActivated.compareTo(o2.dateActivated);
			}
		}
	};

	private final long licenseId;
	private String licenseName;
	private String okMessage;
	private String errorMessage;

	private final List<TableItem> tableItems = new LinkedList<ActivationsDTO.TableItem>();

	public void setOkMessage(String okMessage) {
		this.okMessage = okMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getOkMessage() {
		return okMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getLicenseName() {
		return licenseName;
	}

	public long getLicenseId() {
		return licenseId;
	}

	public void setLicenseName(String licenseName) {
		this.licenseName = licenseName;
	}

	public List<TableItem> getTableItems() {
		return tableItems;
	}
}
