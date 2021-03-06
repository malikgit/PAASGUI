package com.getusroi.paas.rest.service;

import static com.getusroi.paas.helper.PAASConstant.ALL_REPOSTORY_KEY;
import static com.getusroi.paas.helper.PAASConstant.ALL_TAGS_KEY;
import static com.getusroi.paas.helper.PAASConstant.DOCKER_HUB_PROPERTY_FILE_KEY;
import static com.getusroi.paas.helper.PAASConstant.HTTPS_PROTOCOL_KEY;
import static com.getusroi.paas.helper.PAASConstant.IMAGE_RESISTRY_NAME;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.paas.configuration.ConfigurationConstants;
import com.getusroi.paas.configuration.InvalidConfigException;
import com.getusroi.paas.dao.DataBaseOperationFailedException;
import com.getusroi.paas.dao.ImageRegistryDAO;
import com.getusroi.paas.helper.PAASGenericHelper;
import com.getusroi.paas.helper.UnableToLoadPropertyFileException;
import com.getusroi.paas.imageregistry.jaxb.ImageRegistryJAXBHelper;
import com.getusroi.paas.imageregistry.jaxb.ImageTenantConfigs;
import com.getusroi.paas.imageregistry.jaxb.Tenant;
import com.getusroi.paas.imageregistry.jaxb.TenantConfiguration;
import com.getusroi.paas.imageregistry.jaxb.UnableToUnmarshalImageRegistry;
import com.getusroi.paas.jaxb.AddImageRegistry;
import com.getusroi.paas.jaxb.ImageRegistryTenantConfig;
import com.getusroi.paas.rest.RestServiceHelper;
import com.getusroi.paas.rest.service.exception.ApplicationServiceException;
import com.getusroi.paas.rest.service.exception.ImageRegistryServiceException;
import com.getusroi.paas.sdn.service.SDNInterface;
import com.getusroi.paas.sdn.service.impl.SDNServiceImplException;
import com.getusroi.paas.sdn.service.impl.SDNServiceWrapperImpl;
import com.getusroi.paas.vo.ImageRegistry;
import com.google.gson.Gson;


@Path("/imageRegistry")
public class ImageRegistryService {
	 private static final Logger logger = LoggerFactory.getLogger(ImageRegistryService.class);
	 private ImageRegistryTenantConfig tenantConfig;
	 private ImageRegistryDAO imgRegistryDAO = null;
	 private ObjectMapper mapper = null;
	 
	 /**
	  * Rest Service to insert imager Registry details into DB
	  * @param imageRegistryData
	  * @param req
	  * @return
	  * @throws DataBaseOperationFailedException
	  * @throws SDNServiceImplException
	  * @throws ImageRegistryServiceException
	  */
	@POST
	@Path("/addImageRegistry")
	@Produces(MediaType.TEXT_PLAIN)
	public String addImageRegistry(String imageRegistryData,@Context HttpServletRequest req) throws DataBaseOperationFailedException, SDNServiceImplException, ImageRegistryServiceException{
		logger.debug(".addImageRegistry method of ImageRegistryService");
		imgRegistryDAO = new ImageRegistryDAO();
		mapper = new ObjectMapper();
		SDNInterface sdnService=new SDNServiceWrapperImpl();
		String responseMessage=null;
		try {
			ImageRegistry imageRegistry = mapper.readValue(imageRegistryData, ImageRegistry.class);
			HttpSession session = req.getSession(true);
			if(session != null && imageRegistry != null)
				imageRegistry.setTenant_id((int)session.getAttribute("id"));
			imgRegistryDAO.addImageRegistry(imageRegistry);
			String username = imageRegistry.getUser_name();
			String pass = imageRegistry.getPassword();
			String url = imageRegistry.getLocation();
			logger.debug("username : "+username+ " pass: "+pass+ " url : "+url+" id "+session.getAttribute("id"));
			boolean response=sdnService.getUserDetailsRegistry(imageRegistry);
			if(response)
				responseMessage= "Success";
			else
				responseMessage= "failure";
		} catch (IOException e) {
			logger.error("Error in reading value from image registry  : "+imageRegistryData+" using object mapper in addImageRegistry",e);
			throw new ImageRegistryServiceException("Error in reading value from image registry  : "+imageRegistryData+" using object mapper in addImageRegistry");
		}
		
		return responseMessage;
	}//end of method addImageRegistry

