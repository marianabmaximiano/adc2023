package pt.unl.fct.di.apdc.firstwebapp.resources;
import pt.unl.fct.di.apdc.firstwebapp.util.*;


import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;

@Path("/attribute")
@Produces(MediaType.APPLICATION_JSON)
public class AttributeResource {
	
	
	private static final Logger LOG = Logger.getLogger(RoleResource.class.getName()); 
	private final Datastore datastore = DatastoreOptions.newBuilder().setProjectId("firm-dimension-379414").build().getService();
	
	public AttributeResource() {}
	
	private Response changeAttributesAux(Transaction txn, Key key, AttributeData data) {
		String name, email, profile, phoneNumber, nif;
	
		if(data.name.isBlank())
			name = txn.get(key).getString("user_name");
		else name = data.name;
		if(data.email.isBlank())
			email = txn.get(key).getString("user_email");
		else email = data.email;
		if(data.profile.isBlank())
			profile = txn.get(key).getString("user_profile");
		else profile = data.profile;
		if(data.phoneNumber.isBlank())
			phoneNumber = txn.get(key).getString("user_profile");
		else phoneNumber = data.phoneNumber;
		if(data.nif.isBlank())
			nif = txn.get(key).getString("user_profile");
		else nif = data.nif;
		
		Entity user = Entity.newBuilder(key)
				.set("id", txn.get(key).getString("id"))
				.set("user_name",name)
				.set("user_pwd", txn.get(key).getString("user_pwd"))
				.set("user_email", email)
				.set("user_profile",profile)
				.set("user_phone", phoneNumber)
				.set("user_nif", nif)
				.set("user_role", txn.get(key).getString("user_role"))
				.set("state", txn.get(key).getString("state"))
				.set("user_creation_time", Timestamp.now()).build();
		
		txn.update(user);
		txn.commit();
		
		return Response.status(Status.OK).build();
	}
	
	private Response changeUserAttributesAux(Transaction txn, Key key, AttributeData data) {
		String profile, phoneNumber, nif;

		if(data.profile.isBlank())
			profile = txn.get(key).getString("user_profile");
		else profile = data.profile;
		if(data.phoneNumber.isBlank())
			phoneNumber = txn.get(key).getString("user_profile");
		else phoneNumber = data.phoneNumber;
		if(data.nif.isBlank())
			nif = txn.get(key).getString("user_profile");
		else nif = data.nif;
		
		Entity user = Entity.newBuilder(key)
				.set("id", txn.get(key).getString("id"))
				.set("user_name",txn.get(key).getString("user_name"))
				.set("user_pwd", txn.get(key).getString("user_pwd"))
				.set("user_email", txn.get(key).getString("user_email"))
				.set("user_profile",profile)
				.set("user_phone", phoneNumber)
				.set("user_nif", nif)
				.set("user_role", txn.get(key).getString("user_role"))
				.set("state", txn.get(key).getString("state"))
				.set("user_creation_time", Timestamp.now()).build();
		
		txn.update(user);
		txn.commit();
		
		return Response.status(Status.OK).build();
	}
	
	
	
