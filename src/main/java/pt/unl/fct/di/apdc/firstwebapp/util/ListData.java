package pt.unl.fct.di.apdc.firstwebapp.util;

public class ListData {

	public String username;
	public String name;
	public String email;
	public String profile;
	public String role;
	public String state;
	
	public ListData() {}
	
	public ListData(String username, String name, String email, String profile, String role, String state) {
		this.username = username;
		this.name = name;
		this.email = email;
		this.profile = profile;
		this.role = role;
		this.state = state;
	}
}
