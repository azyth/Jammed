package jammed;


/*
 * TerminationReq subclass of Requests that communicates the termination of 
 * connections between client and server
 * 
 * once instantiated the data contained within the object cannot be changed or modified
 */
public class TerminationReq extends Request {
	
	private static final long serialVersionUID = 3L; // For Serialization - @Dettervt
	
	//reason for close? timeout, user request, server goign to explode
	public enum Term{ TIMEOUT, USER_REQUEST, ARMAGEDDON, NONE }
	
	private Term reason;
	
	//Request
	public TerminationReq(Term reason){
		this.reason=reason;
		this.setEvent(EventType.TERMINATION);
	}
	//Response
	public TerminationReq(boolean success, ErrorMessage err){
		this.setSuccess(success);
		this.setError(err);
		this.setEvent(EventType.TERMINATION);

	}
	
	public Term getReason(){return this.reason;}

}
