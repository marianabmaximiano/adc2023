package pt.unl.fct.di.apdc.firstwebapp.util;

public class PasswordData {
	
	public String username;
	public String password;
	public String newPassword;
	public String newPassword2;
	
	public PasswordData() {}
	
	public PasswordData(String username, String password, String newPassword, String newPassword2) {
		this.username = username;
		this.password = password;
		this.newPassword = newPassword;
		this.newPassword2 = newPassword2;
	}

}
