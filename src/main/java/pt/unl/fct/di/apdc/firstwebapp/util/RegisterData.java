package pt.unl.fct.di.apdc.firstwebapp.util;

import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;

public class RegisterData {
	
	public String username;
	public String password;
	public String password2;
	public String email;
	public String name;
	public String profile;
	public String phoneNumber;
	public String nif;
	public Role role;
	public State state;
	
	public RegisterData() {}
	
	public RegisterData(String username, String password, String password2, String email, String name, String profile, String phoneNumber, String nif) {
		this.username = username;
		this.password = password;
		this.password2 = password2;
		this.email = email;
		this.name = name;
		this.profile = profile;
		this.phoneNumber = phoneNumber;
		this.nif = nif;
		role = Role.USER;
		state = State.INACTIVE;
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
	
	public boolean doubleCheckPass() {
		if(DigestUtils.sha512Hex(password).contentEquals(DigestUtils.sha512Hex(password2))) {
			return true;
		}
		return false;
	}
	
	public boolean validPass() {
		if(password.length()>=6) {
			return true;
		}
		return false;
	}

}
