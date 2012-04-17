package cz.cvut.fit.vybirjan.mp.web.dto;

public class OverviewDTO {

	private int numberOfLicenses;
	private int numberOfActivations;
	private String lastActivationDate;

	public int getNumberOfLicenses() {
		return numberOfLicenses;
	}

	public void setNumberOfLicenses(int numberOfLicenses) {
		this.numberOfLicenses = numberOfLicenses;
	}

	public int getNumberOfActivations() {
		return numberOfActivations;
	}

	public void setNumberOfActivations(int numberOfActivations) {
		this.numberOfActivations = numberOfActivations;
	}

	public String getLastActivationDate() {
		return lastActivationDate;
	}

	public void setLastActivationDate(String lastActivationDate) {
		this.lastActivationDate = lastActivationDate;
	}
}
