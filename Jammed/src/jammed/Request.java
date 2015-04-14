package jammed;

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
	
	public enum EventType { LOGIN, LOG, USER_DATA_OP, TERMINATION }
	public enum MessageType { REQUEST, RESPONSE }
	public enum ErrorMessage { BAD_ARGUMENT, BAD_CREDENTIALS, NO_SUCH_USER,
    DUPLICATE_USERNAME, BAD_REQUEST, DATABASE_FAILURE, OTHER, NONE }//etc
	
	private EventType event;
	private MessageType type;
	private boolean success = false;
	private ErrorMessage error = ErrorMessage.NONE;
	private String message = "none";
	
	public Request() {}
	
	public MessageType getType() { return this.type; }
	public boolean getSuccess() { return this.success; }
	public ErrorMessage getError() { return this.error; }
	public String getMessage() { return this.message; }
	public EventType getEvent() { return this.event; }
	
	public void setType(MessageType t) { this.type=t; }
	public void setSuccess(boolean s) { this.success=s; }
	public void setError(ErrorMessage e) { this.error=e; }
	public void setEvent(EventType e) { this.event=e; }

  public static String errToString(ErrorMessage e) {
    String message;
    switch (e) {
      case BAD_ARGUMENT:
        message = "Incorrect argument to function.";
        break;
      case BAD_CREDENTIALS:
        message = "Could not validate credentials.";
        break;
      case NO_SUCH_USER:
        message = "User account does not exist in database.";
        break;
      case DUPLICATE_USERNAME:
        message = "Another account exists with that username.";
        break;
      case BAD_REQUEST:
        message = "Unexpected request sent to server.";
        break;
      case DATABASE_FAILURE:
        message = "I/O issue with server database.";
        break;
      case OTHER:
        message = "Some unknown but terrible thing happened. (request.java)";
        break;
      case NONE:
        message = "No error.";
        break;
      default:
        message = "Some other error occurred - update errToString!";
    }
    return message;
  } 

}
