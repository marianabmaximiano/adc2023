package pt.unl.fct.di.apdc.firstwebapp.util;

public class RoleData {

	public String username1;
	public String username2;
	public Role newRole;
	
	public RoleData() {}
	
	public RoleData(String username1, String username2, String newRole) {
		this.username1 = username1;
		this.username2 = username2;
		this.newRole = Role.valueOf(newRole);
	}
	
}
