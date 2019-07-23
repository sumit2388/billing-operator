package net.one97.billing.halotal.protocol.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RestClient {
	private static final Logger logger = LoggerFactory.getLogger(RestClient.class);
	
	private static Client client = null;

	public String serviceCall(String url, String msisdn) {

		logger.info(" Inside RestClient , method serviceCall ");
		ClientResponse ClientResponse =null;
		String result =null;
		try {
			logger.info("msisdn "+msisdn +" halotel url " + url);
			
			if(client == null ){				
				 client = Client.create();
				 logger.info("msisdn "+msisdn +" client created");
				 client.setReadTimeout(50000);
			}
			WebResource webResource = client.resource(url);
			 ClientResponse = webResource.accept(MediaType.TEXT_PLAIN).get(ClientResponse.class);
			 
			 logger.info("msisdn "+msisdn +" halotel ClientResponse " );
			 result = getStringFromInputStream(ClientResponse.getEntityInputStream());

			logger.info("msisdn "+msisdn +" halotel response " + result);
			} catch (Exception e) {
			logger.error("msisdn "+msisdn +" Exception " ,e);

		}
		finally{			
			if(ClientResponse != null)
			{
				ClientResponse.close();
				logger.info("msisdn "+msisdn +" client closed");
				ClientResponse = null;
			}			
		}
		return result;

	}
	
	
		
	private String getStringFromInputStream(InputStream is) {
		BufferedReader br = null;
		final StringBuilder sb = new StringBuilder();
		logger.info(" inside getStringFromInputStream ");
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					logger.info(" IOException " + e);
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

}
