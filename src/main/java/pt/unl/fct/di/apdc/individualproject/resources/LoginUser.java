package pt.unl.fct.di.apdc.individualproject.resources;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;
import pt.unl.fct.di.apdc.individualproject.util.AuthToken;
import pt.unl.fct.di.apdc.individualproject.util.LoginData;
import pt.unl.fct.di.apdc.individualproject.util.RoleJson;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;
import java.util.logging.Logger;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
public class LoginUser {
    private static final Logger LOG = Logger.getLogger(LoginUser.class.getName());
    private final Gson g = new Gson();
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    public static final long EXPIRATION_TIME = 1000 * 60 * 60 * 2;

    public LoginUser(){

    }

    @POST
    @Path("/v1")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response loginV1(LoginData data){
        LOG.fine("Login attempt by user: " + data.username);
        Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
        UUID idOne = UUID.randomUUID();
        Key logKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", data.username))
                .setKind("UserTokens").newKey(String.valueOf(idOne));

        Transaction txn = datastore.newTransaction();
        try {
            Entity user = txn.get(userKey);

            if (user != null) {

                String userRole = g.toJson(user.getValue("user_role"));
                RoleJson role = g.fromJson(userRole, RoleJson.class);


                String hashedPWD = user.getString("user_password");

                long creationData = System.currentTimeMillis();

                if (hashedPWD.equals(DigestUtils.sha512Hex(data.password))) {
                    Entity log = Entity.newBuilder(logKey)
                            .set("username", data.username)
                            .set("role", role.value)
                            .set("token_creation_data",creationData)
                            .set("token_expiration_data", creationData+EXPIRATION_TIME)
                            .build();


                AuthToken token = new AuthToken(String.valueOf(idOne),data.username, role.value, creationData,creationData+EXPIRATION_TIME  );

                    txn.put(log);
                    txn.commit();



                    LOG.info("User '" + data.username + "' logged in successfully. " + logKey);
                    return Response.ok(g.toJson(token)).build();
                }
                return Response.status(Response.Status.FORBIDDEN).entity("Password incorrect").build();
            }
            return Response.status(Response.Status.FORBIDDEN).entity("User dont exist").build();
        }

        finally{
            if (txn.isActive()) {
                txn.rollback();
            }
        }

    }



}
