package com.getusroi.paas.jaxb;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "appVPC" })
@XmlRootElement(name = "TenantConfiguration")
public class TenantConfiguration implements Serializable {
	private static final long serialVersionUID = 1L;
	@XmlAttribute(name = "id", required = true)
	protected String id;
	@XmlElement(name = "AddVirtualPrivateCloud", required = true)
	protected List<AddVirtualPrivateCloud> appVPC;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<AddVirtualPrivateCloud> getAppVPC() {
		return appVPC;
	}

	public void setAppVPC(List<AddVirtualPrivateCloud> appVPC) {
		this.appVPC = appVPC;
	}

}
