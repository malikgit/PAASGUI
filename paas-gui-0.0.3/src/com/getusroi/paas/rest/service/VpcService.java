package com.getusroi.paas.rest.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.paas.configuration.ConfigurationConstants;
import com.getusroi.paas.configuration.InvalidConfigException;
import com.getusroi.paas.configuration.PassGUIConfiguration;
import com.getusroi.paas.dao.DataBaseOperationFailedException;
import com.getusroi.paas.dao.VpcDAO;
import com.getusroi.paas.jaxb.AddVirtualPrivateCloud;
import com.getusroi.paas.jaxb.ListOfVPC;
import com.getusroi.paas.marathon.service.MarathonServiceException;
import com.getusroi.paas.rest.RestServiceHelper;
import com.getusroi.paas.rest.service.exception.PAASNetworkServiceException;
import com.getusroi.paas.vo.VPC;
import com.getusroi.paas.vpc.jaxb.AddVirtualPrivateCloudConfig;
import com.getusroi.paas.vpc.jaxb.AddVirtualPrivateCloudConfigs;
import com.getusroi.paas.vpc.jaxb.Tenant;
import com.getusroi.paas.vpc.jaxb.TenantConfiguration;
import com.getusroi.paas.vpc.jaxb.UnableToUnmarshalVPC;
import com.getusroi.paas.vpc.jaxb.VPCJAXBHelper;
import com.google.gson.Gson;

@Path("/vpcService")
public class VpcService {
	static final Logger logger = LoggerFactory.getLogger(VpcService.class);
	static final String TENANT = "tenant";

	VpcDAO vpcDAO = null;

	/**
	 * To insert vpc into the db
	 * 
	 * @param vpcData
	 * @param req
	 * @throws DataBaseOperationFailedException
	 * @throws PAASNetworkServiceException
	 */
	@POST
	@Path("/addVPC")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addVPC(String vpcData, @Context HttpServletRequest req)
			throws DataBaseOperationFailedException, PAASNetworkServiceException {
		logger.debug(".addVPC method of VpcService : VpcService " + vpcData);
		ObjectMapper mapper = new ObjectMapper();
		vpcDAO = new VpcDAO();
		RestServiceHelper restServiceHelper = new RestServiceHelper();
		try {
			VPC vpc = mapper.readValue(vpcData, VPC.class);
			if (vpc != null) {
				HttpSession session = req.getSession(true);
				vpc.setTenant_id(restServiceHelper.convertStringToInteger(session.getAttribute("id") + ""));
				// vpc.setVpcId(PAASGenericHelper.getCustomUUID(PAASConstant.VPC_PREFIX));
				vpcDAO.registerVPC(vpc);
			}
		} catch (IOException e) {
			logger.error("Error in reading data : " + vpcData + " using object mapper in addVPC", e);
			throw new PAASNetworkServiceException(
					"Error in reading data : " + vpcData + " using object mapper in addVPC");
		}

	}// end of method

	@POST
	@Path("/addXmlVpc")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addXmlVpc(String vpcData, @Context HttpServletRequest req)
			throws DataBaseOperationFailedException, PAASNetworkServiceException, InvalidConfigException, UnableToUnmarshalVPC {
		logger.debug(".addXmlVpc method of VpcService: " + vpcData);
		VPCJAXBHelper helper = new VPCJAXBHelper();
		TenantConfiguration tenantConfiguration = helper.getVPCConfiguration(vpcData, ConfigurationConstants.VPC_CONFIG_XSD);
		int tenantId = tenantConfiguration.getTenant().getId();
		vpcDAO = new VpcDAO();
		for(AddVirtualPrivateCloudConfig vpcConfig : tenantConfiguration.getTenant().getAddVirtualPrivateCloudConfigs().getAddVirtualPrivateCloudConfig()) {
			VPC vpc = new VPC();
			vpc.setTenant_id(tenantId);
			vpc.setVpc_name(vpcConfig.getVPCName());
			vpc.setCidr(vpcConfig.getCidr());
			vpc.setAclName(vpcConfig.getAcl());
			vpcDAO.registerVPC(vpc);
		}
		
	}//end of method

	/**
	 * To get All vpc using tenant id
	 * 
	 * @param req
	 * @return
	 * @throws DataBaseOperationFailedException
	 */
	@GET
	@Path("/getAllVPC")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllVPC(@Context HttpServletRequest req) throws DataBaseOperationFailedException {
		logger.debug(".getAllVPC method of VpcService");
		HttpSession session = req.getSession(true);
		vpcDAO = new VpcDAO();
		List<VPC> vpcList = vpcDAO.getAllVPC((int) session.getAttribute("id"));
		Gson gson = new Gson();
		String vpcInJsonString = gson.toJson(vpcList);
		logger.debug("vpcList SIZE " + vpcList.size() + "vpcList : " + vpcList);
		return vpcInJsonString;
	}// end of method getAllVPC
	
