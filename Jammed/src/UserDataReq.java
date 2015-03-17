
public class UserDataReq extends Request {

	
	public enum ReqType{upload,download}
	
	private byte[] userdata;
	
	public byte[] getData(){return this.userdata;}
	
	public void setData(byte[] d){this.userdata=d;}
}
