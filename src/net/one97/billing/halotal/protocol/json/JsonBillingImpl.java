package net.one97.billing.halotal.protocol.json;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.one97.billing.core.exception.ValidationException;
import net.one97.billing.core.factory.BeanFactory;
import net.one97.billing.core.protocol.HttpBillingRetryHandler;
import net.one97.billing.core.protocol.http.HttpBilling;
import net.one97.billing.core.util.BillingUtil.SubscriberType;
import net.one97.billing.core.util.ErrorCode;
import net.one97.billing.core.valueObject.BillingRequestVO;
import net.one97.billing.core.valueObject.BillingResponseVO;
import net.one97.billing.core.valueObject.GatewayConfigurationVO;
import net.one97.billing.halotal.commons.Constants;

public class JsonBillingImpl extends HttpBilling{
	
	private  HalotalJsonManagementWrapper halotalJsonManagementWrapper ;

	public void setHalotalJsonManagementWrapper(HalotalJsonManagementWrapper halotalJsonManagementWrapper) {
		this.halotalJsonManagementWrapper = halotalJsonManagementWrapper;
	}

	private static final Logger logger = LoggerFactory
			.getLogger(JsonBillingImpl.class);
	
	public void init() {
		int httpMaxTotConn = Integer.parseInt(BeanFactory.getProperty("halotal.http.Max.Total.Conn"));
		int httpTimeOut = Integer.parseInt(BeanFactory.getProperty("halotal.http.time.out"));
		int httpRetryCnt = Integer.parseInt(BeanFactory.getProperty("halotal.http.retry.count"));
		connectionManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		params.setMaxTotalConnections(httpMaxTotConn);
		params.setDefaultMaxConnectionsPerHost(httpMaxTotConn);
		params.setConnectionTimeout(httpTimeOut);
		connectionManager.setParams(params);
		client = new HttpClient(connectionManager);
		HttpMethodRetryHandler retryHandler = new HttpBillingRetryHandler(httpRetryCnt);
		client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, retryHandler);
		client.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, httpTimeOut);
	}
	
	@Override
	public BillingResponseVO debitAccount(BillingRequestVO billingRequest,
			GatewayConfigurationVO gw) throws ValidationException {
		
		logger.info("Inside class JsonBillingImpl , method debitAccount :: ");

		BillingResponseVO billingResponse = null;
		
		String response_code = null;
		String channel=billingRequest.getChannel();
		logger.info(billingRequest.getMsisdn()+"channel  "+channel);
		Map<String ,String>  paramMap = this.getMap(gw.getParams());
		logger.info(billingRequest.getMsisdn()+"paramMap  "+paramMap.toString());
		Map<String ,String>  chargingCodeMap = this.getMap(gw.getChargingCode());
		logger.info(billingRequest.getMsisdn()+"chargingCodeMap"+chargingCodeMap.toString());
		
		logger.info(billingRequest.getMsisdn()+"gw.getUrl() "+gw.getUrl());
		logger.info(billingRequest.getMsisdn()+"paramMap.get(Constants.PRO) "+paramMap.get(Constants.PRO));	
		logger.info(billingRequest.getMsisdn()+"paramMap.get(Constants.CMD) "+paramMap.get(Constants.CMD));
		
		logger.info(billingRequest.getMsisdn()+"chargingCodeMap.get(Constants.SER) "+chargingCodeMap.get(Constants.SER));	
		logger.info(billingRequest.getMsisdn()+"chargingCodeMap.get(Constants.SUB) "+chargingCodeMap.get(Constants.SUB));
		logger.info(billingRequest.getMsisdn()+"chargingCodeMap.get(Constants.CATE) "+chargingCodeMap.get(Constants.CATE));
		logger.info(billingRequest.getMsisdn()+"chargingCodeMap.get(Constants.ITEM) "+chargingCodeMap.get(Constants.ITEM));
		logger.info(billingRequest.getMsisdn()+"chargingCodeMap.get(Constants.SUB_CP) "+chargingCodeMap.get(Constants.SUB_CP));
		logger.info(billingRequest.getMsisdn()+"chargingCodeMap.get(Constants.CONT) "+chargingCodeMap.get(Constants.CONT));
		logger.info(billingRequest.getMsisdn()+"chargingCodeMap.get(Constants.TYPE) "+chargingCodeMap.get(Constants.TYPE));
		logger.info(billingRequest.getMsisdn()+"chargingCodeMap.get(Constants.SOURCE) "+chargingCodeMap.get(Constants.SOURCE));
		logger.info(billingRequest.getMsisdn()+"chargingCodeMap.get(Constants.IMEI) "+chargingCodeMap.get(Constants.IMEI));
		logger.info(billingRequest.getMsisdn()+"billingRequest.getPricePoint() "+billingRequest.getPricePoint());
		logger.info(billingRequest.getMsisdn()+"billingRequest.getProduct() "+billingRequest.getProduct());
		
		String pricePoint=billingRequest.getPricePoint();
		String product  =billingRequest.getProduct().toUpperCase();
		
		logger.info(billingRequest.getMsisdn()+"product :: "+product);
		
		String  pricePointValue="0";
		if (NumberUtils.isNumber(pricePoint)){	
			pricePointValue =String.valueOf( new BigDecimal(pricePoint).intValue());
			logger.info(billingRequest.getMsisdn()+"pricePointValue :: "+pricePointValue);
		}
	//	int pricePointId=0;
   //		if(chargingCodeMap.get(Constants.PRICE_POINT_ID)!=null && StringUtils.isNumeric(chargingCodeMap.get(Constants.PRICE_POINT_ID)))
	//		pricePointId=Integer.parseInt(chargingCodeMap.get(Constants.PRICE_POINT_ID));  // not provided by client
		if(channel.equalsIgnoreCase("WAP")){
			response_code=Constants.STATUS_CODE_PENDINGMT;
		}else{
			response_code= halotalJsonManagementWrapper.createJsonData(billingRequest.getMsisdn(),pricePointValue,gw.getUrl(),
					paramMap.get(Constants.PRO),chargingCodeMap.get(Constants.SER),
					paramMap.get(Constants.CMD),				
					chargingCodeMap.get(Constants.SUB),chargingCodeMap.get(Constants.CATE),
					chargingCodeMap.get(Constants.ITEM),chargingCodeMap.get(Constants.SUB_CP),
					chargingCodeMap.get(Constants.CONT),chargingCodeMap.get(Constants.TYPE),
					chargingCodeMap.get(Constants.SOURCE),chargingCodeMap.get(Constants.IMEI),product);
		}
		logger.info("Response code received ......."+response_code+" msisdn:"+billingRequest.getMsisdn());
		if(response_code.equalsIgnoreCase(Constants.STATUS_SUCCESS)){
			logger.info(billingRequest.getMsisdn()+"success status true");
			billingResponse = new BillingResponseVO();
			billingResponse.setMsisdn(billingRequest.getMsisdn());
			billingResponse.setBillingRequestId(billingRequest.getId().toString());							
			billingResponse.setResponseMsg(Constants.BILLING_SUCCESS);
			billingResponse.setGatewayResponseCode(response_code+","+billingRequest.getPolicy());
			billingResponse.setResponseString(response_code+","+billingRequest.getPolicy());
			billingResponse.setStatus(Constants.STATUS_CODE_SUCCESS);
			billingResponse.setResponseCode(ErrorCode.GATEWAY_STATUS_SUCCESS);	
			billingResponse.setChargingCode(gw.getChargingCode());
			
			// wallet adjustment code 
		//	accountManagementWrapperTigo.adjustWalletAccount(gw.getUrl(),billingRequest.getMsisdn(),billingRequest.getTxnAmount(),gw.getChargingCode());
		}
		else if (response_code.equalsIgnoreCase(Constants.STATUS_414)){
			billingResponse = new BillingResponseVO();
			billingResponse.setMsisdn(billingRequest.getMsisdn());
			billingResponse.setBillingRequestId(billingRequest.getId().toString());							
			billingResponse.setResponseMsg(Constants.BILLING_FAILURE);
			billingResponse.setGatewayResponseCode(response_code+","+billingRequest.getPolicy());
			billingResponse.setResponseString(response_code+","+billingRequest.getPolicy());
			billingResponse.setStatus(Constants.STATUS_CODE_UNKNOWN);
			billingResponse.setResponseCode("414");     
			billingResponse.setChargingCode(gw.getChargingCode());
		}   
		else if (response_code.equalsIgnoreCase(Constants.STATUS_401)){
			billingResponse = new BillingResponseVO();
			billingResponse.setMsisdn(billingRequest.getMsisdn());
			billingResponse.setBillingRequestId(billingRequest.getId().toString());							
			billingResponse.setResponseMsg(Constants.BILLING_INSUFFICIENT_CREDIT);
			billingResponse.setGatewayResponseCode(response_code+","+billingRequest.getPolicy());
			billingResponse.setResponseString(response_code+","+billingRequest.getPolicy());
			billingResponse.setStatus(Constants.STATUS_CODE_FAILURE);
			billingResponse.setResponseCode(ErrorCode.INSUFFICIENT_BALANCE);	
			billingResponse.setChargingCode(gw.getChargingCode());
					
		}  
		else if (response_code.equalsIgnoreCase(Constants.STATUS_CODE_PENDINGMT)){
			logger.info(billingRequest.getMsisdn()+" channel WAP : sending pending MT response");
			billingResponse = new BillingResponseVO();
			billingResponse.setMsisdn(billingRequest.getMsisdn());
			billingResponse.setBillingRequestId(billingRequest.getId().toString());							
			billingResponse.setResponseMsg(Constants.BILLING_SUCCESS);
			billingResponse.setGatewayResponseCode(response_code+","+billingRequest.getPolicy());
			billingResponse.setResponseString(response_code+","+billingRequest.getPolicy());
			billingResponse.setStatus(Constants.STATUS_CODE_PENDINGMT);
			billingResponse.setResponseCode(ErrorCode.GATEWAY_STATUS_SUCCESS);	
			billingResponse.setChargingCode(gw.getChargingCode());
		} 
		else {
			logger.info(billingRequest.getMsisdn()+"success status not true");
			billingResponse = new BillingResponseVO();
			billingResponse.setMsisdn(billingRequest.getMsisdn());
			billingResponse.setBillingRequestId(billingRequest.getId().toString());							
			billingResponse.setResponseMsg(Constants.BILLING_FAILURE);
			billingResponse.setGatewayResponseCode(response_code+","+billingRequest.getPolicy());
			billingResponse.setResponseString(response_code+","+billingRequest.getPolicy());
			billingResponse.setStatus(Constants.STATUS_CODE_UNKNOWN);
			billingResponse.setResponseCode(ErrorCode.GATEWAY_STATUS_NOT_OK);
			billingResponse.setChargingCode(gw.getChargingCode());
		}
		return billingResponse;
	
	}

	@Override
	public BillingResponseVO getBalance(BillingRequestVO billingRequest,
			GatewayConfigurationVO gw) throws ValidationException {
		BillingResponseVO billingResponse = new BillingResponseVO();
		billingRequest.getPricePoint();
		logger.info(billingRequest.getMsisdn()+"billingRequest.getPricePoint() "+billingRequest.getPricePoint());		
		billingResponse.setSubscriberType(SubscriberType.POSTPAID.code());
		
		int i1=0;
		Set<Double> sortedPricePoint = billingRequest.getSortedPricePnt();
		String pricePoint = null;
		String pricepointHighest =null;
		if(billingRequest.getBillingType().equalsIgnoreCase("straight"))
		{
			pricePoint = ""+sortedPricePoint.iterator().next();
			pricepointHighest = ""+pricePoint;
			logger.info(billingRequest.getMsisdn()+" billing type  straight ="+pricepointHighest);	
		}
		else if(billingRequest.getBillingType().equalsIgnoreCase("toggling")){
			for(Double price : sortedPricePoint)
			{
				pricePoint = ""+price;
				if(i1==0)
				pricepointHighest = ""+price;
				logger.info(billingRequest.getMsisdn()+" billing type  toggling ="+pricepointHighest);	
				i1++;
			}
		}
		
		
		billingResponse.setAmount(pricepointHighest);		
		billingResponse.setStatus(Constants.STATUS_CODE_SUCCESS);
		billingResponse.setResponseCode(ErrorCode.GATEWAY_STATUS_SUCCESS);
		billingResponse.setResponseMsg(Constants.BILLING_SUCCESS);
		billingResponse.setGatewayResponseCode(Constants.POSTPAID_RESPONSE_CODE);
		logger.info(billingRequest.getMsisdn()+"inside getBalance");
		return billingResponse;
	}

	@Override
	public double getBalanceinDouble(BillingRequestVO billingRequest,
			GatewayConfigurationVO gw) throws ValidationException {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	public  HashMap<String, String> getMap(String params){
		  HashMap<String, String> hm = new HashMap<String, String>();
		  String parameterKeyValuePair = params;
		  String [] parameters = parameterKeyValuePair.trim().split(",");
		  logger.info("param values :: " + parameterKeyValuePair);
		  for (int i = 0; i < parameters.length; i++) {
		   String [] keyValue = parameters[i].split("=");
		   if (keyValue != null && keyValue.length == 2){
		    hm.put(keyValue[0].trim(), keyValue[1].trim());
		   }
		  }
		  return hm;
		 }

	@Override
	public BillingResponseVO doBalanceCheck(BillingRequestVO billingRequest, GatewayConfigurationVO gw)
			throws ValidationException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	

}
