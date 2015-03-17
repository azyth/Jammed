

	//John and megan

/*
 * Request
 * 
 * 
 * requests is a superclass object that stores info for requests across the network
 * 
 * some of this may seem irrelevant but may be useful later. 
 */
public class Request {
	
	//public enum Event{login,log,userDataDownload,userDataUpload,termination}
	public enum MessageType{request,response}
	public enum ErrorMessage{badArgument,validationFailed,somethingBad,none}//etc
	
	//private Event event;
	private MessageType type;
	private boolean success = false;
	private ErrorMessage error=ErrorMessage.none;
	private String message = "none";
	
	
//	public Event getEvent(){return this.event;}
	public MessageType getType(){return this.type;}
	public boolean getSuccess(){return this.success;}
	public ErrorMessage getError(){return this.error;}
	public String getMessage(){return this.message;}
	
//	public void setEvent(Event e){this.event=e;}
	public void setType(MessageType t){this.type=t;}
	public void setSuccess(boolean s){this.success=s;}
	public void setError(ErrorMessage e){this.error=e;}

	public String toString(){
		
		return null;
	}
	
	
	
	
	/*************************Depreciated Below******************************/
	
//	//Log in
//	public String loginReq(String username, String password){
//		return null;
//	}
//	public String loginReqAccepted(){
//		return null;
//	}
//	public String loginReqDenied(String errorMessage){
//		return null;
//	}
//	
//	//Log file
//	public String userLogReq(){
//		return null;
//	}
//	
//	public String userLogReqAccepted(){
//		return null;
//	}
//	public String userLogReqDenied(){
//		return null;
//	}
//	
//	//User Data download
//	public String userDataReq(){
//		return null;
//	}
//	public String userDataReqAccepted(){
//		return null;
//	}
//	public String userDataReqDenied(){
//		return null;
//	}
//	
//	//User data upload
//	public String userDataUploadReq(){
//		return null;
//	}
//	public String userDataUploadReqAccepted(){
//		return null;
//	}
//	public String userDataUploadReqDenied(){
//		return null;
//	}
//	
//	//close connection/session
//	public String connectionTerminationReq(){
//		return null;
//	}
//	public String connectionTerminationReqAccepted(){
//		return null;
//	}
//	public String connectionTerminationReqDenied(){
//		return null;
//	}
//	
//	//Parse to JSON for program to use.
//	public void parseReq(String requestmessage){
//		
//	}

}
