package pt.unl.fct.di.apdc.individualproject.resources;


import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import pt.unl.fct.di.apdc.individualproject.util.AuthToken;
import pt.unl.fct.di.apdc.individualproject.util.Verification;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Path("/logout")
public class LogOut {
    private static final Logger LOG = Logger.getLogger(ChangeRole.class.getName());
    private final Gson g = new Gson();
    public static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    public LogOut(){}


    @DELETE
    @Path("/v1/{username}")
    public Response logOut(AuthToken token, @PathParam("username") String username){
        LOG.fine("Log out attempt by user: " + token.username);

        if(!token.username.equalsIgnoreCase(username))
            return Response.status(Response.Status.FORBIDDEN).entity("Cannot log out").build();
        Verification v = new Verification();

        if(!v.VerifyToken(token)){
            return Response.status(Response.Status.FORBIDDEN).entity("token expired please login again").build();
        }
        LOG.fine("token is valid" + token.username);



        //percorrer todos os tokens do user e eliminar
        Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);

        Transaction txn = datastore.newTransaction();

        try{
            Entity user = txn.get(userKey);
            if(user != null) {
                /**
                 * SELECT * FROM
                 * UserTokens
                 * WHERE username = 'admin'
                 */
            Query<Entity> query = Query.newEntityQueryBuilder()
                    .setKind("UserTokens")
                    .setFilter(
                            PropertyFilter.eq("username",username)
                    ).build();


            QueryResults<Entity> logs = datastore.run(query);

                List<Key> loginKeys = new ArrayList();
                logs.forEachRemaining(userlog -> {
                    loginKeys.add(userlog.getKey());
                });

                for(Key k: loginKeys){
                    LOG.info("deleted key " + k.toString());
                    txn.delete(k);

                }
                txn.commit();
                return Response.ok("Logout successfully").build();


            }
            return Response.status(Response.Status.FORBIDDEN).entity("User dont exist").build();

        }finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }


    }
}