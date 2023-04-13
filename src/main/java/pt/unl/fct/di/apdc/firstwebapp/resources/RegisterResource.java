package pt.unl.fct.di.apdc.firstwebapp.resources;

import pt.unl.fct.di.apdc.firstwebapp.util.*;



import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;


@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON)
public class RegisterResource {
	
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName()); 
	
	private final Datastore datastore = DatastoreOptions.newBuilder().setProjectId("firm-dimension-379414").build().getService();
	
	public RegisterResource() {}
	
	
	
	
	@POST
	@Path("/v2")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doRegistrationV2(RegisterData data) {
		LOG.fine("Attempt to register user" + data.username);
		
		if(!data.validEmail()) {
			return Response.status(Status.BAD_REQUEST).entity("Invalid email format.").build();
		}
		if(!data.doubleCheckPass()) {
			return Response.status(Status.BAD_REQUEST).entity("Password is not confirmed.").build();
		}
		if(!data.validPass()) {
			return Response.status(Status.BAD_REQUEST).entity("Invalid password format.").build();
		}
		
		Transaction tn = datastore.newTransaction();
		
		try {
		
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Entity user = tn.get(userKey);
		if(user!=null) {
			tn.rollback();
			return Response.status(Status.BAD_REQUEST).entity("User already exists").build();
		} else {
			user = Entity.newBuilder(userKey)
				.set("id", data.username)
				.set("user_name", data.name)
				.set("user_pwd", DigestUtils.sha512Hex(data.password))
				.set("user_email", data.email)
				.set("user_profile", data.profile)
				.set("user_phone", data.phoneNumber)
				.set("user_nif", data.nif)
				.set("user_role", Role.USER.toString())
				.set("state", State.INACTIVE.toString())
				.set("user_creation_time", Timestamp.now()).build();
			
			
			tn.add(user);
			LOG.info("User registered " + data.username);
			tn.commit();
			return Response.ok("{}").build();
			
		}
		
		} finally {
			if(tn.isActive()) {
				tn.rollback();
			}
			
		}
		
		
	}
	
	

	
	
	
}
