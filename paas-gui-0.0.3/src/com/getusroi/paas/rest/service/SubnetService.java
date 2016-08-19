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
import com.getusroi.paas.dao.SubnetDAO;
import com.getusroi.paas.helper.PAASConstant;
import com.getusroi.paas.helper.ScriptService;
import com.getusroi.paas.helper.UnableToLoadPropertyFileException;
import com.getusroi.paas.jaxb.AddSubnet;
import com.getusroi.paas.jaxb.SubnetTenantConfiguration;
import com.getusroi.paas.rest.RestServiceHelper;
import com.getusroi.paas.rest.service.exception.PAASNetworkServiceException;
import com.getusroi.paas.sdn.service.impl.SDNServiceImplException;
import com.getusroi.paas.subnet.jaxb.SubnetConfiguration;
import com.getusroi.paas.subnet.jaxb.SubnetConfigurations;
import com.getusroi.paas.subnet.jaxb.SubnetJAXBHelper;
import com.getusroi.paas.subnet.jaxb.Tenant;
import com.getusroi.paas.subnet.jaxb.TenantConfiguration;
import com.getusroi.paas.subnet.jaxb.UnableToUnmarshalSubnet;
import com.getusroi.paas.vo.Subnet;
import com.google.gson.Gson;

@Path("/subnetService")
public class SubnetService {
	private Logger logger = LoggerFactory.getLogger(SubnetService.class);
	SubnetDAO subnetDao = null;
	ObjectMapper mapper = null;
	Subnet subnet = null;
	RestServiceHelper restServHlper= null;
	