	@POST
	@Path("/change")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeAttributes(AttributeData data) {
		
		LOG.fine("Attempt to change user attributes");
		
		Key key1 = datastore.newKeyFactory().setKind("User").newKey(data.username1);
		Key key2 = datastore.newKeyFactory().setKind("User").newKey(data.username2);
		
		Transaction txn = datastore.newTransaction();
		
		String roleUser1 = txn.get(key1).getString("user_role");
		String roleUser2 = txn.get(key2).getString("user_role");
		
		Key tkey = datastore.newKeyFactory().setKind("Token").newKey(data.username1);
		
		try {
			
			long expiration = txn.get(tkey).getLong("expiration_data");
			String tID = txn.get(tkey).getString("token_id");
			
			if(expiration>System.currentTimeMillis() && tID!=null ) {
			
			boolean is1SU = roleUser1.equalsIgnoreCase(Role.SU.toString());
			boolean is1GS = roleUser1.equalsIgnoreCase(Role.GS.toString());
			boolean is1GBO = roleUser1.equalsIgnoreCase(Role.GBO.toString());
			boolean is1USER = roleUser1.equalsIgnoreCase(Role.USER.toString());
			
			boolean is2GS = roleUser2.equalsIgnoreCase(Role.GS.toString());
			boolean is2GBO = roleUser2.equalsIgnoreCase(Role.GBO.toString());
			boolean is2USER = roleUser2.equalsIgnoreCase(Role.USER.toString());
			
			boolean is1Active = txn.get(key1).getString("state").equalsIgnoreCase(State.ACTIVE.toString());
			
			if(is1Active) {
			
			if(is1SU) {
				if(is2GS || is2GBO || is2USER) {
					return changeAttributesAux(txn, key2, data);
				}
				else {
					return Response.status(Status.BAD_REQUEST).entity("Not authorized.").build();
				}
			}
			else if(is1GS) {
				if(is2GBO || is2USER) {
					return changeAttributesAux(txn, key2, data);
				}
				else {
					return Response.status(Status.BAD_REQUEST).entity("Not authorized.").build();
				}
			}
			else if(is1GBO) {
				if(is2USER) {
					return changeAttributesAux(txn, key2, data);
				}
				else {
					return Response.status(Status.BAD_REQUEST).entity("Not authorized.").build();
				}
			}
			else if(is1USER) {
				if(data.username1.contentEquals(data.username2)) {
					return changeUserAttributesAux(txn, key2, data);
				}
				else {
					return Response.status(Status.BAD_REQUEST).entity("Not authorized.").build();
				}
			}
			
			}
			
			return Response.status(Status.BAD_REQUEST).entity("Not active.").build();
			
			}
			
			return Response.status(Status.BAD_REQUEST).entity("Not loged in.").build();
			
		} finally {
			if(txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	
	@GET
	@Path("/{username}")
	public Response getAttributes(@PathParam("username") String username) {
		Key key = datastore.newKeyFactory().setKind("User").newKey(username);
		Transaction txn = datastore.newTransaction();
		try {
			String name = txn.get(key).getString("user_name");
			String email = txn.get(key).getString("user_email");
			String profile = txn.get(key).getString("user_profile");
			return Response.status(Status.OK).entity(username + " attributes are: name " + name + ", email: " + email + ", profile: " + profile).build();

		} finally {
			if (txn.isActive())
				txn.rollback();
		}
	}
	
	
	@POST
	@Path("/changePass")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changePassword(PasswordData data) {
		
		Key key = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Key tkey = datastore.newKeyFactory().setKind("Token").newKey(data.username);
		Transaction txn = datastore.newTransaction();
		
		try {
			
			long expiration = txn.get(tkey).getLong("expiration_data");
			String tID = txn.get(tkey).getString("token_id");
			
			if(expiration>System.currentTimeMillis() && tID!=null ) {
			
			boolean isRightPass = DigestUtils.sha512Hex(data.password).contentEquals(txn.get(key).getString("user_pwd"));
			boolean doubleCheckNewPass = DigestUtils.sha512Hex(data.newPassword).contentEquals(DigestUtils.sha512Hex(data.newPassword2));
			boolean isActive = txn.get(key).getString("state").equalsIgnoreCase(State.ACTIVE.toString());
			
			
			if(isActive && isRightPass && doubleCheckNewPass && data.newPassword.length()>=6) {
				Entity user = Entity.newBuilder(txn.get(key)).set("user_pwd",DigestUtils.sha512Hex(data.newPassword)).build();
				txn.update(user);
				txn.commit();
				
				return Response.status(Status.OK).entity("Password updated.").build();
			}
			
			return Response.status(Status.BAD_REQUEST).entity("Not authorized.").build();
			
			}
			
			return Response.status(Status.BAD_REQUEST).entity("Not loged in.").build();
			
		} finally {
			if(txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
}
