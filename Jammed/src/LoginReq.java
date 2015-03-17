
public class LoginReq extends Request {
	
	private String username;
	private String password;
	
	
	public String getUsername(){return this.username;}
	public String getPassword(){return this.password;}
	public void setUsername(String u){this.username=u;}
	public void setPassword(String p){this.password=p;}

}
