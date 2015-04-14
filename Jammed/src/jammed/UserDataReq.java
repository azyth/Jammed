package jammed;


/*
 * UserDataReq subclass of Requests that handles the transfer of 
 * the User's  Data between client and server
 * 
 * once instantiated the data contained within the object cannot be changed or modified
 */
public class UserDataReq extends Request implements Cloneable {
	
	private static final long serialVersionUID = 2L; // For Serialization - @Dettervt
	public enum ReqType { UPLOAD, DOWNLOAD }
	private ReqType direction;
	private byte[] userdata;//for storing AES encrypted User data
	private byte[] iv;

	//Request download
	public UserDataReq(){
		this.direction = ReqType.DOWNLOAD;
		this.setEvent(EventType.USER_DATA_OP);
	}
	//Request upload
	public UserDataReq(byte[] cyphertext, byte[] iv){
		this.direction = ReqType.UPLOAD;
		this.userdata=cyphertext.clone();
		this.iv=iv.clone();
		this.setEvent(EventType.USER_DATA_OP);
	}
	//Response w/o data
	public UserDataReq(boolean success, ErrorMessage err){
		this.setSuccess(success);
		this.setError(err);
	}

	//Response with data
	public UserDataReq(boolean success, ErrorMessage err, byte[] cyphertext, byte[] iv){
		this.setSuccess(success);
		this.setError(err);
		this.userdata=cyphertext.clone();
		this.iv=iv.clone();
	}
		
	public ReqType getDirection() { return this.direction; }
	public byte[] getData() { return this.userdata.clone(); }
	public byte[] getIV() { return this.iv.clone(); }

}

