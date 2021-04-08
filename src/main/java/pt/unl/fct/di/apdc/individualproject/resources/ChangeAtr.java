package pt.unl.fct.di.apdc.individualproject.resources;


import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import pt.unl.fct.di.apdc.individualproject.util.AuthToken;
import pt.unl.fct.di.apdc.individualproject.util.ChangesJson;
import pt.unl.fct.di.apdc.individualproject.util.TokenPlusChanges;
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
    @Path("/v1")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response changeAtr(TokenPlusChanges tokenchanges){
        AuthToken token = new AuthToken(tokenchanges.tokenID,tokenchanges.username,tokenchanges.role,tokenchanges.creationData,tokenchanges.expirationData);
        ChangesJson changes = new ChangesJson(tokenchanges.lastpassword, tokenchanges.newpassword,tokenchanges.confirmation,tokenchanges.email
        ,tokenchanges.phone,tokenchanges.profile,tokenchanges.address,tokenchanges.compAddress,tokenchanges.location);

        LOG.fine("Register attempt by user: "+ token.username);
        Verification v = new Verification();
        if(!v.validRegistration(changes))
            return Response.status(Response.Status.FORBIDDEN).entity("Incorrect information").build();



        if(!v.VerifyToken(token)){
            return Response.status(Response.Status.FORBIDDEN).entity("token expired please login again").build();
        }
        LOG.fine("token is valid" + token.username);

        Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.username);
        Key userAddressKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", token.username))
                .setKind("Address").newKey(token.username);

        Transaction txn = datastore.newTransaction();

        try {
            Entity user = txn.get(userKey);

            if(user != null){
                if(!user.getValue("user_password").get().equals(DigestUtils.sha512Hex(changes.lastpassword)))
                    return Response.status(Response.Status.FORBIDDEN).entity("Last password incorrect").build();




                user = Entity.newBuilder(userKey)
                        .set("last_time_modified",Timestamp.now())
                        .set("user_password", DigestUtils.sha512Hex(changes.newpassword))
                        .set("user_email", changes.email)
                        .set("user_phone", changes.phone)
                        .set("user_profile", changes.profile)
                        .set("user_role", token.role)
                        .set("user_state", State.ENABLED.toString() ).build();
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
            return Response.status(Response.Status.FORBIDDEN).entity("User dont oi exist " +userKey).build();




        }finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }
}
