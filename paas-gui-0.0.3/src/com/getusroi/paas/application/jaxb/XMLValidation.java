package com.getusroi.paas.application.jaxb;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;


/**
 * this class is used to validate the xml based on xsd
 * @author bizruntime
 *
 */
public class XMLValidation {

	private static final Logger logger = LoggerFactory.getLogger(XMLValidation.class);
	private static final String W3C_XML_SCHEMA_NS_URI = "http://www.w3.org/2001/XMLSchema";
	
	/**
	 * this method is used to validate the xml based on xsd
	 * @param configName
	 * @param schemaName
	 */
	static boolean validateConfig(String configName, String schemaName) {
		SchemaFactory factory = SchemaFactory
				.newInstance(W3C_XML_SCHEMA_NS_URI);
		InputStream isSchema = XMLValidation.class.getClassLoader()
				.getResourceAsStream(schemaName);
		Source schemaSource = new StreamSource(isSchema);
		Source configSource;

		try {
			Schema schema = factory.newSchema(schemaSource);
			logger.debug("schema: " + schema);
			Validator validator = schema.newValidator();
			logger.debug("validator: " + validator);
			InputStream isConfig = new ByteArrayInputStream(configName.getBytes());
			logger.debug("isConfig: " + isConfig);
			configSource = new StreamSource(isConfig);
			logger.debug("configSource: " + configSource);
			validator.validate(configSource);
			logger.debug("Valid configuration..");
		} catch (SAXException | IOException e) {
			logger.error("Validation failed cross the schema for the configuration of "
					+ schemaName,e);
			return false;
		}
		return true;
	} //end of method
	
	/**
	 * this method is used to get tenant configuration
	 * @return
	 * @throws UnableToUnMarshalXML 
	 */
	public TenantConfiguration getApplicationConfiguration(String applicationXml, String schemaName) throws UnableToUnMarshalXML {
		try {
			JAXBContext context = JAXBContext.newInstance(TenantConfiguration.class);
			Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
			boolean validateXml = validateConfig(applicationXml, schemaName);
			if(validateXml) {
				InputStream isConfig = new ByteArrayInputStream(applicationXml.getBytes());
				Source configSource = new StreamSource(isConfig);
				return (TenantConfiguration) jaxbUnmarshaller.unmarshal(configSource);
			}
			else{
				logger.error("Unable to Unmarshal of Application Configuration ");
				throw new UnableToUnMarshalXML("Unable to Unmarshal of Application Configuration ");
			}
		} catch (JAXBException e) {
			logger.error("Unable to Unmarshal of Application Configuration " + e);
			throw new UnableToUnMarshalXML("Unable to Unmarshal of Application Configuration " + e);
		}
	}//end of method
	
}
