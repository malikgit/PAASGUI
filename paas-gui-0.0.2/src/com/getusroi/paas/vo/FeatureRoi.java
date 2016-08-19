package com.getusroi.paas.vo;

public class FeatureRoi {
	
	private String tenantid;
	public FeatureRoi(String tenantid, String emailId, String password) {
		super();
		this.tenantid = tenantid;
		this.emailId = emailId;
		this.password = password;
	}
	
	public FeatureRoi() {
		super();
		
	}
	@Override
	public String toString() {
		return "FeatureRoi [tenantid=" + tenantid + ", emailId=" + emailId + ", password=" + password + "]";
	}
	public String getTenantid() {
		return tenantid;
	}
	public void setTenantid(String tenantid) {
		this.tenantid = tenantid;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	private String emailId;
	private String password;

	
}
