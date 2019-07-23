package net.one97.billing.halotal.dao;

import net.one97.billing.core.valueObject.BillingRequestVO;
import net.one97.billing.core.valueObject.BillingResponseVO;

public interface HalotalDAO {
	
	public void insertPostpaidData(BillingRequestVO billingRequest,BillingResponseVO billingResponse);
	public int checkPostpaidMsisdn(String msisdn);
	public int insertTigoData (BillingRequestVO billingRequestVO, BillingResponseVO billingResponseVO);

}
