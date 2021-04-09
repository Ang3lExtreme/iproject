package pt.unl.fct.di.apdc.individualproject.resources;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import pt.unl.fct.di.apdc.individualproject.util.AuthToken;
import pt.unl.fct.di.apdc.individualproject.util.RunQueries;
import pt.unl.fct.di.apdc.individualproject.util.State;
import pt.unl.fct.di.apdc.individualproject.util.Verification;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * @author Frederico Luz 51162
 * * INFO: Class to change user state to Disable or Enable
 */
@Path("/changestate")
public class ChangeState {
    private static final Logger LOG = Logger.getLogger(ChangeState.class.getName());
    private final Gson g = new Gson();
    public static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    public ChangeState() {}

    /**
     * * INFO: Method to change user state to Disable or Enable
     * * State must be Disable or Enable
     * @param token
     * @param username
     * @param state
     * @return
     */
    @PUT
    @Path("/v1/{username}/{state}")
    public Response changeState(AuthToken token, @PathParam("username") String username,
                                @PathParam("state") String state) {
        LOG.fine("Change state attempt by user: " + token.username);
        Verification v = new Verification();

        if (!v.VerifyToken(token)) {
            return Response.status(Response.Status.FORBIDDEN).entity("token expired please login again").build();
        }
        LOG.fine("token is valid" + token.username);

        //por questao de segurança nenhum utilizador pode mudar o proprio estado
        if (token.username.equalsIgnoreCase(username))
            return Response.status(Response.Status.FORBIDDEN).entity("Cannot change own state").build();

        if (!v.stateWriteCorrect(state) || !v.VerifyHierarchy(token.role, username)) {
            return Response.status(Response.Status.FORBIDDEN).entity("Is not possible to change user state").build();
        }
        Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
        Transaction txn = datastore.newTransaction();

        try {
            Entity user = txn.get(userKey);
            if (user != null) {

                user = Entity.newBuilder(userKey)
                        .set("user_password", user.getString("user_password"))
                        .set("user_email", user.getString("user_email"))
                        .set("user_phone", user.getString("user_phone"))
                        .set("user_role", user.getString("user_role"))
                        .set("user_state", state.toUpperCase())
                        .set("user_profile", user.getString("user_profile"))
                        .set("last_time_modified", Timestamp.now()).build();

                if (state.equalsIgnoreCase(State.DISABLED.toString())) {
                    RunQueries r = new RunQueries();
                    r.eliminateLogs(username);
                }

                txn.put(user);
                txn.commit();
                LOG.info("User '" + username + "' changed successfully state to " + state);
                return Response.ok("State changed successfully").build();
            }
            return Response.status(Response.Status.FORBIDDEN).entity("User dont exist").build();

        }catch (Exception e){
            txn.rollback();
            LOG.severe(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }  finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

}