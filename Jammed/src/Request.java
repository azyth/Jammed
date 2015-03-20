

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
public abstract class Request implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L; // For Serialization - @Dettervt
	// Each Request class and subclass needs a serial version UID
	// Adding one to each
	
	public enum EventType{login,log,userDataDownload,userDataUpload,termination}
	public enum MessageType{request,response}
	public enum ErrorMessage{badArgument,validationFailed,somethingBad,none}//etc
	
	private EventType event;
	private MessageType type;
	private boolean success = false;
	private ErrorMessage error=ErrorMessage.none;
	private String message = "none";
	
	
	public MessageType getType(){return this.type;}
	public boolean getSuccess(){return this.success;}
	public ErrorMessage getError(){return this.error;}
	public String getMessage(){return this.message;}
	public EventType getEvent(){return this.event;}
	
	public void setType(MessageType t){this.type=t;}
	public void setSuccess(boolean s){this.success=s;}
	public void setError(ErrorMessage e){this.error=e;}
	public void setEvent(EventType e){this.event=e;}

  public static String errToString(ErrorMessage e) {
    String message;
    switch (e) {
      case badArgument:
        message = "Incorrect argument to function.";
        break;
      case validationFailed:
        message = "Could not validate credentials.";
        break;
      case somethingBad:
        message = "Some unknown but terrible thing happened.";
        break;
      case none:
        message = "No error.";
        break;
      default:
        message = "Some other error occurred - update errToString!";
    }
    return message;
  } 

}
