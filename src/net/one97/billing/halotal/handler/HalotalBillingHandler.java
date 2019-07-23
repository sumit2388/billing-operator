package net.one97.billing.halotal.handler;



import net.one97.billing.core.exception.ValidationException;
import net.one97.billing.core.handler.BillingHandler;
import net.one97.billing.core.protocol.BillingProtocol;
import net.one97.billing.core.valueObject.BillingRequestVO;
import net.one97.billing.core.valueObject.BillingResponseVO;
import net.one97.billing.core.valueObject.GatewayConfigurationVO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HalotalBillingHandler implements BillingHandler {
	
	//public static Logger logger = Logger.getLogger(RelianceBillingHandler.class);
	private static final Logger logger = LoggerFactory.getLogger(HalotalBillingHandler.class);
	
	private BillingProtocol halotalJsonHandler;
	
	
	
	public void setHalotalJsonHandler(BillingProtocol halotalJsonHandler) {
		this.halotalJsonHandler = halotalJsonHandler;
	}





	@Override
	public BillingResponseVO debitAccount(BillingRequestVO billingRequest, GatewayConfigurationVO gw) throws ValidationException {
		BillingResponseVO resp = null;
		if ( gw != null)
			logger.info("BillingRequest ID--" + billingRequest.getId() + " to be billed through protocol--"+ gw.getProtocol());

		
		 if (gw != null && gw.getProtocol().equalsIgnoreCase("HTTP"))
		{
			resp = halotalJsonHandler.debitAccount(billingRequest, gw);
		}
		else
		{
			logger.error("Unsupported protocol " + gw.getProtocol());
		}
		return resp;
	}


	
	
	
	@Override
	public BillingResponseVO getBalance(BillingRequestVO billingRequest, GatewayConfigurationVO gw) throws ValidationException {
		BillingResponseVO resp = null;
		if (logger.isInfoEnabled() && gw != null)
			logger.info("BillingRequest ID--" + billingRequest.getId() + " to be billed through protocol--"
					+ gw.getProtocol());
		 if (gw != null && gw.getProtocol().equalsIgnoreCase("HTTP")){
			logger.info(" getBalance method");
			resp = halotalJsonHandler.getBalance(billingRequest, gw);
			logger.info("resp " +resp);
		}
		else{
			logger.error("Unsupported protocol " + gw.getProtocol());
		}		return resp;
	}

	@Override
	public double getBalanceinDouble(BillingRequestVO billingRequest, GatewayConfigurationVO gw) throws ValidationException {
		if (logger.isInfoEnabled() && gw != null)
			logger.info("BillingRequest ID--" + billingRequest.getId() + " to be billed through protocol--"
					+ gw.getProtocol());
		Double bal = null;
		
		return bal;

	}
	@Override
	public BillingResponseVO doBalanceCheck(BillingRequestVO billingRequest, GatewayConfigurationVO gw)
			throws ValidationException {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
