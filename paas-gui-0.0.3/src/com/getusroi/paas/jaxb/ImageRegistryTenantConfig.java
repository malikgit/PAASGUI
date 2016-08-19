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
@XmlType(name = "", propOrder = { "imageReg" })
@XmlRootElement(name = "ImageTenantConfig")
public class ImageRegistryTenantConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "id", required = true)
	protected String id;
	@XmlElement(name = "ImageRegistry", required = true)
	protected List<AddImageRegistry> imageReg;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<AddImageRegistry> getImageReg() {
		return imageReg;
	}

	public void setImageReg(List<AddImageRegistry> imageReg) {
		this.imageReg = imageReg;
	}

}
