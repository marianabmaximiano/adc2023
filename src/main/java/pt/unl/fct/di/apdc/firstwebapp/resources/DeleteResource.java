package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.logging.Logger;



import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.*;

import pt.unl.fct.di.apdc.firstwebapp.util.*;


@Path("/delete")
@Produces(MediaType.APPLICATION_JSON)
public class DeleteResource {
	
	private static final Logger LOG = Logger.getLogger(DeleteResource.class.getName()); 
	
	private final Datastore datastore = DatastoreOptions.newBuilder().setProjectId("firm-dimension-379414").build().getService();
	
	public DeleteResource() {}
	
	
	private Response deleteAux(Transaction txn, Key key, Key tkey) {
		txn.delete(key);
		txn.delete(tkey);
		txn.commit();
		return Response.status(Status.OK).build();
	}
	
	@DELETE
	@Path("/v1")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteUser(DeleteData data) {
		LOG.fine("Attempt to delete user" + data.username2);
		
		Key key1 = datastore.newKeyFactory().setKind("User").newKey(data.username1);
		Key key2 = datastore.newKeyFactory().setKind("User").newKey(data.username2);

		Key tkey1 = datastore.newKeyFactory().setKind("Token").newKey(data.username1);
		Key tkey2 = datastore.newKeyFactory().setKind("Token").newKey(data.username2);
		
		 
		Transaction txn = datastore.newTransaction();
		
		try {
			
			long expiration = txn.get(tkey1).getLong("expiration_data");
			String tID = txn.get(tkey1).getString("token_id");
			
			if(expiration>System.currentTimeMillis() && tID!=null ) {
			
			Entity user1 = txn.get(key1);
			Entity user2 = txn.get(key2);
			
			
			
			if(user2 == null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("User" + data.username2 + " does not exist.").build();
			}
			
			else {
			
				String roleUser1 = user1.getString("user_role");
				String roleUser2 = user2.getString("user_role");
				
				boolean is1SU = roleUser1.equalsIgnoreCase(Role.SU.toString());
				boolean is1GS = roleUser1.equalsIgnoreCase(Role.GS.toString());
				boolean is1GBO = roleUser1.equalsIgnoreCase(Role.GBO.toString());
				boolean is1GA = roleUser1.equalsIgnoreCase(Role.GA.toString());
				boolean is1USER = roleUser1.equalsIgnoreCase(Role.USER.toString());
				
				boolean is2GBO = roleUser2.equalsIgnoreCase(Role.GBO.toString());
				boolean is2GA = roleUser2.equalsIgnoreCase(Role.GA.toString());
				boolean is2USER = roleUser2.equalsIgnoreCase(Role.USER.toString());
				
				boolean is1Active = user1.getString("state").equalsIgnoreCase(State.ACTIVE.toString());
				
				String token2 = txn.get(tkey2).getString("token_id");
				
				if(is1Active) {
					if(is1SU) {
						if(token2!=null)
						return deleteAux(txn, key2, tkey2);
					}
					else if(is1GS) {
						if(is2GBO || is2GA || is2USER) {
							return deleteAux(txn, key2, tkey2);
						} else {
							return Response.status(Status.BAD_REQUEST).entity("Not authorized.").build();
						}
					}
					else if(is1GA) {
						if(is2GBO || is2USER) {
							return deleteAux(txn, key2, tkey2);
						} else {
							return Response.status(Status.BAD_REQUEST).entity("Not authorized.").build();
						}
					}
					else if(is1GBO) {
						if(is2USER) {
							return deleteAux(txn, key2, tkey2);
						} else {
							return Response.status(Status.BAD_REQUEST).entity("Not authorized.").build();
						}
					}
					else if(is1USER) {
						if(roleUser2.contentEquals(roleUser1)) {
							return deleteAux(txn, key2, tkey2);
						} else {
							return Response.status(Status.BAD_REQUEST).entity("Not authorized.").build();
						}
					}
					
				}
			 	return Response.status(Status.BAD_REQUEST).entity("Not authorized.").build();
			} 
			
			
			}
			
			return Response.status(Status.BAD_REQUEST).entity("Not loged in.").build();
			
		} finally {
			if(txn.isActive()) {
				txn.rollback();
			}
			
		}
		}
	

}
