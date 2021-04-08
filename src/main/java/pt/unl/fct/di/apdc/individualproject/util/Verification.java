package pt.unl.fct.di.apdc.individualproject.util;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import pt.unl.fct.di.apdc.individualproject.resources.Roles;


import java.util.logging.Logger;

public class Verification {
    private static final Logger LOG = Logger.getLogger(Verification.class.getName());
    private final Gson g = new Gson();
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    public Verification(){}

    public boolean VerifyHierarchy(String userrole, String toremove) {
        LOG.fine("Verifying Hierarchy");

        Key toremoveKey = datastore.newKeyFactory().setKind("User").newKey(toremove);

        Transaction txn = datastore.newTransaction();

        try {
            Entity toremoveuser = txn.get(toremoveKey);

            if (toremoveuser != null) {
                String toRemoveRole = g.toJson(toremoveuser.getValue("user_role"));
                //role do utilizador a remover
                RoleJson role = g.fromJson(toRemoveRole, RoleJson.class);

                //nao podem ser da mesma role
                if (userrole.equals(role.value)) {
                    return false;
                }
                //SU > TODOS
                else if (userrole.equals(Roles.SU.toString()))
                    return true;
                    //GA > GBO > USER
                else if (userrole.equals(Roles.GA.toString()) && (role.value.equals(Roles.GBO.toString()) || role.value.equals(Roles.USER.toString())))
                    return true;
                    //GA > USER
                else return userrole.equals(Roles.GBO.toString()) && role.value.equals(Roles.USER.toString());

            }
            LOG.warning("User dont exist");
            return false;

        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }


    public boolean VerifyToken(AuthToken token){
        LOG.info("Verifying token");
        Key tokenkey = datastore.newKeyFactory().addAncestor(PathElement.of("User", token.username))
                .setKind("UserTokens").newKey(token.tokenID);
        //Key(User, 'admin', UserTokens, 5716561121771520)
        Transaction txn = datastore.newTransaction();

        try{
            Entity t = txn.get(tokenkey);
            if(t != null){
                //verify if token has ended his time
                if(token.expirationData < System.currentTimeMillis()){
                    LOG.warning("token expired" + " exp=" + token.expirationData + " curr=" + System.currentTimeMillis());
                    txn.delete(tokenkey);
                    txn.commit();
                    return false;
                }

                return true;

            }
            LOG.warning("user dont exist " + tokenkey);
            return false;
        }finally{
            if (txn.isActive()) {
                txn.rollback();
            }
        }

    }
}
