package pt.unl.fct.di.apdc.individualproject.resources;


import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import org.apache.commons.validator.routines.EmailValidator;

import pt.unl.fct.di.apdc.individualproject.util.Users;
import pt.unl.fct.di.apdc.individualproject.util.Verification;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;


enum State {
    ENABLED,
    DISABLED
}

enum Prof {
    PUB, PRIVY
}

@Path("/register")
public class RegisterUser {
    private static final Logger LOG = Logger.getLogger(RegisterUser.class.getName());
    private final Gson g = new Gson();
    public static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    public RegisterUser(){

    }


    @POST
    @Path("/v1")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerV1(Users data){
        LOG.fine("Register attempt by user: "+ data.username);
        Verification v = new Verification();
        if(!v.validRegistration(data))
            return Response.status(Status.FORBIDDEN).entity("Incorrect information").build();

        Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
        Key addressKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", data.username))
                .setKind("Address").newKey(data.username);

        Transaction txn = datastore.newTransaction();

        try{
            Entity user = txn.get(userKey);
            Entity address;
            if(user != null){
                txn.rollback();
                return Response.status(Status.BAD_REQUEST).entity("User already exist").build();
            }
            else {

                Roles role;
                if(data.username.equalsIgnoreCase("ADMIN")){
                   role = Roles.SU;
                }
                else{
                    role = Roles.USER;
                }

                user = Entity.newBuilder(userKey)
                            .set("user_password", DigestUtils.sha512Hex(data.password))
                            .set("user_email", data.email)
                            .set("user_phone", "")
                            .set("user_role", role.toString())
                            .set("user_state", State.ENABLED.toString())
                            .set("user_profile", Prof.PUB.toString())
                            .set("time_stamp", Timestamp.now()).build();

                address = Entity.newBuilder(addressKey)
                        .set("user_address", "")
                        .set("user_compAddress","")
                        .set("user_location", "").build();
            }

            txn.add(user,address);
            LOG.fine("User registered" + data.username);
            txn.commit();
            return Response.ok("{}").build();
        } finally {
            if(txn.isActive()) {
                txn.rollback();
            }
        }

    }




}
