package pt.unl.fct.di.apdc.firstwebapp.resources;
import pt.unl.fct.di.apdc.firstwebapp.util.*;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.*;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

@Path("/list")
@Produces(MediaType.APPLICATION_JSON)
public class ListResource {

	private static final Logger LOG = Logger.getLogger(RoleResource.class.getName()); 
	private final Datastore datastore = DatastoreOptions.newBuilder().setProjectId("firm-dimension-379414").build().getService();
	
	public ListResource() {}
	
	@POST
	@Path("/v1")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response list(LoginData data) {
		
		LOG.fine("Attempt to list users.");
		
		Key key = datastore.newKeyFactory().setKind("User").newKey(data.username);
		
		String roleUser = datastore.get(key).getString("user_role");
		
		boolean isSU = roleUser.equalsIgnoreCase(Role.SU.toString());
		boolean isGS = roleUser.equalsIgnoreCase(Role.GS.toString());
		boolean isGBO = roleUser.equalsIgnoreCase(Role.GBO.toString());
		boolean isUSER = roleUser.equalsIgnoreCase(Role.USER.toString());
		
		boolean isActive = datastore.get(key).getString("state").equalsIgnoreCase(State.ACTIVE.toString());
		
		if(isActive) {
		
		if(isSU) {
			
			Query<Entity> querySUuser =
					Query.newEntityQueryBuilder().setKind("User").setFilter(PropertyFilter.eq("user_role", Role.USER.toString())).build();
			QueryResults<Entity> uResultsSU = datastore.run(querySUuser);
			
			List<ListData> listSu = new ArrayList<>();
			
			while(uResultsSU.hasNext()) {
				Entity result = uResultsSU.next();
				listSu.add(new ListData(result.getString("id"), result.getString("user_name"), result.getString("user_email"),
						result.getString("user_profile"), result.getString("user_role"), result.getString("state")));
			}
			
			Query<Entity> querySUgbo =
					Query.newEntityQueryBuilder().setKind("User").setFilter(PropertyFilter.eq("user_role", Role.GBO.toString())).build();
			QueryResults<Entity> gboResultsSU = datastore.run(querySUgbo);
			
			while(gboResultsSU.hasNext()) {
				Entity result = gboResultsSU.next();
				listSu.add(new ListData(result.getString("id"), result.getString("user_name"), result.getString("user_email"),
						result.getString("user_profile"), result.getString("user_role"), result.getString("state")));
			}
			
			Query<Entity> querySUga =
					Query.newEntityQueryBuilder().setKind("User").setFilter(PropertyFilter.eq("user_role", Role.GA.toString())).build();
			QueryResults<Entity> gaResultsSU = datastore.run(querySUga);
			
			while(gaResultsSU.hasNext()) {
				Entity result = gaResultsSU.next();
				listSu.add(new ListData(result.getString("id"), result.getString("user_name"), result.getString("user_email"),
						result.getString("user_profile"), result.getString("user_role"), result.getString("state")));
			}
			
			Query<Entity> querySUgs =
					Query.newEntityQueryBuilder().setKind("User").setFilter(PropertyFilter.eq("user_role", Role.GS.toString())).build();
			QueryResults<Entity> gsResultsSU = datastore.run(querySUgs);
			
			while(gsResultsSU.hasNext()) {
				Entity result = gsResultsSU.next();
				listSu.add(new ListData(result.getString("id"), result.getString("user_name"), result.getString("user_email"),
						result.getString("user_profile"), result.getString("user_role"), result.getString("state")));
			}
			
			return Response.status(Status.OK).entity(listSu).build();
			
		}
		
		else if(isGS) {
			
			Query<Entity> queryGSuser =
					Query.newEntityQueryBuilder().setKind("User").setFilter(PropertyFilter.eq("user_role", Role.USER.toString())).build();
			QueryResults<Entity> uResultsGS = datastore.run(queryGSuser);
			List<ListData> listGs = new ArrayList<>();
			
			while(uResultsGS.hasNext()) {
				Entity result = uResultsGS.next();
				listGs.add(new ListData(result.getString("id"), result.getString("user_name"), result.getString("user_email"),
						result.getString("user_profile"), result.getString("user_role"), result.getString("state")));
			}
			
			Query<Entity> queryGSgbo =
					Query.newEntityQueryBuilder().setKind("User").setFilter(PropertyFilter.eq("user_role", Role.GBO.toString())).build();
			QueryResults<Entity> gboResultsGS = datastore.run(queryGSgbo);
			
			while(gboResultsGS.hasNext()) {
				Entity result = gboResultsGS.next();
				listGs.add(new ListData(result.getString("id"), result.getString("user_name"), result.getString("user_email"),
						result.getString("user_profile"), result.getString("user_role"), result.getString("state")));
			}
			
			return Response.status(Status.OK).entity(listGs).build();
		}
		
		else if(isGBO) {
			
			Query<Entity> queryGBOuser =
					Query.newEntityQueryBuilder().setKind("User").setFilter(PropertyFilter.eq("user_role", Role.USER.toString())).build();
			QueryResults<Entity> uResultsGBO = datastore.run(queryGBOuser);
			List<ListData> listGbo = new ArrayList<>();
			
			while(uResultsGBO.hasNext()) {
				Entity result = uResultsGBO.next();
				listGbo.add(new ListData(result.getString("id"), result.getString("user_name"), result.getString("user_email"),
						result.getString("user_profile"), result.getString("user_role"), result.getString("state")));
			}
			
			return Response.status(Status.OK).entity(listGbo).build();
		}
		
		else if(isUSER) {
			
			Query<Entity> queryUser =
				      Query.newEntityQueryBuilder().setKind("User").setFilter(CompositeFilter.and(PropertyFilter.eq("user_role", Role.USER.toString()),
				    		  PropertyFilter.eq("state", State.ACTIVE.toString()), PropertyFilter.eq("user_profile", "public"))).build();
			QueryResults<Entity> resultsUSER = datastore.run(queryUser);
			List<ListUserData> listUser = new ArrayList<>();
			
			
			while(resultsUSER.hasNext()) {
				Entity result = resultsUSER.next();
				
				listUser.add(new ListUserData(result.getString("id"), result.getString("user_email"), result.getString("user_name")));
			}
			
		
			
			return Response.status(Status.OK).entity(listUser).build();
			
		}
		
		}
		
		return Response.status(Status.BAD_REQUEST).entity("Listing not allowed.").build();
	}
	
	
}
