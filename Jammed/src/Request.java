	//John and megan

/*
 * Request
 * 
 * 
 * requests accepted messages will contain the data requested in *info:*
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
	
	
	//Log in
	public String loginReq(String username, String password){
		return null;
	}
	public String loginReqAccepted(){
		return null;
	}
	public String loginReqDenied(String errorMessage){
		return null;
	}
	
	//Log file
	public String userLogReq(){
		return null;
	}
	
	public String userLogReqAccepted(){
		return null;
	}
	public String userLogReqDenied(){
		return null;
	}
	
	//User Data download
	public String userDataReq(){
		return null;
	}
	public String userDataReqAccepted(){
		return null;
	}
	public String userDataReqDenied(){
		return null;
	}
	
	//User data upload
	public String userDataUploadReq(){
		return null;
	}
	public String userDataUploadReqAccepted(){
		return null;
	}
	public String userDataUploadReqDenied(){
		return null;
	}
	
	//close connection/session
	public String connectionTerminationReq(){
		return null;
	}
	public String connectionTerminationReqAccepted(){
		return null;
	}
	public String connectionTerminationReqDenied(){
		return null;
	}
	
	//Parse to JSON for program to use.
	public void parseReq(String requestmessage){
		
	}

}
