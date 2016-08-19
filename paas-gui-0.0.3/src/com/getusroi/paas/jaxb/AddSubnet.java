package com.getusroi.paas.jaxb;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class AddSubnet implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "SubnetName")
	protected String subnetName;
	@XmlElement(name = "Vpc")
	protected String vpc;
	@XmlElement(name = "Cidr")
	protected String cidr;
	@XmlElement(name = "Environment")
	protected String environment;
	@XmlElement(name = "Acl")
	protected String acl;

	public String getSubnetName() {
		return subnetName;
	}

	public void setSubnetName(String subnetName) {
		this.subnetName = subnetName;
	}

	public String getVpc() {
		return vpc;
	}

	public void setVpc(String vpc) {
		this.vpc = vpc;
	}

	public String getCidr() {
		return cidr;
	}

	public void setCidr(String cidr) {
		this.cidr = cidr;
	}

	public String getAcl() {
		return acl;
	}

	public void setAcl(String acl) {
		this.acl = acl;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

}
