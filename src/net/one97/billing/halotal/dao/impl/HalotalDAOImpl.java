package net.one97.billing.halotal.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import net.one97.billing.core.factory.BeanFactory;
import net.one97.billing.core.valueObject.BillingRequestVO;
import net.one97.billing.core.valueObject.BillingResponseVO;
import net.one97.billing.halotal.commons.Constants;
import net.one97.billing.halotal.dao.HalotalDAO;

public class HalotalDAOImpl implements HalotalDAO{
	private JdbcTemplate jdbcTemplateSelect;
	private JdbcTemplate jdbcTemplateUpdate;
	public void setJdbcTemplateUpdate(JdbcTemplate jdbcTemplateUpdate) {
		this.jdbcTemplateUpdate = jdbcTemplateUpdate;
	}
	
	public void setJdbcTemplateSelect(JdbcTemplate jdbcTemplateSelect) {
		this.jdbcTemplateSelect = jdbcTemplateSelect;
	}
	private static final Logger LOGGER = LoggerFactory.getLogger(HalotalDAOImpl.class);
	
	public void insertPostpaidData(BillingRequestVO billingRequest,BillingResponseVO billingResponse){
		String dateStr=null;
		try {
			LOGGER.info("Inserting postpaid data into db");
			Date date = Calendar.getInstance().getTime();
			SimpleDateFormat sdf =  new SimpleDateFormat("yyyyMMdd");
			dateStr=sdf.format(date);
			String serial = billingResponse.getParams();
			LOGGER.info("serial "+serial);
			serial=serial.substring(serial.indexOf(",serial="));
			serial=serial.substring(8);
			String  insertQuery="insert into "+Constants.POSTPAID_TABLE_NAME+ dateStr+"(msisdn,serial_no,circle,pack_name,price_point,charging_code) values(?,?,?,?,?,?)";
			int rowset = jdbcTemplateUpdate.update(insertQuery,new Object[] { billingResponse.getMsisdn(),serial,billingRequest.getCircle(),billingResponse.getProduct(),billingRequest.getTxnAmount(),billingResponse.getChargingCode()});
            LOGGER.info("inserted "+rowset +" rows in "+ Constants.POSTPAID_TABLE_NAME+ dateStr);
		}catch (CannotAcquireLockException e) {
			LOGGER.info("Unable to acquire the lock :["+Constants.POSTPAID_TABLE_NAME+ dateStr+"]  Due to ["+e.getMessage()+"]",e);
			e.printStackTrace();
		}catch (DataAccessException e) {
			LOGGER.info("Data Access Exception on  :["+Constants.POSTPAID_TABLE_NAME+ dateStr+"] Due to ["+e.getMessage()+"]",e);
			e.printStackTrace();
		}
	}
	
	public int checkPostpaidMsisdn(String msisdn){
		int response=0;
		String selectQuery="select 1 as res from "+Constants.POSTPAID_MSISDN_TABLE+" where msisdn="+ msisdn;
		try{
			SqlRowSet rowset =jdbcTemplateSelect.queryForRowSet(selectQuery);
		while(rowset.next())
		{
			response=rowset.getInt("res");
		}
		}
		catch (CannotAcquireLockException e) {
			LOGGER.info("Unable to acquire the lock :["+Constants.POSTPAID_MSISDN_TABLE+"]  Due to ["+e.getMessage()+"]",e);
			e.printStackTrace();
		}catch (DataAccessException e) {
			LOGGER.info("Data Access Exception on  :["+Constants.POSTPAID_MSISDN_TABLE+"] Due to ["+e.getMessage()+"]",e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public int insertTigoData(BillingRequestVO billingRequestVO,
			BillingResponseVO billingResponseVO) {

		String dateStr = null;
		LOGGER.info("Inserting Tigo data into db");
//		Date date = Calendar.getInstance().getTime();
		int rowset=0;
		try {
			Date request_dateTime = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(request_dateTime);
			cal.add(Calendar.DATE, 1);
			Date valid_till = cal.getTime();
			String channel = billingRequestVO.getChannel();
			String content = BeanFactory.getProperty("loanContent");		
			String  insertQuery="insert into "+Constants.DATA_TABLE+ billingRequestVO.getOperator().toLowerCase()+"(msisdn,content,operator,circle,pack_id,pack_name,service_id,service_name,cms_content_id,zone,short_code,suffix,prefix,queue_name,status,channel,app_source,priority,request_dateTime,valid_till,schedule_time,engine_key,service_type,content_type,msg_delivery_act,param1,param2,param3) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			rowset = jdbcTemplateUpdate.update(insertQuery,new Object[] {billingRequestVO.getMsisdn(),content,billingRequestVO.getOperator(),billingRequestVO.getCircle(),0,billingRequestVO.getProduct(),null,Constants.SM_SERVICE_NAME,null,null,"",null,null,null,1,channel,"SMInterface",1,request_dateTime,valid_till,null,null,3,"Text",1,null,null,null});
		    LOGGER.info("inserted "+rowset +" rows in "+ Constants.POSTPAID_TABLE_NAME+ dateStr);
		} catch (DataAccessException e) {
			e.printStackTrace();
			LOGGER.info("unable to add data into de_data_ table");
		}
        LOGGER.info("inserted "+rowset +" rows in "+ Constants.POSTPAID_TABLE_NAME+ dateStr);
			return rowset;
	}
}
