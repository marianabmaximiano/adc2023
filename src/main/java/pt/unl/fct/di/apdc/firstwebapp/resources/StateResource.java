package pt.unl.fct.di.apdc.firstwebapp.resources;
import pt.unl.fct.di.apdc.firstwebapp.util.Role;
import pt.unl.fct.di.apdc.firstwebapp.util.State;
import pt.unl.fct.di.apdc.firstwebapp.util.StateData;

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

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;


@Path("/state")
@Produces(MediaType.APPLICATION_JSON)
public class StateResource {
	
	private static final Logger LOG = Logger.getLogger(RoleResource.class.getName()); 
	private final Datastore datastore = DatastoreOptions.newBuilder().setProjectId("firm-dimension-379414").build().getService();
	
	public StateResource() {}
	
	private Response activateAux(Transaction txn, Key key) {
		
		Entity user2 = Entity.newBuilder(txn.get(key)).set("state", State.ACTIVE.toString()).build();
		
		txn.update(user2);
		txn.commit();
		
		return Response.status(Status.OK).build();
	}
	
	private Response deactivateAux(Transaction txn, Key key) {
		
		Entity user2 = Entity.newBuilder(txn.get(key)).set("state", State.INACTIVE.toString()).build();
		
		txn.update(user2);
		txn.commit();
		
		return Response.status(Status.OK).build();
	}
	
	
	@POST
	@Path("/activate")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response activate(StateData data) {
		
		LOG.fine("Attempt to activate user.");
		
		Key key1 = datastore.newKeyFactory().setKind("User").newKey(data.username1);
		Key key2 = datastore.newKeyFactory().setKind("User").newKey(data.username2);
		
		Transaction txn = datastore.newTransaction();
		
		String roleUser1 = txn.get(key1).getString("user_role");
		String roleUser2 = txn.get(key2).getString("user_role");
		
		String stateUser1 = txn.get(key1).getString("state");
		String stateUser2 = txn.get(key2).getString("state");
		
		try {
			
			boolean is1SU = roleUser1.equalsIgnoreCase(Role.SU.toString());
			boolean is1GS = roleUser1.equalsIgnoreCase(Role.GS.toString());
			boolean is1GBO = roleUser1.equalsIgnoreCase(Role.GBO.toString());
			boolean is1GA = roleUser1.equalsIgnoreCase(Role.GA.toString());
			boolean is1USER = roleUser1.equalsIgnoreCase(Role.USER.toString());
			
			boolean is2GBO = roleUser2.equalsIgnoreCase(Role.GBO.toString());
			boolean is2GA = roleUser2.equalsIgnoreCase(Role.GA.toString());
			boolean is2USER = roleUser2.equalsIgnoreCase(Role.USER.toString());
			
			boolean is1Active = stateUser1.equalsIgnoreCase(State.ACTIVE.toString());
			boolean is2Inactive = stateUser2.equalsIgnoreCase(State.INACTIVE.toString());
			
			
			if(is1Active && is2Inactive) {
			
			if(is1SU) {
				return activateAux(txn,key2);
			}
			else if(is1GS) {
				if(is2GA || is2GBO) {
					return activateAux(txn,key2);
				}
			}
			else if(is1GA) {
				if(is2GBO || is2USER) {
					return activateAux(txn,key2);
				}
			}
			else if(is1GBO) {
				if(is2USER) {
					return activateAux(txn,key2);
				}
			}
			else if(is1USER) {
				return Response.status(Status.BAD_REQUEST).entity("Not authorized.").build();
			}
			
			else {return Response.status(Status.BAD_REQUEST).entity("Not authorized.").build();}
			
			
			} 
			return Response.status(Status.BAD_REQUEST).entity("Not authorized.").build();
			
			
			
			
			
		} finally {
			if(txn.isActive()) {
				txn.rollback();
			}
		}
		
	}
	
	
	@POST
	@Path("/deactivate")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deactivate(StateData data) {
		
		LOG.fine("Attempt to deactivate user.");
		
		Key key1 = datastore.newKeyFactory().setKind("User").newKey(data.username1);
		Key key2 = datastore.newKeyFactory().setKind("User").newKey(data.username2);
		
		Transaction txn = datastore.newTransaction();
		
		String roleUser1 = txn.get(key1).getString("user_role");
		String roleUser2 = txn.get(key2).getString("user_role");
		
		String stateUser1 = txn.get(key1).getString("state");
		String stateUser2 = txn.get(key2).getString("state");
		
		try {
			
			boolean is1SU = roleUser1.equalsIgnoreCase(Role.SU.toString());
			boolean is1GS = roleUser1.equalsIgnoreCase(Role.GS.toString());
			boolean is1GBO = roleUser1.equalsIgnoreCase(Role.GBO.toString());
			boolean is1GA = roleUser1.equalsIgnoreCase(Role.GA.toString());
			boolean is1USER = roleUser1.equalsIgnoreCase(Role.USER.toString());
			
			boolean is2GBO = roleUser2.equalsIgnoreCase(Role.GBO.toString());
			boolean is2GA = roleUser2.equalsIgnoreCase(Role.GA.toString());
			boolean is2USER = roleUser2.equalsIgnoreCase(Role.USER.toString());
			
			boolean is1Active = stateUser1.equalsIgnoreCase(State.ACTIVE.toString());
			boolean is2Active = stateUser2.equalsIgnoreCase(State.ACTIVE.toString());
			
			
			
			
			if(is1Active && is2Active) {
			
			if(is1SU) {
				return deactivateAux(txn,key2);
			}
			else if(is1GS) {
				if(is2GA || is2GBO) {
					return deactivateAux(txn,key2);
				}
			}
			else if(is1GA) {
				if(is2GBO || is2USER) {
					return deactivateAux(txn,key2);
				}
			}
			else if(is1GBO) {
				if(is2USER) {
					return deactivateAux(txn,key2);
				}
			}
			else if(is1USER) {
				return Response.status(Status.BAD_REQUEST).entity("Not authorized.").build();
			}
			
			else {return Response.status(Status.BAD_REQUEST).entity("Not authorized.").build();}
			
			
			} 
			
			return Response.status(Status.BAD_REQUEST).entity("Not authorized.").build();
			
		} finally {
			if(txn.isActive()) {
				txn.rollback();
			}
		}
		
	}
	
	
	@GET
	@Path("/{username}")
	public Response getState(@PathParam("username") String username) {
		Key key = datastore.newKeyFactory().setKind("User").newKey(username);
		Transaction txn = datastore.newTransaction();
		try {
			String state = txn.get(key).getString("state");
			return Response.status(Status.OK).entity(username + " is " + state).build();

		} finally {
			if (txn.isActive())
				txn.rollback();
		}
	}
	
	

}
