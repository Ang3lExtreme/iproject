package pt.unl.fct.di.apdc.individualproject.resources;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import pt.unl.fct.di.apdc.individualproject.util.AuthToken;
import pt.unl.fct.di.apdc.individualproject.util.RunQueries;
import pt.unl.fct.di.apdc.individualproject.util.Verification;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * @author Frederico Luz 51162
 * Class to remove user
 */
@Path("/remove")
@Produces(MediaType.APPLICATION_JSON)
public class RemoveUser {
    private static final Logger LOG = Logger.getLogger(RemoveUser.class.getName());
    private final Gson g = new Gson();
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    public RemoveUser() {

    }

    /**
     * Method to remove user
     * @param toremove
     * @param token
     * @return
     */
    @DELETE
    @Path("/v1/{toremove}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response removeV1(@PathParam("toremove") String toremove, AuthToken token) {
        LOG.fine("Remove attempt by user: " + token.username);
        Verification v = new Verification();
        if (toremove.equalsIgnoreCase("ADMIN"))
            return Response.status(Response.Status.FORBIDDEN).entity("Cannot delete ADMIN").build();

        if (!v.VerifyToken(token)) {
            return Response.status(Response.Status.FORBIDDEN).entity("token expired please login again").build();
        }
        LOG.fine("token is valid" + token.username);

        if (!token.username.equals(toremove)) {

            if (!v.VerifyHierarchy(token.role, toremove)) {
                return Response.status(Response.Status.FORBIDDEN).entity("Is not possible to remove user").build();
            }

        }

        Key userToremoveKey = datastore.newKeyFactory().setKind("User").newKey(toremove.toLowerCase());
        //deletar automaticamente o endereço do utilizador
        Key userAddress = datastore.newKeyFactory().addAncestor(PathElement.of("User", toremove))
                .setKind("Address").newKey(toremove);

        Transaction txn = datastore.newTransaction();

        try {
            Entity user = txn.get(userToremoveKey);
            if (user != null) {

                RunQueries r = new RunQueries();
                r.eliminateLogs(toremove);
                txn.delete(userToremoveKey, userAddress);
                txn.commit();
                LOG.info("User '" + toremove + "' removed successfully.");
                return Response.ok("Deleted user successfully").build();
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