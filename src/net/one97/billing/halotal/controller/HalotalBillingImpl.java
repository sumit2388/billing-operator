package net.one97.billing.halotal.controller;

import java.util.Date;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

import net.one97.billing.core.cache.BillingCache;
import net.one97.billing.core.controller.impl.BillingImpl;
import net.one97.billing.core.exception.GateWayConfigException;
import net.one97.billing.core.exception.ValidationException;
import net.one97.billing.core.factory.BeanFactory;
import net.one97.billing.core.util.BillingUtil.BillingResponse;
import net.one97.billing.core.util.BillingUtil.SubscriberType;
import net.one97.billing.core.util.ErrorCode;
import net.one97.billing.core.valueObject.BillingRequestVO;
import net.one97.billing.core.valueObject.BillingResponseVO;
import net.one97.billing.core.valueObject.GatewayConfigurationVO;
import net.one97.billing.core.valueObject.ResultBeanVO;
import net.one97.billing.core.valueObject.UnsubRequestVO;
import net.one97.billing.halotal.commons.Constants;
import net.one97.billing.halotal.dao.HalotalDAO;

public class HalotalBillingImpl extends BillingImpl {
	
	private static final Logger logger = LoggerFactory.getLogger(HalotalBillingImpl.class);

  private HalotalDAO  halotalDao ;
  
  

 // OoredooQatarDao
	public void setHalotalDao(HalotalDAO halotalDao) {
	this.halotalDao = halotalDao;
}

	@Override
	protected String parseResponseString(String respString) {
		throw new UnsupportedOperationException("parseResponseString Not Required");
		
	}

