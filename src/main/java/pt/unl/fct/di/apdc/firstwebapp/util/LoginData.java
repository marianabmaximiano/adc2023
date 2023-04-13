package pt.unl.fct.di.apdc.firstwebapp.util;

public class LoginData {
	
	public String username;
	public String password;
	public Role role;
	
	public LoginData() {}
	
	public LoginData(String username, String password, Role role) {
		this.username = username;
		this.password = password;
		this.role = role;
	}

}
