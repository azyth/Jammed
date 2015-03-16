	//John and megan

/*
 * Request
 * 
 * 
 * requests is a object that store info for requests
 * 
 * is converted using toString()
 */
public class Request {
//TODO everything
	
	/*
	 * variables 
	 * event
	 * success
	 * username
	 * password
	 * userdata
	 * 
	 * 
	 * toString()
	 * get/set()
	 */
	public enum Event{login,log,userData,termination}
	public enum MessageType{request,upload,download,accept,denial}
	
	private Event event;
	private MessageType type;
	private boolean success = false;
	private String errorMessage;
	private String username;
	private String password;
	private byte[] userdata;
	
	public Event getEvent(){return this.event;}
	public MessageType getType(){return this.type;}
	public boolean getSuccess(){return this.success;}
	public String getError(){return this.errorMessage;}
	public String getUsername(){return this.username;}
	public String getPassword(){return this.password;}
	public byte[] getData(){return this.userdata;}
	
	public void setEvent(Event e){this.event=e;}
	public void setType(MessageType t){this.type=t;}
	public void setSuccess(boolean s){this.success=s;}
	public void setError(String e){this.errorMessage=e;}
	public void setUsername(String u){this.username=u;}
	public void setPassword(String p){this.password=p;}
	public void setData(byte[] d){this.userdata=d;}
	
	public String toString(){
		if (this.event==Event.login && this.type==MessageType.request){
			//loginrequest
			return null;
		}
		return null;
	}
	
	
	/*****************************************************************/
	
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