	@GET
	@Path("/exportXmlVpcData")
	@Produces(MediaType.TEXT_PLAIN)
	public String exportXmlVpcData(@Context HttpServletRequest req) throws DataBaseOperationFailedException, JAXBException {
		logger.debug(".exportXmlVpcData method of VpcService");
		HttpSession session = req.getSession(true);
		vpcDAO = new VpcDAO();
		List<VPC> vpcList = vpcDAO.getAllVPC((int) session.getAttribute("id"));
		int tennatId = vpcList.get(1).getTenant_id();
		List<AddVirtualPrivateCloudConfig> listAddVirtualPrivateCloudConfig = new ArrayList<>();
		for(VPC vpc : vpcList) {
			AddVirtualPrivateCloudConfig cloudConfig = new AddVirtualPrivateCloudConfig();
			cloudConfig.setVPCName(vpc.getVpc_name());
			cloudConfig.setAcl(vpc.getAclName());
			cloudConfig.setCidr(vpc.getCidr());
			listAddVirtualPrivateCloudConfig.add(cloudConfig);
			logger.debug("VPC: " + vpc);
		}
		TenantConfiguration tenantConfig = new TenantConfiguration();
		tenantConfig.setTenant(new Tenant());
		tenantConfig.getTenant().setAddVirtualPrivateCloudConfigs(new AddVirtualPrivateCloudConfigs());
		tenantConfig.getTenant().setId(tennatId);
		tenantConfig.getTenant().getAddVirtualPrivateCloudConfigs().setAddVirtualPrivateCloudConfig(new ArrayList<AddVirtualPrivateCloudConfig>());
		logger.debug("");
		for(AddVirtualPrivateCloudConfig vpc : listAddVirtualPrivateCloudConfig) {
			tenantConfig.getTenant().getAddVirtualPrivateCloudConfigs().getAddVirtualPrivateCloudConfig().add(vpc);
		}
		String xmlString = marshaling(tenantConfig);
		logger.debug("XML String: " + xmlString);
		return xmlString;
	}// end of method getAllVPC

	static String marshaling(TenantConfiguration tenantConfig) throws JAXBException
	{
	    JAXBContext jaxbContext = JAXBContext.newInstance(TenantConfiguration.class);
	    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    //Marshal the employees list in console
	    jaxbMarshaller.marshal(tenantConfig, System.out);
	    StringWriter xmlStringWriter = new StringWriter();
	    jaxbMarshaller.marshal(tenantConfig, xmlStringWriter);
	    String xmlString = xmlStringWriter.toString();
	    return xmlString;
	    //Marshal the employees list in file
	}
	
	/**
	 * To Delete Vpc by using Vpc name
	 * 
	 * @param vpcName
	 * @return
	 * @throws DataBaseOperationFailedException
	 */
	@GET
	@Path("/deleteVPCById/{vpcId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String deleteVPCByName(@PathParam("vpcId") String vpcId) throws DataBaseOperationFailedException {
		logger.debug(".deleteVPCByName method of VpcService : vpcId " + vpcId);
		vpcDAO = new VpcDAO();
		vpcDAO.deleteVPCById(new RestServiceHelper().convertStringToInteger(vpcId));
		return "vpc with name : " + vpcId + " is delete successfully";
	}// end of method deleteVPCByName

	@PUT
	@Path("/updateVPC")
	@Produces(MediaType.TEXT_PLAIN)
	public String updateAclById(String vpcObject, @Context HttpServletRequest req) throws MarathonServiceException {
		logger.debug(".updateVPC method of VpcService : vpcObject " + vpcObject);
		ObjectMapper mapper = new ObjectMapper();
		vpcDAO = new VpcDAO();
		try {
			VPC vpc = mapper.readValue(vpcObject, VPC.class);
			vpcDAO.updateVPCByVPCId(vpc);
			logger.debug("Updated Successfully.");
			return "Success";
		} catch (Exception e) {
			logger.error("Error updating acl ", e);
		}
		return "failed";
	}

	/**
	 * 
	 * @param vpcData
	 * @throws DataBaseOperationFailedException
	 * @throws PAASNetworkServiceException
	 */

	/*
	 * @PUT
	 * 
	 * @Path("/updateVPC")
	 * 
	 * @Produces(MediaType.TEXT_PLAIN)
	 * 
	 * public String updateVPC(String vpcData,@Context HttpServletRequest req)
	 * throws DataBaseOperationFailedException, PAASNetworkServiceException {
	 * 
	 * logger.debug(".updateVPC method of VpcService"); ObjectMapper mapper =
	 * new ObjectMapper(); vpcDAO = new VpcDAO(); try{ VPC vpc =
	 * mapper.readValue(vpcData, VPC.class); vpcDAO.updateVPCByVPCId(vpc);
	 * return "Success"; } catch (IOException e) { logger.error(
	 * "Error in reading data : " + vpcData +
	 * " using object mapper in updateVPC"); throw new
	 * PAASNetworkServiceException("Error in reading data : " + vpcData +
	 * " using object mapper in updateVPC"); }
	 * 
	 * }
	 */// end of method updateVPC

	/**
	 * To check vpc name exist or not in database
	 * 
	 * @param vpcName
	 * @param req
	 * @return
	 * @throws DataBaseOperationFailedException
	 */
	@GET
	@Path("/checkVPC/{vpcName}")
	@Produces(MediaType.TEXT_PLAIN)
	public String checkVPCNameExist(@PathParam("vpcName") String vpcName, @Context HttpServletRequest req)
			throws DataBaseOperationFailedException {
		logger.debug("Inside checkVPCNameExist (.)  of VpcServic : vpcName " + vpcName);
		HttpSession session = req.getSession(true);
		vpcDAO = new VpcDAO();
		logger.debug("ID: " + (int) session.getAttribute("id"));
		int id = vpcDAO.getVPCIdByVPCNames(vpcName, (int) session.getAttribute("id"));
		logger.debug("RETURN ID " + id);
		if (id > 0)
			return "success";
		else
			return "failure";
	}// end of method checkVPCByName validation

}
