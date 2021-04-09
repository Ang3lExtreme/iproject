package pt.unl.fct.di.apdc.individualproject.resources;


import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import pt.unl.fct.di.apdc.individualproject.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * @author Frederico Luz 51162
 * * INFO: Change user attributes
 *
 */
@Path("/changeatr")
public class ChangeAtr {
    private static final Logger LOG = Logger.getLogger(ChangeAtr.class.getName());
    private final Gson g = new Gson();
    public static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    /**
     * * INFO: Method to change user attributes
     *
     * //this param 'tokenchanges' is a fusion between the token and the changes made on the user
     * @param tokenchanges
     * @return
     */
    @PUT
    @Path("/v1/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response changeAtr(TokenPlusChanges tokenchanges,@PathParam("username") String username) {
        LOG.fine("Register attempt by user: " + tokenchanges.username);
        AuthToken token = new AuthToken(tokenchanges.tokenID, tokenchanges.username, tokenchanges.role, tokenchanges.creationData, tokenchanges.expirationData);
        ChangesJson changes = new ChangesJson(tokenchanges.lastpassword, tokenchanges.newpassword, tokenchanges.confirmation, tokenchanges.email, tokenchanges.phone, tokenchanges.profile, tokenchanges.address, tokenchanges.compAddress, tokenchanges.location);

        Verification v = new Verification();
        if (!v.validChanges(changes) && !username.equalsIgnoreCase(token.username))
            return Response.status(Response.Status.FORBIDDEN).entity("Incorrect information").build();

        if (!v.VerifyToken(token)) {
            return Response.status(Response.Status.FORBIDDEN).entity("token expired please login again").build();
        }
        LOG.fine("token is valid" + token.username);

        Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.username);
        Key userAddressKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", token.username))
                .setKind("Address").newKey(token.username);

        Transaction txn = datastore.newTransaction();

        try {
            Entity user = txn.get(userKey);

            if (user != null) {
                if (!user.getValue("user_password").get().equals(DigestUtils.sha512Hex(changes.lastpassword)))
                    return Response.status(Response.Status.FORBIDDEN).entity("Last password incorrect").build();

                user = Entity.newBuilder(userKey)
                        .set("last_time_modified", Timestamp.now())
                        .set("user_password", DigestUtils.sha512Hex(changes.newpassword))
                        .set("user_email", changes.email)
                        .set("user_phone", changes.phone)
                        .set("user_profile", changes.profile)
                        .set("user_role", token.role)
                        .set("user_state", State.ENABLED.toString()).build();
                Entity add = txn.get(userAddressKey);
                if (add != null) {
                    add = Entity.newBuilder(userAddressKey)
                            .set("user_address", changes.address)
                            .set("user_compAddress", changes.compAddress)
                            .set("user_location", changes.location).build();

                }
                txn.put(user, add);
                txn.commit();
                LOG.info("User '" + token.username + "' changed attributes successfully.");
                return Response.ok(g.toJson(changes)).build();
            }
            return Response.status(Response.Status.FORBIDDEN).entity("User dont oi exist " + userKey).build();

        }catch (Exception e){
            txn.rollback();
            LOG.severe(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }
}