package pt.unl.fct.di.apdc.individualproject.resources;


import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import pt.unl.fct.di.apdc.individualproject.util.AuthToken;
import pt.unl.fct.di.apdc.individualproject.util.ChangesJson;
import pt.unl.fct.di.apdc.individualproject.util.Verification;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/changeatr")
public class ChangeAtr {
    private static final Logger LOG = Logger.getLogger(ChangeAtr.class.getName());
    private final Gson g = new Gson();
    public static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    @PUT
    @Path("/v1/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response changeAtr(AuthToken token, ChangesJson changes){
        LOG.fine("Register attempt by user: "+ token.username);
        Verification v = new Verification();
        if(!v.validRegistration(changes))
            return Response.status(Response.Status.FORBIDDEN).entity("Incorrect information").build();



        if(!v.VerifyToken(token)){
            return Response.status(Response.Status.FORBIDDEN).entity("token expired please login again").build();
        }
        LOG.fine("token is valid" + token.username);

        Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.tokenID);
        Key userAddressKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", token.username))
                .setKind("Address").newKey(token.username);

        Transaction txn = datastore.newTransaction();

        try {
            Entity user = txn.get(userKey);

            if(user != null){
                user = Entity.newBuilder(userKey)
                        .set("user_password", DigestUtils.sha512Hex(changes.password))
                        .set("user_email", changes.email)
                        .set("user_phone", changes.phone)
                        .set("user_profile", changes.profile).build();
            Entity add = txn.get(userAddressKey);
            if(add != null){
                add = Entity.newBuilder(userAddressKey)
                        .set("user_address", changes.address)
                        .set("user_compAddress",changes.compAddress)
                        .set("user_location", changes.location).build();

            }
            txn.put(user,add);
            txn.commit();
                LOG.info("User '" + token.username+ "' changed attributes successfully.");
                return Response.ok("Changed attributes successfully").build();
            }
            return Response.status(Response.Status.FORBIDDEN).entity("User dont exist").build();




        }finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }
}