	/**
	 * this method is used to get xml input from UI and store data into db
	 * @param imageRegistryData
	 * @param req
	 * @return
	 * @throws DataBaseOperationFailedException
	 * @throws SDNServiceImplException
	 * @throws ImageRegistryServiceException
	 * @throws InvalidConfigException 
	 * @throws UnableToUnmarshalImageRegistry 
	 */
	@POST
	@Path("/addXmlImageRegistry")
	@Produces(MediaType.TEXT_PLAIN)
	public void addXmlImageRegistry(String imageRegistryData,@Context HttpServletRequest req) throws DataBaseOperationFailedException, SDNServiceImplException, ImageRegistryServiceException, InvalidConfigException, UnableToUnmarshalImageRegistry{
		logger.debug(".addXmlImageRegistry method of ImageRegistryService");
		imgRegistryDAO = new ImageRegistryDAO();
		ImageRegistryJAXBHelper imageRegistryJAXBHelper = new ImageRegistryJAXBHelper();
		try {
			TenantConfiguration tenantConfig = imageRegistryJAXBHelper.getImageRegistryConfig(imageRegistryData, ConfigurationConstants.IMAGE_REG_CONFIG_XSD);
			logger.debug("tenantConfig: " + tenantConfig);
			int tenantID = tenantConfig.getTenant().getId();
	
			List<com.getusroi.paas.imageregistry.jaxb.ImageRegistry> imageRegistries = tenantConfig.getTenant().getImageTenantConfigs().getImageRegistry();
			ImageRegistry registry;
			for(com.getusroi.paas.imageregistry.jaxb.ImageRegistry image : imageRegistries) {
				registry = new ImageRegistry();
				registry.setTenant_id(tenantID);
				registry.setName(image.getName());
				registry.setLocation(image.getLocation());
				registry.setVersion(image.getVersion());
				registry.setUser_name(image.getUserName());
				registry.setPassword(image.getPassword());
				logger.debug("PassWord: " + image.getPassword());
				imgRegistryDAO.addImageRegistry(registry);
			}
		} catch (Exception e) {
			throw new ImageRegistryServiceException();
		}
		
	}//end of method addImageRegistry
	
	/**
	 * Rest Service To Get All Image Registry By using Tenant id
	 * @param req
	 * @return
	 * @throws DataBaseOperationFailedException
	 */
	@GET
	@Path("/getAllImageRegistry")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllImageRegistry(@Context HttpServletRequest req) throws DataBaseOperationFailedException{
		logger.debug(".selectImageRegistry method of ImageRegistryService");
		imgRegistryDAO = new ImageRegistryDAO();
		RestServiceHelper restServcHelper= new RestServiceHelper();
		HttpSession session = req.getSession(true);
		if(session != null){
			int tenantId = restServcHelper.convertStringToInteger(session.getAttribute("id")+"");
			List<ImageRegistry> imageRegistryList=imgRegistryDAO.getAllImageRegistry(tenantId);
			Gson gson = new Gson();
			String imageRegistryListInJSON=gson.toJson(imageRegistryList);
			
			return imageRegistryListInJSON;	
		}else{
			return null;
		}
		
	}//end of method getAllImageRegistry
	 
