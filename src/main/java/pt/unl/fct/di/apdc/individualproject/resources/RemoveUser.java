package pt.unl.fct.di.apdc.individualproject.resources;


import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import pt.unl.fct.di.apdc.individualproject.util.AuthToken;
import pt.unl.fct.di.apdc.individualproject.util.Verification;

import javax.ws.rs.*;
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

    @POST
    @Path("/v1/{toremove}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response removeV1(@PathParam("toremove") String toremove,AuthToken token){
        LOG.fine("Remove attempt by user: " + token.username);
        Verification verification = new Verification();
        if(toremove.equalsIgnoreCase("ADMIN"))
            return Response.status(Response.Status.FORBIDDEN).entity("Cannot delete ADMIN").build();

        if(!verification.VerifyToken(token)){
            return Response.status(Response.Status.FORBIDDEN).entity("token expired please login again").build();
        }
        LOG.fine("token is valid" + token.username);

        if(!token.username.equals(toremove)){

            if (!verification.VerifyHierarchy(token.role, toremove)) {
                return Response.status(Response.Status.FORBIDDEN).entity("Is not possible to remove user").build();
            }

        }

        Key userToremove = datastore.newKeyFactory().setKind("User").newKey(toremove);
        //deletar automaticamente o endereço do utilizador
        Key userAddress = datastore.newKeyFactory().addAncestor(PathElement.of("User", toremove))
                .setKind("Address").newKey(toremove);

        Transaction txn = datastore.newTransaction();

        try{
            Entity user = txn.get(userToremove);
            if(user != null){
                txn.delete(userToremove,userAddress);
                txn.commit();
                LOG.info("User '" + toremove+ "' removed successfully.");
                return Response.ok("Deleted user successfully").build();
            }
            return Response.status(Response.Status.FORBIDDEN).entity("User dont exist").build();


        }finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }


    }




}
