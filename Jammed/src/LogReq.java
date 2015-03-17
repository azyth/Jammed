


/*
 * LogReq handles the requests for the users log file so that they might 
 * manually audit their account activity.
 * 
 * once instantiated the data contained within the object cannot be changed or modified
 */
public class LogReq extends Request{
	
	private String username; //used by the server to double check user identity, this better match the session username
	private String userLog; //holds the log when passing back to the user.

	public LogReq(String username){
		this.username = username;
	}
	public LogReq(String username, String log){
		this(log);
		this.userLog=log;
	}
	
//	public void setLog(String l){this.userLog=l;}
	public String getLog(){return this.userLog;}
	public String getUsername(){return this.username;}


}
