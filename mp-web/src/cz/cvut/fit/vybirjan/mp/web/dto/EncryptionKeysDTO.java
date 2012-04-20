package cz.cvut.fit.vybirjan.mp.web.dto;

import java.util.LinkedList;
import java.util.List;

import cz.cvut.fit.vybirjan.mp.web.model.EncryptionKeyJDO;

public class EncryptionKeysDTO {

	public static class TableItem {
		private long id;
		private String appId;
		private String publicKey;
		private String privateKey;

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public String getPublicKey() {
			return publicKey;
		}

		public void setPublicKey(String publicKey) {
			this.publicKey = publicKey;
		}

		public String getPrivateKey() {
			return privateKey;
		}

		public void setPrivateKey(String privateKey) {
			this.privateKey = privateKey;
		}

		public void setId(long id) {
			this.id = id;
		}

		public long getId() {
			return id;
		}
	}

	private final List<TableItem> tableItems = new LinkedList<EncryptionKeysDTO.TableItem>();
	private String error;
	private String success;

	public List<TableItem> getTableItems() {
		return tableItems;
	}

	public void addTableItem(EncryptionKeyJDO key) {
		TableItem ti = new TableItem();
		ti.setAppId(key.getAppId());
		ti.setPrivateKey(key.getPrivateKey());
		ti.setPublicKey(key.getPublicKey());
		ti.setId(key.getId().getId());

		tableItems.add(ti);
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

}
