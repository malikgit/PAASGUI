package com.getusroi.paas.jaxb;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class AddImageRegistry implements Serializable {
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "Name", required = true)
	protected String name;
	@XmlElement(name = "Location", required = true)
	protected String location;
	@XmlElement(name = "Version", required = true)
	protected String version;
	@XmlElement(name = "UserName", required = true)
	protected String userName;
	@XmlElement(name = "Password", required = true)
	protected String password;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
