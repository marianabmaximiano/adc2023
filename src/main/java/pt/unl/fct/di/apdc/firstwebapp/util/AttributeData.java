package pt.unl.fct.di.apdc.firstwebapp.util;

import java.util.regex.Pattern;

public class AttributeData {

	public String username1;
	public String username2;
	public String email;
	public String name;
	public String profile;
	public String phoneNumber;
	public String nif;
	
	public AttributeData() {}
	
	public AttributeData(String username1, String username2,  String email, String name, String profile, String phoneNumber, String nif) {
		this.username1 = username1;
		this.username2 = username2;
		this.email = email;
		this.name = name;
		this.profile = profile;
		this.phoneNumber = phoneNumber;
		this.nif = nif;
	
	}
	
	public boolean patternMatches(String email, String pattern) {
	    return Pattern.compile(pattern).matcher(email).matches();
	}

	public boolean validEmail() {
		String emailPattern = "^(.+)@(\\S+)$";
	    if(patternMatches(email, emailPattern)) {
	    	return true;
	    }
		return false;
	}
}
