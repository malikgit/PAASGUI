package com.getusroi.paas.jaxb;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class AddVirtualPrivateCloud implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "VPCName")
	protected String vpcName;
	@XmlElement(name = "Cidr")
	protected String cidr;
	@XmlElement(name = "Acl")
	protected String acl;

	public String getVpcName() {
		return vpcName;
	}

	public void setVpcName(String vpcName) {
		this.vpcName = vpcName;
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

}
