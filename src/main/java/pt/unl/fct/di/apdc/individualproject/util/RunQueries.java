package pt.unl.fct.di.apdc.individualproject.util;

import com.google.cloud.datastore.*;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RunQueries {

    private static final Logger LOG = Logger.getLogger(RunQueries.class.getName());
    public static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    public RunQueries() {

    }


    public void eliminateLogs(String username) {
        Transaction txn = datastore.newTransaction();

        try {
            Query<Entity> query = Query.newEntityQueryBuilder()
                    .setKind("UserTokens")
                    .setFilter(
                            StructuredQuery.PropertyFilter.eq("username", username)
                    ).build();


            QueryResults<Entity> logs = datastore.run(query);

            List<Key> loginKeys = new ArrayList();
            logs.forEachRemaining(userlog -> {
                loginKeys.add(userlog.getKey());
            });

            for (Key k : loginKeys) {
                LOG.info("deleted key " + k.toString());
                txn.delete(k);

            }
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }
}