	@Override
	public ResultBeanVO doDebit(BillingRequestVO chargingReq) throws ValidationException, GateWayConfigException {
		ResultBeanVO returnObject = new ResultBeanVO();
		logger.info("inside doDebit method :");
		validateParams(chargingReq);

		String billingType = chargingReq.getBillingType();
		Set<Double> pricePoint=chargingReq.getSortedPricePnt();
		Double price = pricePoint.iterator().next();
		logger.info("price :"+ price);
		chargingReq.setTxnAmount(price);
		logger.info("BillingRequest" + chargingReq.getId() + " Billing type" + billingType);
		
		GatewayConfigurationVO gw = BillingCache.getGatewayConfig(chargingReq, BillingCache.BILLING);
		logger.info("gw :"+ gw.toString());
		if (billingType.equalsIgnoreCase("toggling"))
		{			
			if(gw.getProtocol().equalsIgnoreCase("HTTP") ){
				logger.info("toggling HTTP :");
				returnObject = doTogglingBilling(chargingReq, false);
				}
			else{
				logger.info("wrong protocol");
				}
		}
		else if (billingType.equalsIgnoreCase("straight"))
		{
			 if(gw.getProtocol().equalsIgnoreCase("HTTP") ){
				logger.info("straight HTTP :");
				returnObject = doStraightBilling(chargingReq, false);}
			else{
				logger.info("wrong protocol");
				}
		}
		

		return returnObject;
	}

	
	@Override
	public void validateParams(BillingRequestVO billingRequest) throws ValidationException {
		/* Nothing To Do */		
		
	}

	
	protected ResultBeanVO doTogglingBilling(BillingRequestVO billingRequest, boolean getBalanceSupported)
	throws ValidationException
	{
		BillingResponseVO bResponse = null;
		BillingResponseVO balanceResponse = null;
		boolean debitsucc =false;
		ResultBeanVO returnObject = new ResultBeanVO();
		if(getBalanceSupported)
		{
		try
		{
			balanceResponse = getCheckBalance(billingRequest);	
			logger.info("Subscriber type in controller..."+balanceResponse.getSubscriberType());
			if(balanceResponse.getSubscriberType().equalsIgnoreCase(SubscriberType.PREPAID.code()))
			{
				balanceResponse.setSubscriberType(SubscriberType.PREPAID.code());
			}else if(balanceResponse.getSubscriberType().equalsIgnoreCase(SubscriberType.POSTPAID.code())){
				balanceResponse.setSubscriberType(SubscriberType.POSTPAID.code());
			}else
			{
				balanceResponse.setSubscriberType(SubscriberType.UNKNOWN.code());
			}
			logger.info(" log 1");
			if(balanceResponse.getResponseCode().equalsIgnoreCase(ErrorCode.GATEWAY_STATUS_SUCCESS)){
				bResponse = new BillingResponseVO();
				billingRequest.setTxnAmount(Double.parseDouble(balanceResponse.getAmount()));
				logger.info(" log 2");
				if(balanceResponse.getSubscriberType().equalsIgnoreCase(SubscriberType.POSTPAID.code())){
					//get Gateway configuration 
					GatewayConfigurationVO gw = BillingCache.getGatewayConfig(billingRequest, BillingCache.BILLING);
					 bResponse=balanceResponse;
					 bResponse.setSubscriberType(balanceResponse.getSubscriberType());
						bResponse.setOperator(billingRequest.getOperator());
						bResponse.setMsisdn(billingRequest.getMsisdn());
						bResponse.setProduct(billingRequest.getProduct());
					 bResponse.setChargingCode(gw.getChargingCode());
					 bResponse.setParams(gw.getParams());
					//insert into table
					 logger.info(" log 3");
					 halotalDao.insertPostpaidData(billingRequest,bResponse);
					 logger.info(" log 4");
					String params=bResponse.getParams();
					params=params.substring(0,params.indexOf(",serial="));
					bResponse.setParams(params);
					 logger.info(" log 5");
				}
				else{
					 logger.info(" log 6");
					if(balanceResponse.getGatewayResponseCode().equalsIgnoreCase(Constants.LOAN_ACCOUNT_TYPE_CODE) || balanceResponse.getGatewayResponseCode().equalsIgnoreCase(Constants.MAIN_ACCOUNT_TYPE_CODE))
						billingRequest.setPolicy(balanceResponse.getGatewayResponseCode());
				for (Double price : billingRequest.getSortedPricePnt())
				{ logger.info(" log 7");
					if(price<= Double.parseDouble(balanceResponse.getAvailableAmount()))
					{ logger.info(" log 8");
						billingRequest.setTxnAmount(price);
						debitsucc=true;
						break;
					}
					
				}	
				 logger.info(" log 9");
				if(debitsucc)
				{
					 logger.info(" log 10");
				bResponse = debitAccount(billingRequest);
				 logger.info(" log 11");
				/*
				responseList.add(bResponse);
				if (bResponse.getStatus() != null && bResponse.getStatus().equals("0"))
				{
					if (logger.isInfoEnabled())
					{
						logger.info(" msisdn --" + billingRequest.getMsisdn()
							+ "has been successfully charged for --" + bResponse.getAmount());
					}
					if (bResponse.getParams() != null)
						returnObject.setParams(bResponse.getParams());
					break;
					*/
				}
				else
				{   logger.info(" log 12");
						bResponse.setStatus(Constants.STATUS_CODE_FAILURE);
						bResponse.setResponseCode(ErrorCode.INSUFFICIENT_BALANCE);
						bResponse.setResponseMsg(Constants.BILLING_INSUFFICIENT_CREDIT);	
						bResponse.setResponseString(Constants.BILLING_INSUFFICIENT_CREDIT);	
						bResponse.setGatewayResponseCode(ErrorCode.GATEWAY_STATUS_NOT_OK);
				}
				
				}
				 logger.info(" log 13");
				bResponse.setSubscriberType(balanceResponse.getSubscriberType());
				bResponse.setOperator(billingRequest.getOperator());
				bResponse.setMsisdn(billingRequest.getMsisdn());
				bResponse.setProduct(billingRequest.getProduct());
			}
			else{
				 logger.info(" log 14");
				if(balanceResponse.getSubscriberType().equalsIgnoreCase(SubscriberType.UNKNOWN.code())){String msisdn=billingRequest.getMsisdn();
				if (msisdn != null && msisdn.length() == 12)
					msisdn = msisdn.substring(3, msisdn.length());
				// check if the msisdn is present in db
				 logger.info(" log 15");
				int res= halotalDao.checkPostpaidMsisdn(msisdn);
				 logger.info(" log 16");
				if (res==1){
					bResponse = new BillingResponseVO();
					 logger.info(" log 17");
					billingRequest.setTxnAmount(Double.parseDouble(balanceResponse.getAmount()));
					 logger.info(" log 18");
					//get Gateway configuration 
					GatewayConfigurationVO gw = BillingCache.getGatewayConfig(billingRequest, BillingCache.BILLING);
					 logger.info(" log 19");
					 bResponse=balanceResponse;
					 logger.info(" log 20");
					 bResponse.setSubscriberType(SubscriberType.POSTPAID.code());
					 balanceResponse.setStatus(Constants.STATUS_CODE_SUCCESS);
						balanceResponse.setResponseCode(ErrorCode.GATEWAY_STATUS_SUCCESS);
						balanceResponse.setResponseMsg(Constants.BILLING_SUCCESS);
						balanceResponse.setGatewayResponseCode(Constants.POSTPAID_RESPONSE_CODE);
						
						bResponse.setOperator(billingRequest.getOperator());
						bResponse.setMsisdn(billingRequest.getMsisdn());
						bResponse.setProduct(billingRequest.getProduct());
					 bResponse.setChargingCode(gw.getChargingCode());
					 logger.info(" log 21");
					//insert into table
					 halotalDao.insertPostpaidData(billingRequest,bResponse);
					 logger.info(" log 22");
					String params=bResponse.getParams();
					params=params.substring(0,params.indexOf(",serial="));
					bResponse.setParams(params);
					
					
				}
				else{
					 logger.info(" log 23");
					bResponse=balanceResponse;
					 logger.info(" log 24");
					bResponse.setOperator(billingRequest.getOperator());
					bResponse.setMsisdn(billingRequest.getMsisdn());
					bResponse.setProduct(billingRequest.getProduct());
				}
				}
				 logger.info(" log 26");
				bResponse=balanceResponse;
				 logger.info(" log 27");
				bResponse.setOperator(billingRequest.getOperator());
				bResponse.setMsisdn(billingRequest.getMsisdn());
				bResponse.setProduct(billingRequest.getProduct());
			}
			System.out.println("Subscriber type====="+bResponse.getSubscriberType());
				
			}		
		catch (UnsupportedOperationException e)
		{
			 logger.info(" log 28");
			bResponse = new BillingResponseVO();
			bResponse.setResponseString(BeanFactory.getProperty(ErrorCode.OPERATION_NOT_SUPPORTED));
			bResponse.setResponseMsg(BeanFactory.getProperty(ErrorCode.OPERATION_NOT_SUPPORTED));
			bResponse.setResponseCode(ErrorCode.OPERATION_NOT_SUPPORTED);
			bResponse.setResponseTime(new Date());
			logger.error("UnSupported  Balance Operation .Request can not be processed.");
			bResponse.setStatus(BillingResponse.SYSTEM_ERROR.code());
			bResponse.setOperator(billingRequest.getOperator());
			bResponse.setMsisdn(billingRequest.getMsisdn());
			bResponse.setProduct(billingRequest.getProduct());
		}
		catch (Exception e)
		{
			bResponse = new BillingResponseVO();
			bResponse.setResponseString(BeanFactory.getProperty(ErrorCode.UNKNOWN_ERROR));
			bResponse.setResponseMsg(BeanFactory.getProperty(ErrorCode.UNKNOWN_ERROR));
			bResponse.setResponseCode(ErrorCode.UNKNOWN_ERROR);
			bResponse.setResponseTime(new Date());
			bResponse.setStatus(BillingResponse.SYSTEM_ERROR.code());
			bResponse.setOperator(billingRequest.getOperator());
			bResponse.setMsisdn(billingRequest.getMsisdn());
			bResponse.setProduct(billingRequest.getProduct());
			logger.error("Unknown Exception occured for this request for Balance Operation .Request would not be processed.",e);
			try
			{
				billingService.saveBillingResponse(bResponse);
			}
			catch (DataAccessException ex)
			{
				// Unable to save Response in DB should not stop Response
				// from going back to Client
				logger.error("Billing Response could not be saved for Billing Request - " + billingRequest.getId(),
						ex);
				logger.info("Billing Response for Billing Request Id - " + billingRequest.getId() + "is --- "
						+ "Gateway Txnid =" + bResponse.getTxnId() + ",Status = " + bResponse.getStatus()
						+ ",Response String=" + bResponse.getResponseString() + ",ResponseMsg"
						+ bResponse.getResponseMsg());
			}
		}
	}else{
		try{
			
		bResponse = new BillingResponseVO();
		bResponse = debitAccount(billingRequest);
		bResponse.setSubscriberType("unknown");
		bResponse.setOperator(billingRequest.getOperator());
		bResponse.setMsisdn(billingRequest.getMsisdn());
		bResponse.setProduct(billingRequest.getProduct());
		}catch (Exception e)
		{
			bResponse = new BillingResponseVO();
			bResponse.setResponseString(BeanFactory.getProperty(ErrorCode.UNKNOWN_ERROR));
			bResponse.setResponseMsg(BeanFactory.getProperty(ErrorCode.UNKNOWN_ERROR));
			bResponse.setResponseCode(ErrorCode.UNKNOWN_ERROR);
			bResponse.setResponseTime(new Date());
			bResponse.setStatus(BillingResponse.SYSTEM_ERROR.code());
			bResponse.setOperator(billingRequest.getOperator());
			bResponse.setMsisdn(billingRequest.getMsisdn());
			bResponse.setProduct(billingRequest.getProduct());
			logger.error("Unknown Exception occured for this request for Balance Operation .Request would not be processed.",e);
			
		}
	}
	
			if (logger.isInfoEnabled())
			{
				if (bResponse != null)
					logger.info("Status of Response is " + bResponse.getStatus());
			}
			try
			{
				if (bResponse != null)
					billingService.saveBillingResponse(bResponse);
			}
			catch (DataAccessException ex)
			{
				// Unable to save Response in DB should not stop Response from going
				// back to Client
				logger.error("Billing Response could not be saved for Billing Request - " + billingRequest.getId());
				logger.info("Billing Response for Billing Request Id - " + billingRequest.getId() + "is --- "
								+ "Gateway Txnid =" + bResponse.getTxnId() + ",Status = " + bResponse.getStatus()
								+ ",Response String=" + bResponse.getResponseString() + ",ResponseMsg"
								+ bResponse.getResponseMsg());
				logger.info("Stack trace :" );
				ex.printStackTrace();
			}
			if(bResponse!=null)
			{
					returnObject.setRespMsg(bResponse.getResponseMsg());
					returnObject.setResponseAt(bResponse.getResponseTime());
					returnObject.setRespCode(bResponse.getResponseCode());
					if (bResponse.getAmount() != null)
					{
						returnObject.setAmount(bResponse.getAmount());	
					}
					//returnObject.setResponseList(responseList);
					returnObject.setRespStatus(bResponse.getStatus());
					System.out.println("Subscriber aType at final submittion==="+bResponse.getSubscriberType());
					returnObject.setSubscriberType(bResponse.getSubscriberType());
					returnObject.setChargingCode(bResponse.getChargingCode());
					logger.info("Gateway Response code="+bResponse.getGatewayResponseCode());
					returnObject.setGatewayRespone(bResponse.getGatewayResponseCode());
			}
			if(billingRequest.getParams()!=null && billingRequest.getParams().contains("param1@loan") && billingRequest.getBillingMode().equalsIgnoreCase("fresh") && bResponse.getResponseCode().equalsIgnoreCase(ErrorCode.GATEWAY_STATUS_SUCCESS) && BeanFactory.getProperty("sendMessage").equalsIgnoreCase("1"))
				halotalDao.insertTigoData(billingRequest, bResponse);
		return returnObject;
	}
	
	
	private BillingResponseVO getCheckBalance(BillingRequestVO billingRequest) throws ValidationException, GateWayConfigException{
 		logger.info("In check balance method.....");
		GatewayConfigurationVO gw = BillingCache.getGatewayConfig(billingRequest, BillingCache.BALANCE);
		GatewayConfigurationVO gw1 = BillingCache.getGatewayConfig(billingRequest, BillingCache.BILLING);
		// setting billing charging code in balance charging code..
		gw.setChargingCode(gw1.getChargingCode());
		logger.info("charging code ::"+gw1.getChargingCode() );
		BillingResponseVO bResponse= billingHandler.getBalance(billingRequest, gw);
 		return bResponse;
 	}	

	
	protected ResultBeanVO doStraightBilling(BillingRequestVO billingRequest, boolean getBalanceSupported)
	throws ValidationException {

		BillingResponseVO balanceResponse = null;
		BillingResponseVO bResponse = null;
		ResultBeanVO returnObject = new ResultBeanVO();
		logger.info("Request for Straight Billing received .....");
		
		if (getBalanceSupported)	
				{	
					try{
						balanceResponse = new BillingResponseVO();
						balanceResponse = getCheckBalance(billingRequest);	
						logger.info("Response code==="+balanceResponse.getResponseCode());
						
						if(balanceResponse.getSubscriberType().equalsIgnoreCase(SubscriberType.PREPAID.code()))
						{
							balanceResponse.setSubscriberType(SubscriberType.PREPAID.code());
						}else if(balanceResponse.getSubscriberType().equalsIgnoreCase(SubscriberType.POSTPAID.code())){
							balanceResponse.setSubscriberType(SubscriberType.POSTPAID.code());
						}else
						{
							balanceResponse.setSubscriberType(SubscriberType.UNKNOWN.code());
						}
						if(balanceResponse.getResponseCode().equalsIgnoreCase(ErrorCode.GATEWAY_STATUS_SUCCESS)){
							bResponse = new BillingResponseVO();
							logger.info("operator:"+bResponse.getOperator()+", msisdn:"+ bResponse.getMsisdn()+", product:"+bResponse.getProduct()+"Transcation amount:"+balanceResponse.getAmount());
							//logger.info("Transcation amount to be set======"+balanceResponse.getAmount());
							billingRequest.setTxnAmount(Double.parseDouble(balanceResponse.getAmount()));
							if(balanceResponse.getSubscriberType().equalsIgnoreCase(SubscriberType.POSTPAID.code())){
								//get Gateway configuration 
								GatewayConfigurationVO gw = BillingCache.getGatewayConfig(billingRequest, BillingCache.BILLING);
								 bResponse=balanceResponse;
								 bResponse.setSubscriberType(balanceResponse.getSubscriberType());
									bResponse.setOperator(billingRequest.getOperator());
									bResponse.setMsisdn(billingRequest.getMsisdn());
									bResponse.setProduct(billingRequest.getProduct());
								 bResponse.setChargingCode(gw.getChargingCode());
								//insert into table
								 halotalDao.insertPostpaidData(billingRequest,bResponse);
								String params=bResponse.getParams();
								params=params.substring(0,params.indexOf(",serial="));
								bResponse.setParams(params);
							}
							else{
								if(balanceResponse.getGatewayResponseCode().equalsIgnoreCase(Constants.LOAN_ACCOUNT_TYPE_CODE) || balanceResponse.getGatewayResponseCode().equalsIgnoreCase(Constants.MAIN_ACCOUNT_TYPE_CODE))
									billingRequest.setPolicy(balanceResponse.getGatewayResponseCode());
								bResponse = debitAccount(billingRequest);
							}
							bResponse.setSubscriberType(balanceResponse.getSubscriberType());
							bResponse.setOperator(billingRequest.getOperator());
							bResponse.setMsisdn(billingRequest.getMsisdn());
							bResponse.setProduct(billingRequest.getProduct());
							
							
						}
						else{
							billingRequest.setTxnAmount(Double.parseDouble(balanceResponse.getAmount()));
							if(balanceResponse.getSubscriberType().equalsIgnoreCase(SubscriberType.UNKNOWN.code())){
								String msisdn=billingRequest.getMsisdn();
								if (msisdn != null && msisdn.length() == 12)
									msisdn = msisdn.substring(3, msisdn.length());
								// check if the msisdn is present in db
								int res= halotalDao.checkPostpaidMsisdn(msisdn);
								if (res==1){
									bResponse = new BillingResponseVO();
									billingRequest.setTxnAmount(Double.parseDouble(balanceResponse.getAmount()));
									//get Gateway configuration 
									GatewayConfigurationVO gw = BillingCache.getGatewayConfig(billingRequest, BillingCache.BILLING);
									 bResponse=balanceResponse;
									 bResponse.setSubscriberType(SubscriberType.POSTPAID.code());
									 balanceResponse.setStatus(Constants.STATUS_CODE_SUCCESS);
										balanceResponse.setResponseCode(ErrorCode.GATEWAY_STATUS_SUCCESS);
										balanceResponse.setResponseMsg(Constants.BILLING_SUCCESS);
										balanceResponse.setGatewayResponseCode(Constants.POSTPAID_RESPONSE_CODE);
										
										bResponse.setOperator(billingRequest.getOperator());
										bResponse.setMsisdn(billingRequest.getMsisdn());
										bResponse.setProduct(billingRequest.getProduct());
									 bResponse.setChargingCode(gw.getChargingCode());
									 
									//insert into table
									 halotalDao.insertPostpaidData(billingRequest,bResponse);
									String params=bResponse.getParams();
									params=params.substring(0,params.indexOf(",serial="));
									bResponse.setParams(params);
									
									
								}
								else{
									
									bResponse=balanceResponse;
									bResponse.setOperator(billingRequest.getOperator());
									bResponse.setMsisdn(billingRequest.getMsisdn());
									bResponse.setProduct(billingRequest.getProduct());
									bResponse.setSubscriberType(balanceResponse.getSubscriberType());
								}
							}
							else{
							bResponse=balanceResponse;
							bResponse.setOperator(billingRequest.getOperator());
							bResponse.setMsisdn(billingRequest.getMsisdn());
							bResponse.setProduct(billingRequest.getProduct());
							bResponse.setSubscriberType(balanceResponse.getSubscriberType());
						}
						}
					}
				catch (UnsupportedOperationException e)
				{
					bResponse = new BillingResponseVO();
					bResponse.setResponseString(BeanFactory.getProperty(ErrorCode.OPERATION_NOT_SUPPORTED));
					bResponse.setResponseMsg(BeanFactory.getProperty(ErrorCode.OPERATION_NOT_SUPPORTED));
					bResponse.setResponseCode(ErrorCode.OPERATION_NOT_SUPPORTED);
					bResponse.setResponseTime(new Date());
					logger.error("UnSupported  Balance Operation .Request can not be processed.");
					bResponse.setStatus(BillingResponse.SYSTEM_ERROR.code());
					bResponse.setOperator(billingRequest.getOperator());
					bResponse.setMsisdn(billingRequest.getMsisdn());
					bResponse.setProduct(billingRequest.getProduct());
				}
					catch (Exception e)
					{
						bResponse = new BillingResponseVO();
						bResponse.setResponseString(BeanFactory.getProperty(ErrorCode.UNKNOWN_ERROR));
						bResponse.setResponseMsg(BeanFactory.getProperty(ErrorCode.UNKNOWN_ERROR));
						bResponse.setResponseCode(ErrorCode.UNKNOWN_ERROR);
						bResponse.setResponseTime(new Date());
						bResponse.setStatus(BillingResponse.SYSTEM_ERROR.code());
						bResponse.setOperator(billingRequest.getOperator());
						bResponse.setMsisdn(billingRequest.getMsisdn());
						bResponse.setProduct(billingRequest.getProduct());
						logger.error("Unknown Exception occured for this request for Balance Operation .Request would not be processed.",e);
						
					}
				}else{
			logger.info("FFE protocol Debit Request.");
			try{
				
			bResponse = new BillingResponseVO();
			bResponse = debitAccount(billingRequest);
			bResponse.setSubscriberType("unknown");
			bResponse.setOperator(billingRequest.getOperator());
			bResponse.setMsisdn(billingRequest.getMsisdn());
			bResponse.setProduct(billingRequest.getProduct());
			}catch (Exception e)
			{
				bResponse = new BillingResponseVO();
				bResponse.setResponseString(BeanFactory.getProperty(ErrorCode.UNKNOWN_ERROR));
				bResponse.setResponseMsg(BeanFactory.getProperty(ErrorCode.UNKNOWN_ERROR));
				bResponse.setResponseCode(ErrorCode.UNKNOWN_ERROR);
				bResponse.setResponseTime(new Date());
				bResponse.setStatus(BillingResponse.SYSTEM_ERROR.code());
				bResponse.setOperator(billingRequest.getOperator());
				bResponse.setMsisdn(billingRequest.getMsisdn());
				bResponse.setProduct(billingRequest.getProduct());
				logger.error("Unknown Exception occured for this request for Balance Operation .Request would not be processed.",e);
				
			}
		}
		if (logger.isInfoEnabled())
		{
			if (bResponse != null)
				logger.info("Status of Response is " + bResponse.getStatus());
		}
		/*
		 * Response Message and res time of Last transaction will be sent to
		 * Client in case of Short Response
		 */

		// save response
		try
		{
						 
			if (bResponse != null){
				billingService.saveBillingResponse(bResponse);
			}	
		}
		catch (DataAccessException ex)
		{
			// Unable to save Response in DB should not stop Response from going
			// back to Client
			logger.error("Billing Response could not be saved for Billing Request - " + billingRequest.getId());
			logger.info("Billing Response for Billing Request Id - " + billingRequest.getId() + "is --- "
							+ "Gateway Txnid =" + bResponse.getTxnId() + ",Status = " + bResponse.getStatus()
							+ ",Response String=" + bResponse.getResponseString() + ",ResponseMsg"
							+ bResponse.getResponseMsg());
			logger.info("Stack trace :" );
			ex.printStackTrace();
		}
				if(bResponse!=null)
		{
				returnObject.setRespMsg(bResponse.getResponseMsg());
				returnObject.setResponseAt(bResponse.getResponseTime());
				returnObject.setRespCode(bResponse.getResponseCode());
				if (bResponse.getAmount() != null)
					returnObject.setAmount(bResponse.getAmount());
				//returnObject.setResponseList(responseList);
				returnObject.setRespStatus(bResponse.getStatus());
				returnObject.setSubscriberType(bResponse.getSubscriberType());
				returnObject.setChargingCode(bResponse.getChargingCode());	
				logger.info("Gateway Response code="+bResponse.getGatewayResponseCode());
				returnObject.setGatewayRespone(bResponse.getGatewayResponseCode());
		}
		if (billingRequest.getParams() != null
				&& billingRequest.getParams().contains("param1@loan")
				&& billingRequest.getBillingMode().equalsIgnoreCase("fresh")
				&& bResponse.getResponseCode().equalsIgnoreCase(
						ErrorCode.GATEWAY_STATUS_SUCCESS)
				&& BeanFactory.getProperty("sendMessage").equalsIgnoreCase("1"))
			halotalDao.insertTigoData(billingRequest, bResponse);
		/*
		 * Response Message and res time of Last transaction will be sent to
		 * Client in case of Short Response
		 */
		return returnObject;
	
	}


	@Override
	public String doUnsub(UnsubRequestVO unsubRequest) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public BillingResponseVO createResponse(String txnId,String Status , String amt, String responseCode , String responseMsg , String responseString ,String gatewayResponseCode ,String params , String subscriberType ){
		BillingResponseVO response = new BillingResponseVO();
		Date responseTime = new Date();
		response.setResponseTime(responseTime);
		if(amt!=null && !amt.equals(""))response.setAmount(amt);
		response.setResponseString(responseString);
		response.setResponseMsg(responseMsg);
		response.setResponseCode(responseCode);
		response.setStatus(Status);
		if(txnId!=null)
		response.setTxnId(txnId);
		response.setGatewayResponseCode(gatewayResponseCode);
		if(params!=null && !params.equals(""))response.setParams(params);
		if(subscriberType!=null && !subscriberType.equals(""))
		response.setSubscriberType(subscriberType);
		return response;
		}


	@Override
	public ResultBeanVO doBalanceCheck(BillingRequestVO balanceRequest) throws ValidationException {
		// TODO Auto-generated method stub
		return null;
	}



}
