



/*
 * UserDataReq subclass of Requests that handles the transfer of 
 * the User's  Data between client and server
 * 
 * once instantiated the data contained within the object cannot be changed or modified
 */
public class UserDataReq extends Request {
	
	private static final long serialVersionUID = 2L; // For Serialization - @Dettervt
	public enum ReqType{upload,download}
	private ReqType direction;
	private byte[] userdata;//for storing AES encrypted User data
	private byte[] iv;

	//Request download
	public UserDataReq(){
		this.direction = ReqType.download;
		this.setEvent(EventType.userDataDownload);
	}
	//Request upload
	public UserDataReq(byte[] cyphertext, byte[] iv){
		this.direction = ReqType.upload;
		this.userdata=cyphertext;
		this.iv=iv;
		this.setEvent(EventType.userDataUpload);
	}
	//Response w/o data
	public UserDataReq(boolean success, ReqType dir, ErrorMessage err){
		this.direction = dir;
		this.setSuccess(success);
		this.setError(err);
	}
	//Response with data
	public UserDataReq(boolean success, ReqType dir, ErrorMessage err, byte[] cyphertext, byte[] iv){
		this.direction = dir;
		this.setSuccess(success);
		this.setError(err);
		this.userdata=cyphertext;
		this.iv=iv;
	}
		
	public ReqType getDirection(){return this.direction;}
	public byte[] getData(){return this.userdata;}
	public byte[] getIV(){return this.iv;}

}

