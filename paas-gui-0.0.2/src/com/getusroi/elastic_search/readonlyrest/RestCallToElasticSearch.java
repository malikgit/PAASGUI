package com.getusroi.elastic_search.readonlyrest;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.indices.CreateIndex;

import java.io.IOException;
import java.util.Arrays;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.transport.FailedCommunicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.elastic_search.exception.AuthenticationFailedException;
import com.getusroi.elastic_search.exception.ConnectionFailedException;
import com.getusroi.paas.vo.PaasUserRegister;

 

/**
 * RestCallToElasticSearch Class.<br>
 * Rest call is made with user auth to Elastic search, <br>
 * the match documents is diplayed.
 * 
 * @author bizruntime
 */
public class RestCallToElasticSearch {

	protected static Logger LOGGER = LoggerFactory.getLogger(RestCallToElasticSearch.class);

	/**
	 * Connection is established to the elastic search, <br>
	 * and the Auth is passed with the search query.
	 * 
	 * @return jestClient
	 * @throws AuthenticationFailedException
	 */

	public static JestClient getClient() throws AuthenticationFailedException {

		LOGGER.debug("inside (.) getclient RestCallToElasticSearch class ");

		JestClient jestClient = null;
		JestClientFactory factory = new JestClientFactory();

		try {
			BasicCredentialsProvider customCredentialsProvider = new BasicCredentialsProvider();
			customCredentialsProvider.setCredentials(new AuthScope("127.0.0.1",
					//9200), new UsernamePasswordCredentials("user3", "user3"));
					9200), new UsernamePasswordCredentials("admin", "admin"));
			factory.setHttpClientConfig(new HttpClientConfig.Builder(Arrays
					.asList("http://127.0.0.1:9200"))
					.credentialsProvider(customCredentialsProvider)
					// .defaultCredentials("admin", "admin")
					.multiThreaded(true).build());
			jestClient = factory.getObject();
		} catch (FailedCommunicationException fce) {
			// #TODO Read Error code from properties file
			throw new AuthenticationFailedException(
					"401 Authentication problem");
		}
		LOGGER.info("connection to elasticsearch success");
		LOGGER.debug("end of getclient()");
		return jestClient;

	}

	/**
	 * The search query is formed and with the connection and auth, <br>
	 * the query hits the elastic search.
	 * 
	 * @return searchResult
	 * @throws ConnectionFailedException
	 */
	public static SearchResult getSearchResult()
			throws ConnectionFailedException {

		String query = "{" + "" + "    \"query\": {" + "        \"bool\": {"
				+ "            \"must\": [" + "                {"
				+ "                    \"range\": {"
				+ "                        \"age\": {"
				+ "                            \"gt\": \"23\","
				+ "                            \"lt\": \"27\""
				+ "                        }" + "                    }"
				+ "                }" + "            ],"
				+ "            \"must_not\": [ ],"
				+ "            \"should\": [ ]" + "        }" + "    },"
				+ "    \"from\": 0," + "    \"size\": 10,"
				+ "    \"sort\": [ ]," + "    \"aggs\": { }" + "" + "}";
		Search search = new Search.Builder(query).addIndex("bizruntime").build();
		SearchResult result = null;

		try {
			result = RestCallToElasticSearch.getClient().execute(search);
			LOGGER.info("query result from elasticsearch"
					+ result.getJsonString());
			LOGGER.debug("end of getresult()");
		} catch (Exception e) {
			// #TODO Read Error code from properties file
			throw new ConnectionFailedException(
					"Could not connect to ElasticSearch for the given index :  "
							+ search.getIndex());
		}
		return result;
	}

	public  void addIndexByTenant(PaasUserRegister tenant){
		LOGGER.info("Inside (.) addIndexByTenant of RestCallToElasticSearch ");
		 
			JestClient jestClient;
			try {
				jestClient = RestCallToElasticSearch.getClient();
				LOGGER.debug("jestClient=== "+jestClient );
				jestClient.execute(new CreateIndex.Builder(tenant.getTenant_name()).build());
			} catch (AuthenticationFailedException e) {
				LOGGER.error("Authontication failed with tenant name "+tenant.getTenant_name(),e);
			} catch (IOException  e) {
				LOGGER.error("Error while creating index in elastic with tenant name ",e);
			}
			 
	}
	
	public static void main(String[] args) {
		try {
			RestCallToElasticSearch.getSearchResult();
		} catch (ConnectionFailedException e) {
			LOGGER.error(e.getMessage());
		}
	}

}