	/**
	 * this method is used to get all data from image registry and converted return XML
	 * @param req
	 * @return
	 * @throws DataBaseOperationFailedException
	 * @throws JAXBException 
	 */
	@GET
	@Path("/getAllImageRegistryXml")
	@Produces(MediaType.TEXT_PLAIN)
	public String getAllImageRegistryXml(@Context HttpServletRequest req) throws DataBaseOperationFailedException, JAXBException{
		logger.debug(".selectImageRegistry method of ImageRegistryService");
		imgRegistryDAO = new ImageRegistryDAO();
		RestServiceHelper restServcHelper= new RestServiceHelper();
		HttpSession session = req.getSession(true);
		if(session != null){
			int tenantId = restServcHelper.convertStringToInteger(session.getAttribute("id")+"");
			List<ImageRegistry> imageRegistryList=imgRegistryDAO.getAllImageRegistry(tenantId);
			List<com.getusroi.paas.imageregistry.jaxb.ImageRegistry> imageRegistries = new ArrayList<>();
			for(ImageRegistry image : imageRegistryList) {
				com.getusroi.paas.imageregistry.jaxb.ImageRegistry registry = new com.getusroi.paas.imageregistry.jaxb.ImageRegistry();
				registry.setName(image.getName());
				registry.setLocation(image.getLocation());
				registry.setUserName(image.getUser_name());
				registry.setPassword(image.getPassword());
				registry.setVersion(image.getVersion());
				imageRegistries.add(registry);
			}
			TenantConfiguration tenantConfiguration = new TenantConfiguration();
			tenantConfiguration.setTenant(new Tenant());
			tenantConfiguration.getTenant().setId(tenantId);
			tenantConfiguration.getTenant().setImageTenantConfigs(new ImageTenantConfigs());
			tenantConfiguration.getTenant().getImageTenantConfigs().setImageRegistry(new ArrayList<com.getusroi.paas.imageregistry.jaxb.ImageRegistry>());
			for(com.getusroi.paas.imageregistry.jaxb.ImageRegistry imageReg : imageRegistries){
				tenantConfiguration.getTenant().getImageTenantConfigs().getImageRegistry().add(imageReg);
			}
			String imageRegistryXML = marshaling(tenantConfiguration);
			logger.debug("Image Registry XML: " + imageRegistryXML);
			return imageRegistryXML;	
		}else{
			return null;
		}
		
	}//end of method getAllImageRegistry
	
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
	 * Rest Service to Delete image Registry by image registry id
	 * @param imageId
	 * @param userName
	 * @return
	 * @throws DataBaseOperationFailedException
	 */
	@GET
	@Path("/deleteImageRegistry/{imageId}/{userName}")
	@Produces(MediaType.TEXT_PLAIN)
	public String deleteImageRegistry(@PathParam("imageId") String imageId,@PathParam("userName") String userName) throws DataBaseOperationFailedException{
		logger.debug(".deleteImageRegistry method of ImageRegistryService imageId >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>. "+imageId +" userName "+userName);
		imgRegistryDAO = new ImageRegistryDAO();
		imgRegistryDAO.deleteImageRegistryById(new RestServiceHelper().convertStringToInteger(imageId), userName);	
		return "delete successful for image registry with image name : "+imageId+" and user name : "+userName;
	}//end of method deleteImageRegistry
	
	/**
	 * Rest Service to get Docker Hub Registry Tags from the web
	 * @param repositoryName
	 * @param req
	 * @return
	 * @throws DataBaseOperationFailedException
	 * @throws ApplicationServiceException
	 */
	@POST
	@Path("/getDockerHubRegistryTags")
	@Produces(MediaType.APPLICATION_JSON)
	public String getApplicationSummary(String repositoryName,@Context HttpServletRequest req) throws DataBaseOperationFailedException, ApplicationServiceException{
		logger.debug(".getApplicationSummary method of ApplicationService "+repositoryName);
		JSONObject jsonObject =new JSONObject(repositoryName);
		imgRegistryDAO = new ImageRegistryDAO();
		HttpSession session = req.getSession(true);
		
		ImageRegistry imageRegistry = imgRegistryDAO.getImageRegistryByName(jsonObject.getString("imageRegistry"),(int)session.getAttribute("id"));
		logger.debug("imageRegistry: "+imageRegistry);
		String response=null;
		String baseURL = null;
		String authentication= null;
		if(imageRegistry != null ){
			try {
				Properties prop = PAASGenericHelper.getPropertyFile(DOCKER_HUB_PROPERTY_FILE_KEY);
				baseURL = prop.getProperty(HTTPS_PROTOCOL_KEY)+prop.getProperty(IMAGE_RESISTRY_NAME)+ prop.getProperty(ALL_REPOSTORY_KEY)+imageRegistry.getLocation()+prop.getProperty(ALL_TAGS_KEY);
				logger.debug("baseURL: "+baseURL);
				authentication=imageRegistry.getUser_name() + ":" + imageRegistry.getPassword();
				// response=getHttpResponse(baseURL, authentication, "GET");
				 authentication="";
				 response=getHttpResponse(baseURL,authentication , "GET");
				logger.debug("http response  : "+response);				
			}catch (UnableToLoadPropertyFileException e) {
				logger.error("Error in reading file : "+DOCKER_HUB_PROPERTY_FILE_KEY+" in getApplicationSummary");
				throw new ApplicationServiceException("Unable to read properties file name :"+DOCKER_HUB_PROPERTY_FILE_KEY);
			}
			catch (IOException e) {
				logger.error("Unable to get the http response using base url :"+baseURL+", authentication : "+authentication);
				throw new ApplicationServiceException("Unable to get the http response using base url :"+baseURL+", authentication : "+authentication);
			}
		}else{
			logger.debug("No image repository availabel with name : "+repositoryName);
		}
		logger.debug("response "+response);
		return response;
	}//end of method getApplicationSummary
	