	/**
	 * Rest Service to add new Subnet
	 * @param subnetData
	 * @param request
	 * @throws DataBaseOperationFailedException
	 * @throws SDNServiceImplException
	 * @throws PAASNetworkServiceException
	 */
	@POST
	 @Path("/addSubnet")
	 @Consumes(MediaType.APPLICATION_JSON)
	 public void addSubnet(String subnetData, @Context HttpServletRequest request) throws DataBaseOperationFailedException, SDNServiceImplException, PAASNetworkServiceException{
		 	logger.debug(".addSubnet method of SubnetService"+subnetData);
			ObjectMapper mapper = new ObjectMapper();
			subnetDao= new SubnetDAO();
			ScriptService scriptService=new ScriptService();
			try {
				subnet = mapper.readValue(subnetData, Subnet.class);
				if (subnet != null) {
					HttpSession session = request.getSession(true);
					subnet.setTenantId((int)session.getAttribute("id"));
					int id=	subnetDao.addSubnet(subnet);
					//This is not used in currently
					//scriptService.createSubnetNetwork(subnet.getCidr(),PAASConstant.TENANT+subnet.getTenantId()+"-"+PAASConstant.SUBNET_PREFIX+id);
					
					//UnComment it
					scriptService.createSubnetNetwork(subnet.getCidr(),subnet.getSubnetName());
				} 
			} catch (IOException  e) {
				 logger.error(""+e);
				 
				throw new PAASNetworkServiceException("Error in reading data : "
						+ subnetData + " using object mapper in addSubnet");
			}catch ( InterruptedException e) {
				logger.error("Error In Excuting subnetCrearion Script ",e);
			} catch (UnableToLoadPropertyFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//end of method addSubnet
	
	/**
	 * this method is used to get subnet in xml format and store it into database
	 * @param vpcData
	 * @param req
	 * @throws DataBaseOperationFailedException
	 * @throws PAASNetworkServiceException
	 * @throws UnableToUnmarshalSubnet 
	 * @throws InvalidConfigException
	 */
	@POST
	@Path("/addXmlSubnet")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addXmlSubnet(String subnetData, @Context HttpServletRequest req)
			throws DataBaseOperationFailedException, PAASNetworkServiceException, UnableToUnmarshalSubnet {
		logger.debug("subnet Xml Data: " + subnetData);
		SubnetJAXBHelper helper = new SubnetJAXBHelper();
		try {
			TenantConfiguration tenantConfiguration = helper.getSubnetConfiguration(subnetData, ConfigurationConstants.SUBNET_CONFIG_XSD);
			int tenantId = tenantConfiguration.getTenant().getId();
			List<SubnetConfiguration> subnetConfigList =  tenantConfiguration.getTenant().getSubnetConfigurations().getSubnetConfiguration();
			subnetDao = new SubnetDAO();
			for(SubnetConfiguration subnetConfig : subnetConfigList) {
				Subnet subnet = new Subnet();
				subnet.setTenantId(tenantId);
				subnet.setSubnetName(subnetConfig.getSubnetName());
				subnet.setVpcName(subnetConfig.getVpc());
				subnet.setAclName(subnetConfig.getAcl());
				subnet.setCidr(subnetConfig.getCidr());
				subnet.setEnvironmentName(subnetConfig.getEnvironment());
				subnetDao.addSubnet(subnet);
			}
		} catch(UnableToUnmarshalSubnet e) {
			throw new UnableToUnmarshalSubnet("Unable to Insert XML Data");
		}
		
	}//end of method
	
	/**
	 * Rest service to Get Subnet name using Vpc
	 * @param subnetName
	 * @param request
	 * @return
	 * @throws DataBaseOperationFailedException
	 * @throws PAASNetworkServiceException
	 */
	@POST
	@Path("/getSubnetNameByVpc")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String getAllSubnetByVpcName(String subnetName, @Context HttpServletRequest  request) throws DataBaseOperationFailedException, PAASNetworkServiceException {
		logger.debug(".getAllSubnet method of PAASNetworkService "+subnetName);
		subnetDao = new SubnetDAO();
		String subnetJsonString = null;
		try {
			HttpSession session = request.getSession(true);
			List<Subnet> subnetList = subnetDao.getAllSubnetByVpcNameAndTenantId(subnetName, (int)session.getAttribute("id"));
			logger.debug("comming"+subnetList);
			Gson gson = new Gson();
			subnetJsonString = gson.toJson(subnetList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return subnetJsonString;
	}
	
	/**
	 * Rest Service to get all Subnet
	 * @param request
	 * @return
	 * @throws DataBaseOperationFailedException
	 */
	 @GET
	 @Path("/getAllSubnet")
	 @Produces(MediaType.APPLICATION_JSON)
	 public String getAllSubnet(@Context HttpServletRequest request) throws DataBaseOperationFailedException{
			logger.debug(".getAllSubnet method of PAASNetworkService");
			subnetDao = new SubnetDAO();
			String subnetJsonString = null;
			try {
				HttpSession session = request.getSession(true);
				List<Subnet> subnetList = subnetDao.getAllSubnetByTenantId((int)session.getAttribute("id"));
				logger.debug("comming"+subnetList);
				Gson gson = new Gson();
				subnetJsonString = gson.toJson(subnetList);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return subnetJsonString;
	 }//end of method getAllSubnet
	 
	 @GET
	 @Path("/exportAllSubnet")
	 @Produces(MediaType.TEXT_PLAIN)
	 public String exportAllSubnet(@Context HttpServletRequest request) throws DataBaseOperationFailedException{
			logger.debug(".getAllSubnet method of PAASNetworkService");
			subnetDao = new SubnetDAO();
			try {
				HttpSession session = request.getSession(true);
				List<Subnet> subnetList = subnetDao.getAllSubnetByTenantId((int)session.getAttribute("id"));
				List<SubnetConfiguration> listSubnetConfiguration = new ArrayList<>();
				int tenantId = subnetList.get(0).getTenantId();
				for(Subnet subnet : subnetList) {
					SubnetConfiguration subnetConfiguration = new SubnetConfiguration();
					subnetConfiguration.setSubnetName(subnet.getSubnetName());
					subnetConfiguration.setVpc(subnet.getVpcName());
					subnetConfiguration.setCidr(subnet.getCidr());
					subnetConfiguration.setEnvironment(subnet.getEnvironmentName());
					subnetConfiguration.setAcl(subnet.getAclName());
					listSubnetConfiguration.add(subnetConfiguration);
				}
				TenantConfiguration tenantConfiguration = new TenantConfiguration();
				tenantConfiguration.setTenant(new Tenant());
				tenantConfiguration.getTenant().setId(tenantId);
				tenantConfiguration.getTenant().setSubnetConfigurations(new SubnetConfigurations());
				tenantConfiguration.getTenant().getSubnetConfigurations().setSubnetConfiguration(new ArrayList<SubnetConfiguration>());
				for(SubnetConfiguration subnetConfiguration : listSubnetConfiguration) {
					tenantConfiguration.getTenant().getSubnetConfigurations().getSubnetConfiguration().add(subnetConfiguration);
				}
				String xmlSubnetString =  marshaling(tenantConfiguration);
				logger.debug("");
				return xmlSubnetString;
			} catch (Exception e) {
				throw new DataBaseOperationFailedException();
			}
			
	 }//end of method getAllSubnet
	 
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
	  * Rest Service to delete Subnet by subnet id
	  * @param id
	  * @return
	  * @throws DataBaseOperationFailedException
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @throws UnableToLoadPropertyFileException 
	  */
	 @GET
	 @Path("/deleteSubnetById/{id}")
	 @Produces(MediaType.TEXT_PLAIN)
	 public String deleteSubnetById(@PathParam("id") String id, @Context HttpServletRequest request)
			throws DataBaseOperationFailedException, InterruptedException, IOException,
			UnableToLoadPropertyFileException {
		logger.debug(".deleteSubnetById method of PAASNetworkService " + id);
		restServHlper = new RestServiceHelper();
		subnetDao = new SubnetDAO();
		HttpSession session = request.getSession(true);
		new ScriptService().deleteSubnetNetwork(
				PAASConstant.TENANT + (int) session.getAttribute("id") + "-" + PAASConstant.SUBNET_PREFIX + id);
		subnetDao.deleteSubnetById(restServHlper.convertStringToInteger(id));
		logger.debug("Subnet  : " + id + " delete successubnetNamesfully");
		return "subnet with name : " + id + " is delete successfully";
	 }// end of method deleteSubnetByName

	 /**
	  * Rest Service to update Subnet by subnet id
	  * @param subnetData
	  * @return
	  * @throws DataBaseOperationFailedException
	  * @throws PAASNetworkServiceException
	  */
	 @PUT
	 @Path("/updateSubnet")
	 @Produces(MediaType.TEXT_PLAIN)
	public String updateSubnet(String subnetData) throws DataBaseOperationFailedException, PAASNetworkServiceException{
		logger.debug(".updateSubnet method of SubnetService subnetData "+subnetData);
		ObjectMapper mapper = new ObjectMapper();
		subnetDao = new SubnetDAO();
		try {
			Subnet subnet=mapper.readValue(subnetData,Subnet.class);
			subnetDao.updateSubnetBySubnetID(subnet);
			return "Success";
		} catch (IOException e) {
			logger.error("Error in reading data : "+subnetData+" using object mapper in updateSubnet");
			throw new PAASNetworkServiceException("Error in reading data : "+subnetData+" using object mapper in updateSubnet");
		}
	}//end of method updateSubnet
	 
	 
	 /**
		 * To check subnet name exist or not
		 * @param subName
		 * @param req
		 * @return
		 * @throws DataBaseOperationFailedException
		 */
		@GET
		@Path("/checkSubnet/{subName}")
		@Produces(MediaType.TEXT_PLAIN)
		public String subnetValidation(@PathParam("subName") String subName,
				@Context HttpServletRequest req)
				throws DataBaseOperationFailedException {
			logger.debug("coming to check acl of pass network");
			HttpSession session = req.getSession(true);
			SubnetDAO networkDAO = new SubnetDAO();
			int id = networkDAO.getSubnetIdBySubnetName(subName,
					(int) session.getAttribute("id"));
			if (id > 0)
				return PAASConstant.SUCCESS;
			else
				return PAASConstant.FAILURE;
		}// end of method aClByName
		
		
		 /**
		 * To check cidr value by subnet name
		 * @param subName
		 * @param req
		 * @return
		 * @throws DataBaseOperationFailedException
		 */
		/*@GET
		@Path("/getCidr/{subName}")
		@Produces(MediaType.TEXT_PLAIN)
		public String get(@PathParam("subName") String subName,
				@Context HttpServletRequest req)
				throws DataBaseOperationFailedException {
			logger.debug(" coming to check acl of pass network");
			HttpSession session = req.getSession(true);
			SubnetDAO networkDAO = new SubnetDAO();
			String cidr = networkDAO.getCidrBySubnetName((int) session.getAttribute("id"), subName);
					
			return cidr;
		}// end of method aClByName
*/}

