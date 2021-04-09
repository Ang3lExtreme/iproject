package pt.unl.fct.di.apdc.individualproject.resources;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import pt.unl.fct.di.apdc.individualproject.util.AuthToken;
import pt.unl.fct.di.apdc.individualproject.util.Roles;
import pt.unl.fct.di.apdc.individualproject.util.Verification;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/changerole")
public class ChangeRole {
    private static final Logger LOG = Logger.getLogger(ChangeRole.class.getName());
    private final Gson g = new Gson();
    public static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();


    public ChangeRole(){}


    @PUT
    @Path("/v1/{username}/{newrole}")
    public Response changeRole(AuthToken token, @PathParam("username") String username, @PathParam("newrole") String newrole){
        LOG.fine("Change role attempt by user: " + token.username);
        Verification v = new Verification();

        if(!v.VerifyToken(token)){
            return Response.status(Response.Status.FORBIDDEN).entity("token expired please login again").build();
        }
        LOG.fine("token is valid" + token.username);
        if(token.username.equalsIgnoreCase(username))
            return Response.status(Response.Status.FORBIDDEN).entity("Cannot change own role").build();

        if (!v.roleWriteCorrect(newrole) || !v.VerifyHierarchy(token.role, username)) {
            return Response.status(Response.Status.FORBIDDEN).entity("Is not possible to change user role").build();
        }

        Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);

        Transaction txn = datastore.newTransaction();

        try{
            Entity user = txn.get(userKey);
            if(user != null){

                if(ValidRole(token.role,newrole)){
                    user = Entity.newBuilder(userKey)
                            .set("user_password", user.getValue("user_password").get().toString())
                            .set("user_email", user.getValue("user_email").get().toString())
                            .set("user_phone", user.getValue("user_phone").get().toString())
                            .set("user_role", newrole.toUpperCase())
                            .set("user_state", user.getValue("user_state").get().toString())
                            .set("user_profile", user.getValue("user_profile").get().toString())
                            .set("last_time_modified", Timestamp.now()).build();

                txn.put(user);
                txn.commit();
                LOG.info("User '" + username+ "' changed successfully role to " + newrole);
                return Response.ok("Role changed successfully").build();
                }
                return Response.status(Response.Status.FORBIDDEN).entity("Role Cannot be changed").build();
            }
            return Response.status(Response.Status.FORBIDDEN).entity("User dont exist").build();


        }finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }

    }

    private boolean ValidRole(String modifierRole, String newrole) {
            if(modifierRole.equals(Roles.SU.toString()))
                return true;
            if(modifierRole.equals(Roles.GA.toString()) && newrole.equals(Roles.SU.toString()))
                return false;
            if(modifierRole.equals(Roles.GBO.toString()) && (newrole.equals(Roles.SU.toString()) || newrole.equals(Roles.GA.toString())))
                return false;
            else return false;



    }

}
