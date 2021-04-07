package pt.unl.fct.di.apdc.individualproject.resources;


import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import pt.unl.fct.di.apdc.individualproject.util.AuthToken;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/remove")
@Produces(MediaType.APPLICATION_JSON)
public class RemoveUser {
    private static final Logger LOG = Logger.getLogger(RemoveUser.class.getName());
    private final Gson g = new Gson();
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    public RemoveUser(){

    }

   /* @DELETE
    @Path("/v1")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response removeV1(AuthToken token,String toremove){
        LOG.fine("Remove attempt by user: " + token.username);
        Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.username);

        if(!token.username.equals(toremove) && token.role.equals(Roles.USER.toString())) {
            return Response.status(Response.Status.FORBIDDEN).entity("User cant do this").build();
        }
        else if(!token.username.equals(toremove)){
            Key userToremoveKey = datastore.newKeyFactory().setKind("User").newKey(token.username);
        }
        Transaction txn = datastore.newTransaction();

        try{
           // Entity userKey = datastore.newKeyFactory().setKind("User").newKey(token.username);

        }finally{
            if (txn.isActive()) {
                txn.rollback();
            }
        }
        return null;
    }*/








}
