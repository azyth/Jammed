package jammed;

/*
 * LoginReq handles the Authentication of a user with the server by transmitting 
 * the username and password for the server to verify and initiate the session with
 * 
 * once instantiated the data contained within the object cannot be changed or modified
 */
public class LoginReq extends Request {

	private static final long serialVersionUID = 5L; // For Serialization - @Dettervt
	
	private String username;
	private String password;
	
	//Response constructor - err should be "none" if success is true
	public LoginReq(String username, boolean success, ErrorMessage err){
		this.username=username;
		this.setSuccess(success);
		this.setError(err);
		this.setEvent(EventType.login);
	}
	//Request constructor
	public LoginReq(LoginInfo l){
		this.username=l.username;
		this.password=l.password;
		this.setEvent(EventType.login);

	}
	
	
	public String getUsername(){return this.username;}
	public String getPassword(){return this.password;}

}
