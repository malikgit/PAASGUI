package com.getusroi.paas.configuration;

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

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.getusroi.paas.jaxb.ImageRegistryTenantConfig;
import com.getusroi.paas.jaxb.SubnetTenantConfiguration;
import com.getusroi.paas.jaxb.TenantConfiguration;

public class PassGUIConfiguration {
	private static Logger log = Logger.getLogger(PassGUIConfiguration.class
			.getName());
	private static final String W3C_XML_SCHEMA_NS_URI = "http://www.w3.org/2001/XMLSchema";

	public static TenantConfiguration getVpcConfiguration(
			String vpcConfigFileName, String vpcConfigSchemaName)
			throws InvalidConfigException {
		log.debug("VPC Xml File: " + vpcConfigFileName);
		log.debug("VPC Config Schema: " + vpcConfigSchemaName);
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance(TenantConfiguration.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			validateConfig(vpcConfigFileName, vpcConfigSchemaName);
			InputStream isConfig = new ByteArrayInputStream(vpcConfigFileName.getBytes());
			Source configSource = new StreamSource(isConfig);
			return (TenantConfiguration) jaxbUnmarshaller
					.unmarshal(configSource);
		} catch (JAXBException e) {
			throw new InvalidConfigException(
					"Unable to unmarshal configuration of the producer"
							+ vpcConfigFileName, e);
		}
	}

	public static SubnetTenantConfiguration getsubnetConfiguration(
			String vpcConfigFileName, String vpcConfigSchemaName)
			throws InvalidConfigException {
		log.debug("Subnet XML: " + vpcConfigFileName);
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance(SubnetTenantConfiguration.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			validateConfig(vpcConfigFileName, vpcConfigSchemaName);
			InputStream isConfig = new ByteArrayInputStream(vpcConfigFileName.getBytes());
			Source configSource = new StreamSource(isConfig);
			return (SubnetTenantConfiguration) jaxbUnmarshaller
					.unmarshal(configSource);
		} catch (JAXBException e) {
			throw new InvalidConfigException(
					"Unable to unmarshal configuration of the producer"
							+ vpcConfigFileName, e);
		}

	}

	public static ImageRegistryTenantConfig getImageRegistryConfiguration(
			String vpcConfigFileName, String vpcConfigSchemaName)
			throws InvalidConfigException {
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance(ImageRegistryTenantConfig.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			validateConfig(vpcConfigFileName, vpcConfigSchemaName);
			InputStream isConfig = new ByteArrayInputStream(vpcConfigFileName.getBytes());
			Source configSource = new StreamSource(isConfig);
			return (ImageRegistryTenantConfig) jaxbUnmarshaller
					.unmarshal(configSource);
		} catch (JAXBException e) {
			throw new InvalidConfigException(
					"Unable to unmarshal configuration of the producer"
							+ vpcConfigFileName, e);
		}
	}

	static void validateConfig(String configName, String schemaName) {
		SchemaFactory factory = SchemaFactory
				.newInstance(W3C_XML_SCHEMA_NS_URI);
		InputStream isSchema = PassGUIConfiguration.class.getClassLoader()
				.getResourceAsStream(schemaName);
		Source schemaSource = new StreamSource(isSchema);
		Source configSource;

		try {
			Schema schema = factory.newSchema(schemaSource);
			log.debug("schema: " + schema);
			Validator validator = schema.newValidator();
			log.debug("validator: " + validator);
			InputStream isConfig = new ByteArrayInputStream(configName.getBytes());
			log.debug("isConfig: " + isConfig);
			configSource = new StreamSource(isConfig);
			log.debug("configSource: " + configSource);
			validator.validate(configSource);
			log.debug("Valid configuration..");
		} catch (SAXException | IOException e) {
			log.error("Validation failed cross the schema for the configuration of "
					+ schemaName,e);
		}
	}
}
