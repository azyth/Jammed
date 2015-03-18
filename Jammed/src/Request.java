

	//John and megan

/*
 * Request
 * 
 * 
 * requests is a superclass object that stores info for requests across the network
 * 
 * all requests are sent as either a "request" if it is the initiating message
 * "responses" are direct responses to a "request" message
 * 
 * some of this may seem irrelevant but should be useful later. 
 */
public abstract class Request {
	
	public enum MessageType{request,response}
	public enum ErrorMessage{badArgument,validationFailed,somethingBad,none}//etc
	
	private MessageType type;
	private boolean success = false;
	private ErrorMessage error=ErrorMessage.none;
	private String message = "none";
	
	
	public MessageType getType(){return this.type;}
	public boolean getSuccess(){return this.success;}
	public ErrorMessage getError(){return this.error;}
	public String getMessage(){return this.message;}
	
	public void setType(MessageType t){this.type=t;}
	public void setSuccess(boolean s){this.success=s;}
	public void setError(ErrorMessage e){this.error=e;}

	public abstract String toString();

  public static Request fromString(String req) {
    // TODO
    return null;
  } 

}
