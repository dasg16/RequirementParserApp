package com.das.pojo;

public class RequirementDetails {

	private String testCaseID;

	private String given;

	private String when;

	private String then;

	private String coverageType;

	public String getTestCaseID() {
		return testCaseID;
	}

	public void setTestCaseID(String testCaseID) {
		this.testCaseID = testCaseID;
	}

	public String getGiven() {
		return given;
	}

	public void setGiven(String given) {
		this.given = given;
	}

	public String getWhen() {
		return when;
	}

	public void setWhen(String when) {
		this.when = when;
	}

	public String getThen() {
		return then;
	}

	public void setThen(String then) {
		this.then = then;
	}

	public String getCoverageType() {
		return coverageType;
	}

	public void setCoverageType(String coverageType) {
		this.coverageType = coverageType;
	}

	@Override
	public String toString() {
		return "RequirementDetails [testCaseID=" + testCaseID + ", given=" + given + ", when=" + when + ", then=" + then
				+ ", coverageType=" + coverageType + "]";
	}

}
