package net.one97.billing.halotal.protocol.json;

public class RequestBean {
	
	private String productId;
	private int pricepointId;
	private String mcc;
	private String mnc;
	private String text;
	private String msisdn;
	private String largeAccount;
	private String sendDate;
	private String priority;
	private String timezone;
	private String context;
	private String moTransactionUUID;
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public int getPricepointId() {
		return pricepointId;
	}
	public void setPricepointId(int pricepointId) {
		this.pricepointId = pricepointId;
	}
	public String getMcc() {
		return mcc;
	}
	public void setMcc(String mcc) {
		this.mcc = mcc;
	}
	public String getMnc() {
		return mnc;
	}
	public void setMnc(String mnc) {
		this.mnc = mnc;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getLargeAccount() {
		return largeAccount;
	}
	public void setLargeAccount(String largeAccount) {
		this.largeAccount = largeAccount;
	}
	public String getSendDate() {
		return sendDate;
	}
	public void setSendDate(String sendDate) {
		this.sendDate = sendDate;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getMoTransactionUUID() {
		return moTransactionUUID;
	}
	public void setMoTransactionUUID(String moTransactionUUID) {
		this.moTransactionUUID = moTransactionUUID;
	}
	
	
	

	
	

}
