package com.getusroi.paas.rest.service;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.paas.freenas.FreeNasFlowManager;
import com.getusroi.paas.freenas.FreeNasFlowManagerException;
import com.getusroi.paas.helper.UnableToLoadPropertyFileException;
import com.getusroi.paas.vo.Storage;

@Path("/freeNasService")
public class FreeNasService {

	private static final Logger logger = LoggerFactory.getLogger(FreeNasService.class);
	
	@POST
	@Path("/createNas")
	@Consumes(MediaType.APPLICATION_JSON)
	public String createVolume(String nasData) throws JSONException, FreeNasFlowManagerException, JsonParseException, JsonMappingException, IOException, UnableToLoadPropertyFileException {
		logger.debug("inside  Create  volume  data");
		ObjectMapper mapper = new ObjectMapper();
		Storage storage = mapper.readValue(nasData, Storage.class);
		logger.debug("Storage Data: " + storage);
		JSONObject postData = new JSONObject();
		String volumeName = storage.getServiceName();
		String volSize = storage.getVolumeSize();
		logger.debug("volumeName " + volumeName);
		logger.debug("volsize " + volSize);
		postData.put("name", volumeName);
		postData.put("volsize", volSize);
		new FreeNasFlowManager().createVolume(postData);
		return "Ok";
	}
	
	@POST
	@Path("/getVolumeSizeFromNas")
	public void getVolumeSizeFromNas(String serviceName) {
		logger.debug("Inside getVolumeSizeFromNas Service Name: " + serviceName);
	}

}
