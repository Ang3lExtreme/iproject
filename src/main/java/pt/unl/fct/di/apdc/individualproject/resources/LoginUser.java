package pt.unl.fct.di.apdc.individualproject.resources;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;
import pt.unl.fct.di.apdc.individualproject.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * @author Frederico Luz 51162
 * * INFO: Class to make login
 */
@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
public class LoginUser {
    private static final Logger LOG = Logger.getLogger(LoginUser.class.getName());
    private final Gson g = new Gson();
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    public static final long EXPIRATION_TIME = 1000 * 60 * 60 * 2;

    public LoginUser() {

    }

    /**
     * * INFO: Method to make login
     * @param data
     * @param username
     * @return
     */
    @POST
    @Path("/v1/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response loginV1(LoginData data, @PathParam("username") String username) {
        LOG.fine("Login attempt by user: " + data.username);
        Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
        UUID idOne = UUID.randomUUID();
        Key logKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", data.username))
                .setKind("UserTokens").newKey(String.valueOf(idOne));

        Transaction txn = datastore.newTransaction();
        try {
            Entity user = txn.get(userKey);

            if (username.equalsIgnoreCase(data.username) && user != null && !user.getValue("user_state").get().equals(State.DISABLED.toString())) {

                String userRole = user.getString("user_role");

                String hashedPWD = user.getString("user_password");

                long creationData = System.currentTimeMillis();

                if (hashedPWD.equals(DigestUtils.sha512Hex(data.password))) {
                    Entity log = Entity.newBuilder(logKey)
                            .set("username", data.username)
                            .set("role", userRole)
                            .set("token_creation_data", creationData)
                            .set("token_expiration_data", creationData + EXPIRATION_TIME)
                            .build();

                    AuthToken token = new AuthToken(String.valueOf(idOne), data.username, userRole, creationData, creationData + EXPIRATION_TIME);

                    Key userAddressKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", token.username))
                            .setKind("Address").newKey(token.username);
                    Entity address = txn.get(userAddressKey);

                    txn.put(log);
                    txn.commit();

                    List<Users> regUsers = null;
                    if(!userRole.equalsIgnoreCase(Roles.USER.toString())){
                       regUsers = RunQueries.getRegisteredUsers();
                    }

                    Users info = new Users(data.username, user.getString("user_email"), user.getString("user_phone"),
                            user.getString("user_profile"), userRole, address.getString("user_address"), address.getString("user_compAddress"),
                            address.getString("user_location"));

                    LOG.info("User '" + data.username + "' logged in successfully. ");
                    return Response.ok(g.toJson(token) + g.toJson(info) + g.toJson(regUsers)).build();
                }
                return Response.status(Response.Status.FORBIDDEN).entity("Password incorrect").build();
            }
            return Response.status(Response.Status.FORBIDDEN).entity("User dont exist or is disabled").build();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }

    }

}