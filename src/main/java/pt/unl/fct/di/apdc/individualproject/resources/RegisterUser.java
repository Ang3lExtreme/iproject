package pt.unl.fct.di.apdc.individualproject.resources;


import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import org.apache.commons.validator.routines.EmailValidator;

import pt.unl.fct.di.apdc.individualproject.util.Users;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/register")
public class RegisterUser {
    private static final Logger LOG = Logger.getLogger(RegisterUser.class.getName());
    private final Gson g = new Gson();
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    public RegisterUser(){
        createAdmin();
    }

    @POST
    @Path("/init")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAdmin() {
        LOG.fine("Initiating app creating admin");
        Transaction txn = datastore.newTransaction();

        try{
            Key userKey = datastore.newKeyFactory().setKind("User").newKey("admin");
            Entity user = txn.get(userKey);

            if(user != null){
                return Response.status(Status.ACCEPTED).entity("Admin already exist").build();
            }
            else {
                user = Entity.newBuilder(userKey)
                        .set("user_password", DigestUtils.sha512Hex("passfacil")  )
                        .set("user_email", "admin@admin.com")
                        .set("time_stamp", Timestamp.now()).build();
            }
            txn.add(user);
            LOG.fine("Admin created");
            txn.commit();
            return Response.ok("{}").build();
        } finally {
            if(txn.isActive()) {
                txn.rollback();
            }
        }

    }


    @POST
    @Path("/v1")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerV1(Users data){
        LOG.fine("Register attempt by user: "+ data.getUsername());

        if(!validRegistration(data))
            return Response.status(Status.FORBIDDEN).entity("Incorrect information").build();

        Transaction txn = datastore.newTransaction();

        try{
            Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.getUsername());
            Entity user = txn.get(userKey);

            if(user != null){
                txn.rollback();
                return Response.status(Status.BAD_REQUEST).entity("User already exist").build();
            }
            else {
                user = Entity.newBuilder(userKey)
                        .set("user_password", DigestUtils.sha512Hex(data.getPassword())  )
                        .set("user_email", data.getEmail())
                        .set("time_stamp", Timestamp.now()).build();
            }
            txn.add(user);
            LOG.fine("User registered" + data.getUsername());
            txn.commit();
            return Response.ok("{}").build();
        } finally {
            if(txn.isActive()) {
                txn.rollback();
            }
        }

    }

    private boolean validRegistration(Users data) {
        if(data.getPassword() == null || data.getConfirmation() == null || data.getEmail() == null ||
                data.getUsername() == null)
            return false;
        else if(!EmailValidator.getInstance().isValid(data.getEmail()))
            return false;
        else return data.getPassword().equals(data.getConfirmation());
    }


}
