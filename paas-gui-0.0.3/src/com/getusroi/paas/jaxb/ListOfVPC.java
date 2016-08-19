package com.getusroi.paas.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.getusroi.paas.vo.VPC;

@XmlRootElement(name = "listOfVPC")
@XmlAccessorType (XmlAccessType.FIELD)
public class ListOfVPC {
	@XmlElement(name = "vpc", type = VPC.class)
	private List<VPC> listOfVPC = null;

	public List<VPC> getListOfVPC() {
		return listOfVPC;
	}

	public void setListOfVPC(List<VPC> listOfVPC) {
		this.listOfVPC = listOfVPC;
	}
	
}
