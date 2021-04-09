package pt.unl.fct.di.apdc.individualproject.util;

import com.google.cloud.datastore.*;
import com.google.datastore.v1.PropertyFilter;
import com.google.gson.Gson;

import javax.xml.ws.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Frederico Luz 51162
 * * INFO: Addicional Class to run some queries
 */
public class RunQueries {

    private static final Logger LOG = Logger.getLogger(RunQueries.class.getName());
    public static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    public RunQueries() {

    }

    /**
     * INFO: This method select all users registered
     * @return List<Users>
     */
    public static List<Users> getRegisteredUsers() {
        Transaction txn = datastore.newTransaction();
        LOG.info("Eliminating logs...");
        try {

            //SELECT __key__,user_role, user_profile
            //FROM User

            Query < Entity > query = Query.newEntityQueryBuilder()
                    .setKind("User").build();

            QueryResults < Entity > logs = datastore.run(query);
            List<Users> l = new ArrayList<>();
            logs.forEachRemaining(userlog -> {
                Users u = new Users(userlog.getKey().getName(),userlog.getString("user_role"),userlog.getString("user_profile"));
                l.add(u);
            });

            txn.commit();
            return l;
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }


    }

    /**
     * * INFO: Eliminate all logins tokens of a user
     * @param username
     */
    public void eliminateLogs(String username) {
        Transaction txn = datastore.newTransaction();
        LOG.info("Eliminating logs...");
        try {
            Query < Entity > query = Query.newEntityQueryBuilder()
                    .setKind("UserTokens")
                    .setFilter(
                            StructuredQuery.PropertyFilter.eq("username", username)
                    ).build();

            QueryResults < Entity > logs = datastore.run(query);

            logs.forEachRemaining(userlog -> {
                txn.delete(userlog.getKey());
            });

            txn.commit();
        } catch (Exception e){
            txn.rollback();
            LOG.severe(e.getMessage());
        }finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }
}