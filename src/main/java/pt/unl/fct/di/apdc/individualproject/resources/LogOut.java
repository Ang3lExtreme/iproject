package pt.unl.fct.di.apdc.individualproject.resources;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import pt.unl.fct.di.apdc.individualproject.util.AuthToken;
import pt.unl.fct.di.apdc.individualproject.util.RunQueries;
import pt.unl.fct.di.apdc.individualproject.util.Verification;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * @author Frederico Luz 51162
 * * INFO: Class for logout
 *
 */
@Path("/logout")
public class LogOut {
    private static final Logger LOG = Logger.getLogger(ChangeRole.class.getName());
    private final Gson g = new Gson();
    public static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    public LogOut() {}

    /**
     * * INFO: Method for logout
     * @param token
     * @param username
     * @return
     */
    @DELETE
    @Path("/v1/{username}")
    public Response logOut(AuthToken token, @PathParam("username") String username) {
        LOG.fine("Log out attempt by user: " + token.username);

        if (!token.username.equalsIgnoreCase(username))
            return Response.status(Response.Status.FORBIDDEN).entity("Cannot log out").build();
        Verification v = new Verification();

        if (!v.VerifyToken(token)) {
            return Response.status(Response.Status.FORBIDDEN).entity("token expired please login again").build();
        }
        LOG.fine("token is valid" + token.username);

        //percorrer todos os tokens do user e eliminar
        Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);

        Transaction txn = datastore.newTransaction();

        try {
            Entity user = txn.get(userKey);
            if (user != null) {

                RunQueries r = new RunQueries();
                r.eliminateLogs(username);
                txn.commit();
                return Response.ok("Logout successfully").build();

            }
            return Response.status(Response.Status.FORBIDDEN).entity("User dont exist").build();

        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }

    }
}