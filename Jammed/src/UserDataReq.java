

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
		
	public UserDataReq(byte[] cyphertext, ReqType dir){
    direction = dir;
		this.userdata=cyphertext;
	}
		
	public ReqType getDirection(){return this.direction;}
	public byte[] getData(){return this.userdata;}
//	public void setData(byte[] d){this.userdata=d;}
}
