package com.getusroi.paas.freenas;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.paas.helper.PAASGenericHelper;
import com.getusroi.paas.helper.UnableToLoadPropertyFileException;

public class FreeNasFlowManager {

	private static final Logger logger = LoggerFactory.getLogger(FreeNasFlowManager.class);
	private static final int CREATED = 201;

	public boolean createVolume(JSONObject postData) throws FreeNasFlowManagerException, IOException, UnableToLoadPropertyFileException {
		logger.info("createVolume (.) createVolume of FreeNasFlowManager ");
		HttpURLConnection connection = null;
		int callStatus = 0;
		Properties properties = PAASGenericHelper.getPropertyFile("nasStorage.properties");
		logger.debug("user name"+properties.getProperty("nasUserName")+" pwd "+properties.getProperty("nasPassword")+
				" naas url "+properties.getProperty("nasURL"));
		// Creating the actual URL to call
		String baseURL = properties.getProperty("nasURL");

		try {
			// Create URL = base URL + container
			URL url;
			url = new URL(baseURL);
			// Create authentication string and encode it to Base64
			String authStr = properties.getProperty("nasUserName") + ":"
					+ properties.getProperty("nasPassword");
			
			/*String authStr = "root" + ":"
					+ "bizruntime@123";*/
			String encodedAuthStr = Base64.encodeBase64String(authStr.getBytes());
			
			// Create Http connection
			connection = (HttpURLConnection) url.openConnection();
			// Set connection properties
			connection.setRequestMethod(FreeNasConstants.METHOD);
			connection.setRequestProperty("Authorization", FreeNasConstants.AUTHORIZATION + encodedAuthStr);
			connection.setRequestProperty("Content-Type", FreeNasConstants.CONTENT_TYPE);
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			// Set Post Data
			OutputStream os = connection.getOutputStream();
			os.write(postData.toString().getBytes());
			os.close();
			// Getting the response code
			callStatus = connection.getResponseCode();
		} catch (IOException e) {
			logger.error("Unexpected error while volume installation.. " , e);
			throw new FreeNasFlowManagerException("Unexpected error while volume installation..", e);
		} finally {
			if (connection != null)
				connection.disconnect();
		}
		if (callStatus == CREATED) {
			logger.info("FreeNas volume created Successfully");
			return true;
		} else {
			logger.error("Failed to create Controller.. " + callStatus);
			return false;
		}
	}

}