	/**
	 * This method is used to get response from http client
	 * @param baseURL : base url in String
	 * @param authentication : authentication in String
	 * @param httpRequestMethod : http request method
	 * @return String : response data in String
	 * @throws IOException : Unable to connect to url using http client
	 */
	private String getHttpResponse(String baseURL,String authentication,String httpRequestMethod) throws IOException{
		logger.debug(".getHttpResponse method of ApplicationService");	
		StringBuffer result = new StringBuffer();
		URL url = new URL(baseURL);       
		//String authStr = summary.getUser_name() + ":" + summary.getPassword();
        String encodedAuthStr = Base64.encodeBase64String(authentication.getBytes());
		// Create Http connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if(connection!=null){
        // Set connection properties
        connection.setRequestMethod(httpRequestMethod);
        connection.setRequestProperty("Authorization", "Basic "
                + encodedAuthStr);
        connection.setRequestProperty("Accept", "application/json");        
        logger.debug("Response  Code"+connection.getResponseCode());        
        InputStream content = (InputStream) connection.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                content));
        String line = "";
        while ((line = in.readLine()) != null) {
            result.append(line);
        }
        }
        return result.toString();
	}//end of method getHttpResponse
	 
	/**
	 * Rest Service to check image registry with provided name exist or not with tenant id
	 * @param registryName
	 * @param req
	 * @return
	 * @throws DataBaseOperationFailedException
	 */
	@GET
	@Path("/checkimageRegistry/{registryName}")
	@Produces(MediaType.TEXT_PLAIN)
	public String imgRegistryValidation(@PathParam("registryName") String registryName,@Context HttpServletRequest req) throws DataBaseOperationFailedException {
		 logger.debug(" coming to check Image Registry of pass network");
		 HttpSession session = req.getSession(true);
		 imgRegistryDAO = new ImageRegistryDAO();
		 int id = imgRegistryDAO.getImageRegistryIdByName(registryName,(int)session.getAttribute("id"));
		if(id>0)
			return "success";
		else
		 return "failure" ;
		
	 }//End of method checkimageRegistry validation
	
	 /**
	  * to check image registry username exist or not
	  * @param userName
	  * @param req
	  * @return
	  * @throws DataBaseOperationFailedException
	  */
	 
	@GET
	@Path("/checkingUserName/{userName}")
	@Produces(MediaType.TEXT_PLAIN)
	public String userValidation(@PathParam("userName") String userName,
			@Context HttpServletRequest req)
			throws DataBaseOperationFailedException {
		logger.debug(" coming to check Image Registry of pass network");
		HttpSession session = req.getSession(true);
		imgRegistryDAO = new ImageRegistryDAO();
		int id = imgRegistryDAO.getImgRegistryIdByUser_NameandTenant_Id(userName,
				(int) session.getAttribute("id"));
		if (id > 0)
			return "success";
		else
			return "failure";
	}// end of method aClByName validation
	 
	/**
	 * Rest Service To update image Registry by image registry id
	 * @param updateEnviromentType
	 * @param req
	 * @return
	 * @throws DataBaseOperationFailedException
	 * @throws SDNServiceImplException
	 * @throws ImageRegistryServiceException
	 */
	@PUT
	@Path("/updateImageRegistry")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String updateImageRegistry(String updateEnviromentType,@Context HttpServletRequest req) throws DataBaseOperationFailedException, SDNServiceImplException, ImageRegistryServiceException{
		logger.debug(".update EnvironmentType method of EnvironmentTypeService"+updateEnviromentType);
		mapper = new ObjectMapper();
		imgRegistryDAO = new ImageRegistryDAO();
		try {
			//HttpSession session =req.getSession();
			ImageRegistry imageRegistryVO = mapper.readValue(updateEnviromentType, ImageRegistry.class);
			imgRegistryDAO.updateImageRegistry(imageRegistryVO);
			logger.debug("username : "+imageRegistryVO.getName()+ " pass: "+imageRegistryVO.getPassword());			
			return "Success";
		} catch (IOException e) {
			logger.error("Error in reading value from image registry  : "+updateEnviromentType+" using object mapper in addImageRegistry",e);
			throw new ImageRegistryServiceException("Error in reading value from image registry  : "+updateEnviromentType+" using object mapper in addImageRegistry");
		}
	}
}
