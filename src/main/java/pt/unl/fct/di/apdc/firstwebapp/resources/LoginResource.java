package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.logging.Logger;


import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import javax.ws.rs.POST;
import javax.ws.rs.Consumes;



import pt.unl.fct.di.apdc.firstwebapp.util.LoginData;
import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;

import com.google.gson.Gson;
import com.google.cloud.datastore.*;


@Path("/account")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {
	
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName()); 
	
	private final Gson g = new Gson();
	
	private final Datastore datastore = DatastoreOptions.newBuilder().setProjectId("firm-dimension-379414").build().getService();
	
	
	public LoginResource() {} 

	
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doLogin (LoginData data) {
		LOG.fine("Attempt to login user: " + data.username);
		
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username); 
		
		Key tKey = datastore.newKeyFactory().setKind("Token").newKey(data.username); 
		
		Transaction txn = datastore.newTransaction();
		
		try {
		Entity user = txn.get(userKey);
		Entity tk = txn.get(tKey);
		
		if(user != null) {
			String hashedPWD = user.getString("user_pwd");
			if(hashedPWD.equals(DigestUtils.sha512Hex(data.password))) {
				AuthToken token = new AuthToken(data.username, user.getString("user_role"));
				
				tk = Entity.newBuilder(tKey)
						.set("token_id", token.tokenID)
						.set("expiration_data", token.expirationData)
						.build();
				txn.add(tk);
				txn.commit();
				LOG.info("User " + data.username + " logged in successfully.");
				return Response.ok(g.toJson(token)).build();
			} else {
				LOG.warning("Wrong password for username:" + data.username);
				return Response.status(Status.FORBIDDEN).build();
			}
		}
		else {
			LOG.warning("Failed login attempt for username " + data.username );
			return Response.status(Status.FORBIDDEN).build();
			
		}
		} finally {
			if(txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	
	@POST
	@Path("/logout")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogout(LoginData data) {
		
		Key tKey = datastore.newKeyFactory().setKind("Token").newKey(data.username); 
		Transaction txn = datastore.newTransaction();
		
		try {
			Entity token = txn.get(tKey);
			if(token.getString("token_id") != null) {
				txn.delete(tKey);
				txn.commit();
				return Response.status(Status.OK).entity(data.username + " is now logged out. Token was revoked.").build();
			}
			return Response.status(Status.BAD_REQUEST).entity("Token doesn't exist.").build();
		} finally {
			if(txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	
	

}
