package net.one97.billing.halotal.commons;

import java.util.ArrayList;
import java.util.Arrays;

public class Constants {
	
	public static final String FFE_RESPONSE_CODE_SUCCESS = "fulfillproduct-1003-0000-S";
	public static final String TIGO_PRICE_PARAM = "Price";
	public static final String TIGO_PESA_PIN_PARAM = "PIN";
	public static final String PESA_BILLING_FAILURE = "Pesa Billing Failure";
	public static final String FFE_BILLING_FAILURE = "FFE Billing Failure";
	/* Billing Response */
	public static String BILLING_SUCCESS = "Successful billing";
	public static String BILLING_FAILURE = "Billing Failure";
	public static String BILLING_CONNECTION_TIMEOUT = "connection time out";
	
	/* Status Code */
	public static String STATUS_CODE_SUCCESS = "0";	
	public static String STATUS_CODE_FAILURE = "1";
	public static String STATUS_CODE_UNKNOWN = "2";
	public static String STATUS_CODE_SYSTEM_ERROR = "3";
	public static String STATUS_CODE_PENDINGMT = "6";
	public static String STATUS_CODE_VALIDATION_ERROR = "4";
	public static String MAIN_ACCOUNT_TYPE_CODE = "2000";
	public static String LOAN_ACCOUNT_TYPE_CODE = "2002";
	public static String GATEWAY_RESPONSE_CODE_FAILURE = "-1";
	public static String FFE_NO_PIN = "No Pin passed"; 
	
	/* Subscriber Type */
	public static String USER_PREPAID = "0";	
	public static String USER_POSTPAID = "1";
	public static String USER_UNKNOWN = "2";
	public static String RESPONSE_CODE_SUCCESS="405000000";
	public static String BILLING_INSUFFICIENT_CREDIT = "insufficient fund";
	public static String BILLING_INSUFFICIENT_LOAN_CREDIT = "insufficient loan balance";
	public static String LOAN_NOT_GRANTED = "loan not granted";
	public static String INVALID_USER="unknown user";
	
	public static String POSTPAID_TABLE_NAME = "sm_charginglog_cdr_";
	
	
	public static String RESPONSE_HEADER_SUCCESS_CODE="405000000";
	public static String RESPONSE_HEADER_UNKNOWN_STRING="unknown number";
	public static String POSTPAID_RESPONSE_CODE="0";
	public static String POSTPAID_MSISDN_TABLE="sm_tigo_postpaid_numbers";
	public static String DATA_TABLE = "de_data_";
	public static String SM_SERVICE_NAME = "subscription message delivery";
	public static final ArrayList<String> TIGO_FFE_LOW_BALANCE_CODES = new ArrayList<String>(
			Arrays.asList("00007", "00045", "405914012", "405914560",
					"405914547", "402023021", "60019"));
	public static final ArrayList<String> PESA_INVALID_PINS = new ArrayList<String>(
			Arrays.asList("00013","00017","00026","00270"));
	public static final String PESA_INVALID_PIN = null;
	
	public static String STATUS_SUCCESS = "0";	
	public static String STATUS_414 = "414";	
	public static String STATUS_401 = "401";	
	public static String STATUS_403 = "403";	
	public static String STATUS_ERROR = "error";	
	
	public static String  URL ="url";
		
	public static String  PRODUCT_NAME ="productName";
	public static String  LARGE_ACCOUNT ="largeAccount";
	
	
	public static String SUBKEYWORD  ="subKeyword";
	public static String  TEXT ="text";
	public static String MESSAGE  ="message";
	
	
	public static String  PARTNER_ID ="partnerId";
	public static String  PARTNER_SERVICE_ID ="partnerServiceId";
	public static String  PARTNER_ROLE ="partnerRole";
	
	public static String  TRACKING_ID ="trackingId";
	public static String  CLIENT_IP ="clientIp";
	public static String  CAMPAIGN_URL ="campaignUrl";
		
	public static String PARTNER_ROLE_ID  ="partnerRoleId";
	public static String PASSWORD  ="password";
	public static String OP_ID  ="opId";
	
	public static String  PRODUCT_ID ="productId";
	public static String  PRICE_POINT_ID ="pricepointId";
	

	public static String  SUB ="SUB";
	public static String  CATE ="CATE";
	public static String  ITEM ="ITEM";
	public static String  SUB_CP ="SUB_CP";
	public static String  CONT ="CONT";
	public static String  TYPE ="TYPE";
	public static String  PRO ="PRO";
	public static String  SER ="SER";
	public static String  CMD ="CMD";
	public static String  SOURCE ="SOURCE";
	public static String  IMEI ="IMEI";
	
	
}

