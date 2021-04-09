package pt.unl.fct.di.apdc.individualproject.util;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import org.apache.commons.validator.routines.EmailValidator;


import java.util.logging.Logger;

/**
 * @author Frederico Luz 51162
 * * INFO: This Class is made for usuals verifications
 */
public class Verification {
    private static final Logger LOG = Logger.getLogger(Verification.class.getName());
    private final Gson g = new Gson();
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    public Verification(){}

    /**
     * INFO: Verify hierarchy between users
     * PS: Can be optimized
     * @param userrole
     * @param otheruser
     * @return
     */
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

    /**
     * INFO: Verify is token is valid
     * @param token
     * @return
     */
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

    /**
     * INFO: Verify if registration data is valid
     * @param data
     * @return
     */
    public boolean validRegistration(Users data) {
        if(data.password == null || data.confirmation == null || data.email == null ||
                data.username == null)
            return false;
        else if(!EmailValidator.getInstance().isValid(data.email))
            return false;
        else return data.password.equals(data.confirmation);
    }
    
    /**
     * INFO: Verify if changes data is valid
     * @param data
     * @return
     */
    public boolean validChanges(ChangesJson data) {
        if(data.lastpassword == null || data.newpassword == null|| data.confirmation == null || data.email == null)
            return false;
        else if(!EmailValidator.getInstance().isValid(data.email))
            return false;
        else if(!data.profile.equalsIgnoreCase(Prof.PUB.toString()) && !data.profile.equalsIgnoreCase(Prof.PRIV.toString()))
            return false;
        else return data.newpassword.equals(data.confirmation);
    }

    /**
     * INFO: Verify if role is wrote correctly
     * @param newrole
     * @return
     */
    public boolean roleWriteCorrect(String newrole) {
        return (newrole.equalsIgnoreCase(Roles.GBO.toString())
        || newrole.equalsIgnoreCase(Roles.GA.toString())
        || newrole.equalsIgnoreCase(Roles.USER.toString())
        || newrole.equalsIgnoreCase(Roles.SU.toString()));

    }

    /**
     * INFO: Verify if state is wrote valid
     * @param state
     * @return
     */
    public boolean stateWriteCorrect(String state) {
        return (state.equalsIgnoreCase(State.ENABLED.toString())
        || state.equalsIgnoreCase(State.DISABLED.toString()));
    }
}
