

/*
 * UserDataReq subclass of Requests that handles the transfer of 
 * the User's  Data between client and server
 * 
 * once instantiated the data contained within the object cannot be changed or modified
 */
public class UserDataReq extends Request {
	
	public enum ReqType{upload,download}
	private ReqType direction;
	private byte[] userdata;//for storing AES encrypted User data

	//Request download
	public UserDataReq(){
		this.direction = ReqType.download;
	}
	//Request upload
	public UserDataReq(byte[] cyphertext){
		this.direction = ReqType.upload;
		this.userdata=cyphertext;
	}
	//Response w/o data
	public UserDataReq(boolean success, ReqType dir, ErrorMessage err){
		this.direction = dir;
		this.setSuccess(success);
		this.setError(err);
	}
	//Response with data
	public UserDataReq(boolean success, ReqType dir, ErrorMessage err, byte[] cyphertext){
		this.direction = dir;
		this.setSuccess(success);
		this.setError(err);
		this.userdata=cyphertext;
	}
		
	public ReqType getDirection(){return this.direction;}
	public byte[] getData(){return this.userdata;}

  public String toString() {
    // TODO
    return "";
  }
}

