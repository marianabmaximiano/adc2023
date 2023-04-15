package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.logging.Logger;


import com.google.cloud.datastore.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.unl.fct.di.apdc.firstwebapp.util.*;

@Path("/role")
@Produces(MediaType.APPLICATION_JSON)
public class RoleResource {
	
	private static final Logger LOG = Logger.getLogger(RoleResource.class.getName()); 
	private final Datastore datastore = DatastoreOptions.newBuilder().setProjectId("firm-dimension-379414").build().getService();
	
	public RoleResource() {}
	
	private Response switchRoleAux(Transaction txn, Key key, String newRole) {
		
		Entity user2 = Entity.newBuilder(txn.get(key)).set("user_role", newRole).build();
		
		txn.update(user2);
		txn.commit();
		
		return Response.status(Status.OK).build();
	}
	
	@POST
	@Path("/change")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response switchRole(RoleData data) {
		
		LOG.fine("Attempt to change user role");
		
		Key key1 = datastore.newKeyFactory().setKind("User").newKey(data.username1);
		Key key2 = datastore.newKeyFactory().setKind("User").newKey(data.username2);
		
		Key tkey = datastore.newKeyFactory().setKind("Token").newKey(data.username1);
		
		String newRole = data.newRole.toString();
		
		Transaction txn = datastore.newTransaction();
		
		String roleUser1 = txn.get(key1).getString("user_role");
		String roleUser2 = txn.get(key2).getString("user_role");
		
		try {
			
			long expiration = txn.get(tkey).getLong("expiration_data");
			String tID = txn.get(tkey).getString("token_id");
			
			if(expiration>System.currentTimeMillis() && tID!=null ) {
			
			boolean is1SU = roleUser1.equalsIgnoreCase(Role.SU.toString());
			boolean is1GS = roleUser1.equalsIgnoreCase(Role.GS.toString());
			boolean is1GBO = roleUser1.equalsIgnoreCase(Role.GBO.toString());
			boolean is1GA = roleUser1.equalsIgnoreCase(Role.GA.toString());
			boolean is1USER = roleUser1.equalsIgnoreCase(Role.USER.toString());
			boolean is2USER = roleUser2.equalsIgnoreCase(Role.USER.toString());
			
			if(is1SU) {
				return switchRoleAux(txn, key2, newRole);
			}
			else if(is1GS) {
				if(is2USER && newRole.equalsIgnoreCase(Role.GBO.toString())) {
					return switchRoleAux(txn, key2, newRole);
				}
			}
			else if(is1GA) {
				return Response.status(Status.BAD_REQUEST).entity("Not authorized.").build();
			}
			else if(is1GBO) {
				return Response.status(Status.BAD_REQUEST).entity("Not authorized.").build();
			}
			else if(is1USER) {
				return Response.status(Status.BAD_REQUEST).entity("Not authorized.").build();
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
	
	
	
	@GET
	@Path("/{username}")
	public Response getRole(@PathParam("username") String username) {
		Key key = datastore.newKeyFactory().setKind("User").newKey(username);
		Transaction txn = datastore.newTransaction();
		try {
			String role = txn.get(key).getString("user_role");
			return Response.status(Status.OK).entity(username + " is a " + role).build();

		} finally {
			if (txn.isActive())
				txn.rollback();
		}
	}
	
	
	@POST
	@Path("/su")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response turnSupervisor(RegisterData data) {  
			Key key = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Transaction txn = datastore.newTransaction();
			try {
				Entity user = Entity.newBuilder(txn.get(key)).set("user_role", Role.SU.toString()).build();
				txn.update(user);
				txn.commit(); 
			return Response.status(Status.OK).entity(data.username + " is now a SU.").build();
			} finally {
				if (txn.isActive())
					txn.rollback();
			}
		
	}
	
	
	
	
	
	
}
