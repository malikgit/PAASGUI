package com.getusroi.paas.rest.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.paas.application.jaxb.ApplicationConfig;
import com.getusroi.paas.application.jaxb.RunSettings;
import com.getusroi.paas.application.jaxb.ServiceConfig;
import com.getusroi.paas.application.jaxb.TenantConfiguration;
import com.getusroi.paas.application.jaxb.UnableToUnMarshalXML;
import com.getusroi.paas.application.jaxb.XMLValidation;
import com.getusroi.paas.configuration.ConfigurationConstants;
import com.getusroi.paas.dao.ApplicationDAO;
import com.getusroi.paas.dao.ContainerTypesDAO;
import com.getusroi.paas.dao.DataBaseOperationFailedException;
import com.getusroi.paas.dao.SubnetDAO;
import com.getusroi.paas.dao.VpcDAO;
import com.getusroi.paas.helper.PAASConstant;
import com.getusroi.paas.helper.PAASGenericHelper;
import com.getusroi.paas.helper.ScriptService;
import com.getusroi.paas.helper.UnableToLoadPropertyFileException;
import com.getusroi.paas.marathon.service.IMarathonService;
import com.getusroi.paas.marathon.service.MarathonServiceException;
import com.getusroi.paas.marathon.service.impl.MarathonService;
import com.getusroi.paas.rest.service.exception.ApplicationServiceException;
import com.getusroi.paas.vo.Applications;
import com.getusroi.paas.vo.ContainerTypes;
import com.getusroi.paas.vo.MessosTaskInfo;
import com.getusroi.paas.vo.Service;
import com.getusroi.paas.vo.Storage;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@Path("/applicationService")
public class ApplicationService {
	 static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);
	 static final String TENANT="tenant";
	 static final String SUCCESS="sucess";
	 static final String FAILURE="failed";
	 static final String ERROR_CODE_PROP_FILE = "paasErrorCode.properties";
	 private ApplicationDAO applicationDAO=null;
	
	@POST
	@Path("/addService")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addService(String applicationServiceData,@Context HttpServletRequest request) throws DataBaseOperationFailedException, MarathonServiceException, InterruptedException, ApplicationServiceException{
		logger.debug(".addService method of ApplicationService ");
		ObjectMapper mapper = new ObjectMapper();
		applicationDAO = new ApplicationDAO();
		IMarathonService marathonService=new MarathonService();
		ContainerTypesDAO contianerDAO = new ContainerTypesDAO();
		//defulat value 
		int containerValue=500;
		try {
			HttpSession session = request.getSession(false);
			int userId = (int)session.getAttribute("id");
			Service service = mapper.readValue(applicationServiceData,Service.class);
			logger.debug("application id "+service.getAppsId());
			logger.debug("service "+service);
			
			service.setTenantId(userId);
			 
			List<ContainerTypes> containerTypesList = contianerDAO.getContainerTypeIdByName(service.getType(),userId);
			if(!containerTypesList.isEmpty())
			containerValue=containerTypesList.get(0).getMemory();
			
			//create instance in marathon using service object
			String instanceId = marathonService.postRequestToMarathon(service,containerValue);
		
			service.setInstanceId(instanceId);
			applicationDAO.addService(service);	
			logger.debug("----------Before  ContianerScript  script  call------------------------");			
			Thread.sleep(60000);
			List<MessosTaskInfo>  listOfMessosTask=	 ScriptService.runSCriptGetMessosTaskId(instanceId);
			logger.debug("listOfMessosTask "+listOfMessosTask);
			for(MessosTaskInfo mess:listOfMessosTask){
				logger.debug("mess======= "+mess);
			}
			if(listOfMessosTask.isEmpty()){
			Thread.sleep(60000);
			listOfMessosTask = ScriptService.runSCriptGetMessosTaskId(instanceId);
			
			}
			for (Iterator<?> iterator = listOfMessosTask.iterator(); 
				iterator .hasNext();) {
				MessosTaskInfo messosTaskInfo = (MessosTaskInfo) iterator.next();
				new ScriptService().updateSubnetNetworkInMessos(messosTaskInfo, service.getSubnetName());
			}
			logger.debug("----------Network  script  called------------------------");
		} catch (IOException e) {
			logger.error("Error in reading data "+applicationServiceData+" using object mapper in addService" + e);
			//throw new ApplicationServiceException("Error in reading data "+applicationServiceData+" using object mapper in addService");
		}catch (UnableToLoadPropertyFileException e) {
		
			logger.error("Error in loading script  configuration file ",e);
		}
	}//end of method addService
	
	/**
	 * Rest Service to Get all Services using Apps_id Foreign key
	 * @param request
	 * @return
	 * @throws DataBaseOperationFailedException
	 */
	@GET
	@Path("/getAllServiceByAppsId")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllApplicationService(@Context HttpServletRequest request) throws DataBaseOperationFailedException{
		logger.debug(".getAllApplicationService method of ApplicationService ");
		
		HttpSession session=request.getSession(false);
		int userId=(int)session.getAttribute("id");
	
		applicationDAO=new ApplicationDAO();
		List<Service> addServiceList = applicationDAO.getAllServiceByUserId(userId);
		 
		Gson gson = new Gson();
		String addServiceInJsonString=gson.toJson(addServiceList);
		return addServiceInJsonString;
		
	}//end of method getAllApplicationService
	
	@GET
	@Path("/getAllServiceByAppsId/{apps_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllServices(@PathParam("apps_id") int apps_id,@Context HttpServletRequest request) throws DataBaseOperationFailedException{
		logger.debug(".getAllServices method of ApplicationService ");
		HttpSession session=request.getSession(false);
		applicationDAO = new ApplicationDAO();
		List<Service> applicantSummaryList=applicationDAO.getAllServiceByAppsIdAndTenantID(apps_id, (int)session.getAttribute("id"));
		Gson gson = new Gson();
		String applicantSummaryInJsonString=gson.toJson(applicantSummaryList);
		return applicantSummaryInJsonString;
	}//end of method getAllServices

	/*@GET
	@Path("/deleteServiceByName/{serviceName}/{appsid}")
	public void deleteServiceByName(@PathParam("serviceName") String serviceName,@PathParam("appsid") String appsid,@Context HttpServletRequest request) throws DataBaseOperationFailedException, MarathonServiceException{
		logger.debug(".deleteServiceByName method of ApplicationService ");
		logger.debug("ServiceNAme : "+serviceName  +" apps_id : "+appsid);
		applicationDAO=new ApplicationDAO();
		HttpSession session=request.getSession(false);
		//String appid=TENANT+user_id+"-"+envirnoment;
		//new MarathonService().deletInstanceformMarathan(appid);
		Service service = new Service();
		service.setServiceName(serviceName);
		service.setTenantId((int)session.getAttribute("id"));
		service.setAppsId(new RestServiceHelper().convertStringToInteger(appsid));
		applicationDAO.deleteServiceByServiceName(service);
	}//end of method deleteServiceByName
*/	
	@GET
	@Path("/deleteServiceById/{serviceId}/{instanceId}")
	public void deleteServiceById(@PathParam("serviceId") String serviceId,@PathParam("instanceId") String instanceId, @Context HttpServletRequest request) throws DataBaseOperationFailedException, MarathonServiceException,InterruptedException, ApplicationServiceException{
		logger.debug(".deleteServiceByName method of ApplicationService ");
		logger.debug("serviceId : "+serviceId +" instanceId "+instanceId );
		  
		String containerId= null;
		try {
			List<MessosTaskInfo>  listOfMessosTask=	 ScriptService.runSCriptGetMessosTaskId(instanceId);
			logger.debug("listOfMessosTask checking  >>>>>>>>>>>>>>>>>>>>>>>>>>>> "+listOfMessosTask+" Size "+listOfMessosTask.size());
			for(MessosTaskInfo mess:listOfMessosTask){
				logger.debug("containerId "+mess.getContainerId());
				String mesTaskId = mess.getMesso_Task_Id();
				containerId=mess.getContainerId();
				/*logger.debug("mesTaskId "+mesTaskId);
				String tempInstance = mesTaskId.substring(mesTaskId.charAt('_'), mesTaskId.charAt('.'));
				logger.debug("tempInstance "+tempInstance);
				if(instanceId.equals(tempInstance)){
					containerId=mess.getContainerId();
				}*/
			}
			if(listOfMessosTask.isEmpty()){
			Thread.sleep(60000);
			listOfMessosTask = ScriptService.runSCriptGetMessosTaskId(instanceId);
			
			}
			for (Iterator iterator = listOfMessosTask.iterator(); 
				iterator .hasNext();) {
				MessosTaskInfo messosTaskInfo = (MessosTaskInfo) iterator.next();
			}
		applicationDAO = new ApplicationDAO();
		HttpSession session=request.getSession(false);
		int userId = (int)session.getAttribute("id");
		String appid = "/"+userId+"/"+instanceId;
		new MarathonService().deletInstanceformMarathan(appid);
		Service service = new Service();
		service.setId(Integer.parseInt(serviceId));
		service.setTenantId(userId);
		service.setInstanceId(instanceId);
		applicationDAO.deleteServiceByServiceId(service);
		new ApplicationService().deregisterService(containerId);
		}catch (IOException | UnableToLoadPropertyFileException e) {
		
		logger.error("Error when deleting service ",e);
	}catch(Exception ex){
		logger.error("Error when deleting service ",ex);
	}
	
}	//end of method deleteServiceByName
	/*
	@PUT
	@Path("/updateMarathonInstace")
	@Produces(MediaType.APPLICATION_JSON)
	public String updateMarathonInstace(String data) throws MarathonServiceException{
		logger.debug(".updateMarathonInstace method of ApplicationService ");
		IMarathonService marathonService=new MarathonService();
		marathonService.updateMarathonInsance(data);
		return data;
	}*///end of method updateMarathonInstace
	
	/*All Below methods for the shake of Fectch.java i.e methods ass*/
	
	@GET
	@Path("/getApplications")
	@Produces(MediaType.APPLICATION_JSON)
	
	public String getApplications(@Context HttpServletRequest request) {

		logger.info("Inside getAllApplications (.) of ApplicationService ");
		List<Applications> appList = new ArrayList<Applications>();
		String appsLista =null;
		try {
			applicationDAO = new ApplicationDAO();
			HttpSession session=request.getSession(false);
			appList = applicationDAO.getApplicationsDetailsBytTenantId((int)session.getAttribute("id"));
		Gson gson = new Gson();
		appsLista = gson.toJson(appList);
		//logger.info("selectApplicantName : " + customersList);
		}catch(Exception e){
			logger.error("Error when getting all data from Applications table");
		}
	return appsLista;
	}
	
	@GET
	@Path("/deleteApplication/{apps_id}")
	public void deleteApplicationById(@PathParam("apps_id") int apps_Id,@Context HttpServletRequest request) {
		logger.info("Inside (.) deleteApplicationById of ApplicationService");
		
		applicationDAO = new ApplicationDAO();
		try{
			HttpSession session=request.getSession(false);

			applicationDAO.deleteApplicationById(apps_Id, (int)session.getAttribute("id"));
		}catch(Exception e){
			logger.error("Error when deleting application ",e);
		}
	}
	
	/**
	 * 
	 * @param application
	 * @param req
	 * @return
	 * @throws JSONException
	 */
	@POST
	@Path("/storeApplication")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String storeApplicationsDetails(String application, @Context HttpServletRequest req) throws JSONException {
		logger.info("Inside  (.)storeApplicationsDetails ApplicationService "+application);
		ObjectMapper mapper = new ObjectMapper();
		applicationDAO = new ApplicationDAO();
	
		try{
			HttpSession session=req.getSession(false);
			Applications apps=mapper.readValue(application, Applications.class);
				apps.setTenant_id( (int)session.getAttribute("id"));
				applicationDAO.addApplication(apps);
				return SUCCESS;
		}catch(Exception e){
			logger.error("Error when deleting application ",e);
		}
		
		return FAILURE;
	}
	
	
	/**
	 * Rest Service to create Application by name only.
	 * @param msg
	 * @param req
	 * @return
	 * @throws JSONException
	 */
	@POST
	@Path("/createApplicationByName")
	@Produces(MediaType.TEXT_PLAIN)
	public String createApplicationByName(String msg, @Context HttpServletRequest req) throws JSONException {
		logger.info("Inside storeApplicantUser (.) ApplicationService"+msg);
		applicationDAO = new ApplicationDAO();
		String apps_id = "";
		try {
			Applications app = new Applications();
			app.setApplicantionName(msg);
			HttpSession session = req.getSession(true);
			app.setTenant_id((int)session.getAttribute("id"));
			apps_id=applicationDAO.addApplication(app);
		} catch (Exception e) {
			 logger.error("Error when creating Application by using name only ",e);
		}
		logger.info("-----Json is -------"+msg);
		return apps_id;
	}
	
	@GET
	@Path("/checkApplication/{applictionName}")
	@Produces(MediaType.TEXT_PLAIN)
	public String checkApplicationExistOrNotByName(@PathParam("applictionName") String applicationName,@Context HttpServletRequest req) throws JSONException {
		logger.info("Inside  (.)checkApplicationExistOrNotByName  ApplicationService");		 
		applicationDAO=new ApplicationDAO();
		int  id=0;
		try{
			HttpSession session=req.getSession(false);
		
				id=applicationDAO.getApplicationsIdByName(applicationName, (int)session.getAttribute("id"));
		 if(id==0)
			 return FAILURE;
			 
		 return SUCCESS;
			 
		}catch(Exception e){
			logger.error("Error when deleting application ",e);
		}
		
		return FAILURE;
	}
	
	/**
	 * Rest Service to  update Application by application id
	 * @param application
	 * @param req
	 * @return
	 * @throws MarathonServiceException
	 */
	@PUT
	@Path("/updateApplication")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String  updateApplication(String application,@Context HttpServletRequest req,@Context HttpServletResponse response) throws MarathonServiceException{
		logger.debug(".updateApplication method of ApplicationService "+application);
		ObjectMapper mapper = new ObjectMapper();
		applicationDAO=new ApplicationDAO();
	
		
		try{
			HttpSession session=req.getSession(false);
			Applications apps=mapper.readValue(application, Applications.class);
				apps.setTenant_id( (int)session.getAttribute("id"));
				applicationDAO.updateApplication(apps);

				
				return SUCCESS;
		}catch(Exception e){
			logger.error("Error updating pplication ",e);
		}
		
	return FAILURE;
	}
	
	
	
	/**
	 * Rest Service to check application name exist or not
	 * @param applicationName
	 * @param request
	 * @return
	 * @throws DataBaseOperationFailedException
	 */
	@GET
	@Path("/checkingApplication/{applicationName}")
	@Produces(MediaType.TEXT_PLAIN)
	public String checkApplicationExistByNameAndTenantId(
			@PathParam("applicationName") String applicationName,
			@Context HttpServletRequest request)
			throws DataBaseOperationFailedException {
		logger.debug(".getAllApplicationService method of ApplicationService ");
		int availability;
		HttpSession session = request.getSession(false);
		int userId = (int) session.getAttribute("id");

		applicationDAO = new ApplicationDAO();
		try {
			availability = applicationDAO
					.checkApplicationExistByNameAndTenantId(applicationName,
							userId);
			if (availability > 0)
				return "success";

		} catch (Exception e) {
			logger.error("Erro in checking application by name and tenant Id ",
					e);
		}
		return "failure";
	}
	
	@PUT
	@Path("/updateService")
	@Produces(MediaType.TEXT_PLAIN)

	public String updateService(String serviceJsoObj,@Context HttpServletRequest req) throws MarathonServiceException{
		logger.debug(".updateService method of ApplicationService "+serviceJsoObj);
		ObjectMapper mapper = new ObjectMapper();
		applicationDAO = new ApplicationDAO();
		try{
			HttpSession session=req.getSession(false);
			Service service = mapper.readValue(serviceJsoObj, Service.class);
			logger.debug("service IS NULL " +service);
			if(service != null)
			service.setTenantId((int)session.getAttribute("id"));
			
			applicationDAO.updateServiceByServiceId(service);
			IMarathonService marathonService=new MarathonService();
			marathonService.updateMarathonInsance(service);
			return SUCCESS;
		}catch(Exception e){
			logger.error("Error updating pplication ",e);
		}
		return FAILURE;
	}	
	
	/**
	 * To get All application and number of service number
	 * @param request
	 * @return
	 * @throws DataBaseOperationFailedException
	 */
	@GET
	@Path("/getAllApplAndNumOfServcCount")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllApplicationAndNoOfServiceCount(@Context HttpServletRequest request) throws DataBaseOperationFailedException{
		logger.debug(".getAllApplicationService method of ApplicationService ");
		applicationDAO = new ApplicationDAO();
		logger.debug(".getListOfAppsAndAssociatedService method of ApplicationService");
		List<Applications> listOfApplication = null;
		JSONArray jsonArray=new JSONArray(); 
		JSONArray jsonaPP=new JSONArray();
		jsonaPP.put("Application");
		jsonaPP.put("Total Counts");
		jsonArray.put(jsonaPP);
		try{
		HttpSession session=request.getSession(false);
		int userId=(int)session.getAttribute("id");
		listOfApplication = applicationDAO.getApplicationsDetailsBytTenantId(userId);
		List list = null;
		for(int i=0; i< listOfApplication.size(); i++){
			list = new ArrayList(); 
			String applName=listOfApplication.get(i).getApplicantionName();
			int serviceCount = applicationDAO.getNumberOfServiceByAppsId(listOfApplication.get(i).getApps_id());
			list.add(applName);
			list.add(serviceCount);
			 
			jsonArray.put(list);
		}
		}catch(Exception e){
		logger.debug("Error when getting data of all application and their respective number count service ");	
		}
		return jsonArray+"";
		
	}//end of method getAllApplicationService
	
	/**
	 * To de-register the Service when delete service from marathon 
	 * @param containerid
	 */
	public void deregisterService(String containerid) {
		  
		 try {

		  Client client = Client.create();
		  /*WebResource webResource = client.resource("http://172.16.2.7:8500/v1/catalog/deregister");*/
		  WebResource webResource = client.resource("http://192.168.1.104:8500/v1/catalog/deregister");
		  System.out.println(webResource);

		  String input = "{"+
		  "  \"Datacenter\": \"dc1\","+
		  "  \"Node\": \"PaasDemo\","+
		  "  \"ServiceID\": \""+containerid+"\""+
		  "}";
		                  System.out.println(input);
		  ClientResponse response = webResource.type("application/json")
		    .post(ClientResponse.class, input);
		  
		  if (response.getStatus() != 200) 
		  {
		   throw new RuntimeException("Failed : HTTP error code : "
		     + response.getStatus());
		  }   
		  String output = response.getEntity(String.class);
		  }           
		        catch (Exception e) 
		     {
		    e.printStackTrace();
		     }
		        finally
		        { 
		            System.out.println("rest of the code...");  
		        }
	
	}
	
	@GET
	@Path("/getAllServiceNameApplication")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllApplication(@Context HttpServletRequest request) {

		logger.info("Inside getAllApplications (.) of ApplicationService ");
		List<Storage> appList = new ArrayList<Storage>();
		String appsLista = null;
		try {
			HttpSession session=request.getSession(false);
			int userId=(int)session.getAttribute("id");
			applicationDAO = new ApplicationDAO();
			appList = applicationDAO.getAllServiceName(userId);
			Gson gson = new Gson();
			appsLista = gson.toJson(appList);
		} catch (Exception e) {
			logger.error("Error when getting all data from Applications table");
		}
		return appsLista;
	}//end of method
	
	/**
	 * this method is used to add application details into db
	 * @param applicationServiceData
	 * @param request
	 * @throws DataBaseOperationFailedException
	 * @throws MarathonServiceException
	 * @throws InterruptedException
	 * @throws ApplicationServiceException
	 * @throws UnableToUnMarshalXML
	 * @throws JSONException 
	 * @throws UnableToLoadPropertyFileException 
	 */
	@POST
	@Path("/addApplicationXml")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String addApplicationXml(String applicationServiceData,@Context HttpServletRequest request) throws DataBaseOperationFailedException, MarathonServiceException, InterruptedException, ApplicationServiceException, UnableToUnMarshalXML, JSONException, UnableToLoadPropertyFileException{
		logger.debug(".addApplicationXml method of ApplicationService " + applicationServiceData);
		HttpSession session = request.getSession(true);
		logger.debug("Session: " + session);
		int tenantId = (int) session.getAttribute("id");
		logger.debug("Tenant ID: " + tenantId);
		XMLValidation xmlValidation = new XMLValidation();
		TenantConfiguration configuration = xmlValidation.getApplicationConfiguration(applicationServiceData, ConfigurationConstants.APPLICATION_CONFIG_XSD);
		logger.debug("Company Name: " + configuration.getTenant().getCompanyName());
		for(ApplicationConfig appConfig: configuration.getTenant().getApplictionsConfig().getApplicationConfig()) {
			logger.debug("Application Name: " + appConfig.getApplicationName());
			String id = new ApplicationService().createApplicationByName(appConfig.getApplicationName(), request);
			if(id != null) {
				//Checking whether VPC name is exist or not
				String vpcName = appConfig.getNetworkConfig().getVpcName();
				if(vpcName != null) {
					VpcDAO vpcService = new VpcDAO();
					int vpcID = vpcService.getVPCIdByVPCNames(vpcName, tenantId);
					logger.debug("vpcExist: " + vpcID);
					//Check whether Subnet Name Exist or Not Based on VPCID
					if(vpcID != 0) {
						String subnetName = appConfig.getServiceConfig().getSubnet().getSubnetName();
						logger.debug("Subnet Name: " + subnetName);
						SubnetDAO subnetDAO = new SubnetDAO();
						boolean isExistSubnetName = subnetDAO.checkVPCNameExistInSubnet(subnetName, tenantId);
						if(!isExistSubnetName) {
							logger.debug("Subnet Name Doesn't Exist");
							String msg = "Error Code: " + PAASConstant.SUBNET_NAME_NOT_EXIST + ", " + PAASGenericHelper.getPropertyFile(ERROR_CODE_PROP_FILE).getProperty(PAASConstant.SUBNET_NAME_NOT_EXIST);
							return msg;
						}
					}
				}
				Service service = new Service();
				service.setAppsId(Integer.parseInt(id));
				ServiceConfig serviceConfig = appConfig.getServiceConfig();
				String serviceName = serviceConfig.getService().getName();
				service.setServiceName(serviceName);
				String serviceType = serviceConfig.getService().getType();
				service.setType(serviceType);
				byte instanceCount = serviceConfig.getService().getInstanceCount();
				service.setInstanceCount(instanceCount);
				String name = serviceConfig.getImageRegistry().getName();
				service.setImageRegistry(name);
				String imageRegistry = serviceConfig.getImageRegistry().getRepository();
				service.setImageRegistry(imageRegistry);
				String tag = serviceConfig.getImageRegistry().getTag();
				service.setImageRepository(tag);
				String runCommand = serviceConfig.getRunSettings().getRunCommand();
				service.setRun(runCommand);
				String hostName = serviceConfig.getRunSettings().getHostName();
				service.setHostName(hostName);
				String portType = serviceConfig.getNetworkPolicy().getPortType();
				service.setPortType(portType);
				int hostPort = serviceConfig.getNetworkPolicy().getHostPort();
				service.setHostPort(hostPort);
				int containerPort = serviceConfig.getNetworkPolicy().getContainerPort();
				service.setContainerPort(containerPort);
				//Health Check Rout
				String typeName = serviceConfig.getHealthCheckRoute().getTypeName();
				service.setTypeName(typeName);
				int intervals = serviceConfig.getHealthCheckRoute().getNterval();
				service.setEnvInterval(intervals);
				String path = serviceConfig.getHealthCheckRoute().getPath();
				service.setEnvPath(path);
				int threshold = serviceConfig.getHealthCheckRoute().getThreshold();
				service.setEnvThreshold(threshold);
				int ignore = serviceConfig.getHealthCheckRoute().getIgnore();
				service.setEnvIgnore(ignore);
				//Volume 
				int volume = serviceConfig.getVolume();
				service.setVolume(volume);
				//Subnet
				String subnetName = serviceConfig.getSubnet().getSubnetName();
				service.setSubnetName(subnetName);
				String cidr = serviceConfig.getSubnet().getCidr();
				service.setCidr(cidr);
				logger.debug("Service: " + service);
				Gson gson = new Gson();
				String serviceJSON = gson.toJson(service);
				logger.debug("Service JSON: " + serviceJSON);
				new ApplicationService().addService(serviceJSON, request);
				logger.debug("Successfully...");
			}
		}
		return PAASConstant.SUCCESS;
	}//end of method addApplicationXml
	
}
