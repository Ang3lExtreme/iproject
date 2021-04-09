package pt.unl.fct.di.apdc.individualproject.util;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import org.apache.commons.validator.routines.EmailValidator;


import java.util.logging.Logger;

public class Verification {
    private static final Logger LOG = Logger.getLogger(Verification.class.getName());
    private final Gson g = new Gson();
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    public Verification(){}

    public boolean VerifyHierarchy(String userrole, String otheruser) {
        LOG.fine("Verifying Hierarchy");

        Key otherUserKey = datastore.newKeyFactory().setKind("User").newKey(otheruser);

        Transaction txn = datastore.newTransaction();

        try {
            Entity toremoveuser = txn.get(otherUserKey);

            if (toremoveuser != null) {
                String otherUserRole = (String) toremoveuser.getValue("user_role").get();
                //role do utilizador a remover


                //nao podem ser da mesma role
                if (userrole.equals(otherUserRole)) {
                    return false;
                }
                //SU > TODOS
                else if (userrole.equals(Roles.SU.toString()))
                    return true;
                    //GA > GBO > USER
                else if (userrole.equals(Roles.GA.toString()) && (otherUserRole.equals(Roles.GBO.toString()) || otherUserRole.equals(Roles.USER.toString())))
                    return true;
                    //GA > USER
                else return userrole.equals(Roles.GBO.toString()) && otherUserRole.equals(Roles.USER.toString());

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
            LOG.warning("user kk dont exist " + tokenkey);
            return false;
        }finally{
            if (txn.isActive()) {
                txn.rollback();
            }
        }

    }

    public boolean validRegistration(Users data) {
        if(data.password == null || data.confirmation == null || data.email == null ||
                data.username == null)
            return false;
        else if(!EmailValidator.getInstance().isValid(data.email))
            return false;
        else return data.password.equals(data.confirmation);
    }
    public boolean validRegistration(ChangesJson data) {
        if(data.lastpassword == null || data.newpassword == null|| data.confirmation == null || data.email == null)
            return false;
        else if(!EmailValidator.getInstance().isValid(data.email))
            return false;
        else if(!data.profile.equals(Prof.PUB.toString()) || data.profile.equals(Prof.PRIVY.toString()))
            return false;
        else return data.newpassword.equals(data.confirmation);
    }


    public boolean roleWriteCorrect(String newrole) {
        return (newrole.equalsIgnoreCase(Roles.GBO.toString())
        || newrole.equalsIgnoreCase(Roles.GA.toString())
        || newrole.equalsIgnoreCase(Roles.USER.toString())
        || newrole.equalsIgnoreCase(Roles.SU.toString()));

    }

    public boolean stateWriteCorrect(String state) {
        return (state.equalsIgnoreCase(State.ENABLED.toString())
        || state.equalsIgnoreCase(State.DISABLED.toString()));
    }
}
