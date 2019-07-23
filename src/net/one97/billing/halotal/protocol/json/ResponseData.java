package net.one97.billing.halotal.protocol.json;

public class ResponseData {

	private String subscriptionResult;

    private String transactionId;

  //  private String externalTxId;

    private String subscriptionError;

    public String getSubscriptionResult ()
    {
        return subscriptionResult;
    }

    public void setSubscriptionResult (String subscriptionResult)
    {
        this.subscriptionResult = subscriptionResult;
    }

    public String getTransactionId ()
    {
        return transactionId;
    }

    public void setTransactionId (String transactionId)
    {
        this.transactionId = transactionId;
    }

    public String getSubscriptionError ()
    {
        return subscriptionError;
    }

    public void setSubscriptionError (String subscriptionError)
    {
        this.subscriptionError = subscriptionError;
    }

	@Override
	public String toString() {
		return "ResponseData [subscriptionResult=" + subscriptionResult + ", transactionId=" + transactionId
				+ ", subscriptionError=" + subscriptionError + "]";
	}

   
}
