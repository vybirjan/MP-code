package cz.cvut.fit.vybirjan.mp.web.dto;

import java.util.LinkedList;
import java.util.List;

import cz.cvut.fit.vybirjan.mp.web.model.FeatureJDO;

public class FeaturesDTO {

	public static class TableItem {
		private long id;
		private String code;
		private String description;
		private String key;

		public long getId() {
			return id;
		}

		public String getCode() {
			return code;
		}

		public String getDescription() {
			return description;
		}

		public String getKey() {
			return key;
		}

		public void setId(long id) {
			this.id = id;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public void setKey(String key) {
			this.key = key;
		}

	}

	private String okMessage;
	private String errorMessage;
	private final List<TableItem> tableItems = new LinkedList<FeaturesDTO.TableItem>();

	public List<TableItem> getTableItems() {
		return tableItems;
	}

	public void addTableItem(FeatureJDO feature) {
		TableItem item = new TableItem();
		item.setCode(feature.getCode());
		item.setDescription(feature.getDescription());
		item.setId(feature.getId().getId());
		item.setKey(feature.getEncodedKey());

		tableItems.add(item);
	}

	public String getOkMessage() {
		return okMessage;
	}

	public void setOkMessage(String okMessage) {
		this.okMessage = okMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
