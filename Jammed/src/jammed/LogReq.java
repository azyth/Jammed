package jammed;


/*
 * LogReq handles the requests for the users log file so that they might 
 * manually audit their account activity.
 * 
 * once instantiated the data contained within the object cannot be changed or modified
 */
public class LogReq extends Request{
	
	private static final long serialVersionUID = 4L; // For Serialization - @Dettervt
	
	private String userLog; //holds the log when passing back to the user.

	//Request constructor
	public LogReq() {
		this.setType(MessageType.REQUEST);
		this.setEvent(EventType.LOG);
	}

	//Response constructors
	//failure 
	public LogReq(ErrorMessage err){
		this.setError(err);
		this.setType(MessageType.RESPONSE);
		this.setSuccess(false);
		this.setEvent(EventType.LOG);
	}
	//succeed
	public LogReq(String log){
		this.userLog=log;
		this.setSuccess(true);
		this.setType(MessageType.RESPONSE);
		this.setEvent(EventType.LOG);
	}
	
//	public void setLog(String l){this.userLog=l;}
	public String getLog(){return this.userLog;}

}
