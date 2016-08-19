package com.getusroi.paas.jaxb;

public class ObjectFactory {
	
	public ObjectFactory() {
		// TODO Auto-generated constructor stub
	}
	
	public TenantConfiguration createTenantConfig(){
		return new TenantConfiguration();
	}
	public AddVirtualPrivateCloud createVPC(){
		return new AddVirtualPrivateCloud();
	}

}
