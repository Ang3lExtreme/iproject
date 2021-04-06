package pt.unl.fct.di.apdc.individualproject.resources;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;
import pt.unl.fct.di.apdc.individualproject.util.AuthToken;
import pt.unl.fct.di.apdc.individualproject.util.LoginData;
import pt.unl.fct.di.apdc.individualproject.util.Users;
import pt.unl.fct.di.apdc.individualproject.util.UsersJson;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/login")
public class LoginUser {
    private static final Logger LOG = Logger.getLogger(LoginUser.class.getName());
    private final Gson g = new Gson();
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();


    public LoginUser(){

    }

    @POST
    @Path("/v1")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginV1(LoginData data, @Context HttpServletRequest request, @Context HttpHeaders headers){
        LOG.fine("Login attempt by user: " + data.username);
        Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
        Key ctrsKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", data.username))
                .setKind("UserStats").newKey("counters");
        Key logKey = datastore.allocateId(datastore.newKeyFactory().addAncestor(PathElement.of("User", data.username))
                .setKind("UserLog").newKey());

        Transaction txn = datastore.newTransaction();
        try {
            Entity user = txn.get(userKey);
            if (user != null) {


                Entity stats = txn.get(ctrsKey);
                if (stats == null) {
                    stats = Entity.newBuilder(ctrsKey)
                            .set("user_stats_logins", 0L)
                            .set("user_stats_failed", 0L)
                            .set("user_first_login", Timestamp.now())
                            .set("user_last_login", Timestamp.now())
                            .build();
                }

                String hashedPWD = user.getString("user_password");

                if (hashedPWD.equals(DigestUtils.sha512Hex(data.password))) {
                    Entity log = Entity.newBuilder(logKey)
                            .set("user_login_ip", request.getRemoteAddr())
                            .set("user_login_host", request.getRemoteAddr())
                            .set("user_login_latlong", StringValue.newBuilder(headers.getHeaderString("X-AppEngine-CityLatLong"))
                                    .setExcludeFromIndexes(true).build())
                            .set("user_login_city", headers.getHeaderString("X-AppEngine-City"))
                            .set("user_login_country", headers.getHeaderString("X-AppEngine-Country"))
                            .set("user_login_time", Timestamp.now())
                            .build();


                    Entity ustats = Entity.newBuilder(ctrsKey)
                            .set("user_stats_logins", 1L + stats.getLong("user_stats_logins"))
                            .set("user_stats_failed", 0L)
                            .set("user_first_login", stats.getTimestamp("user_first_login"))
                            .set("user_last_login", Timestamp.now())
                            .build();

                    txn.put(log, ustats);
                    txn.commit();

                    Query<Entity> query = Query.newEntityQueryBuilder()
                            .setKind("User")
                            .setFilter(PropertyFilter.eq("__key__",data.username)).build();

                    QueryResults<Entity> result = datastore.run(query);
                    String json = g.toJson(result);
                    UsersJson ruser = new Gson().fromJson(json,UsersJson.class);

                    AuthToken token = new AuthToken(data.username, ruser.user_role);
                    LOG.info("User '" + data.username + "' logged in successfully.");
                    return Response.ok(g.toJson(token)).build();
                }
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
