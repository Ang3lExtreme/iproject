package pt.unl.fct.di.apdc.individualproject.resources;


import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;
import pt.unl.fct.di.apdc.individualproject.util.Users;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

@Path("/init")
public class BootstrapUser {
    private static final Logger LOG = Logger.getLogger(BootstrapUser.class.getName());
    private final Gson g = new Gson();
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    public BootstrapUser(){

    }

    @POST
    @Path("/v")
    @Consumes(MediaType.APPLICATION_JSON)
    public void registerAdmin(){
        Users user = new Users("ADMIN","fc.luz@campus.fct.unl", DigestUtils.sha512Hex("adminpass"),
                DigestUtils.sha512Hex("adminpass"));
        //RegisterUser.registerV1(user);
    }

}
