package net.one97.billing.halotal.protocol.json;

public class Response {

	private String message;

    private String inError;

    private String requestId;

    private String code;
    
    private ResponseData responseData;

    public ResponseData getResponseData ()
    {
        return responseData;
    }

    public void setResponseData (ResponseData responseData)
    {
        this.responseData = responseData;
    }

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getInError ()
    {
        return inError;
    }

    public void setInError (String inError)
    {
        this.inError = inError;
    }

    public String getRequestId ()
    {
        return requestId;
    }

    public void setRequestId (String requestId)
    {
        this.requestId = requestId;
    }

    public String getCode ()
    {
        return code;
    }

    public void setCode (String code)
    {
        this.code = code;
    }

    @Override
    public String toString()
    {
        return "Response [responseData = "+responseData+", message = "+message+", inError = "+inError+", requestId = "+requestId+", code = "+code+"]";
    }
}
