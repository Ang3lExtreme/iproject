package pt.unl.fct.di.apdc.individualproject.resources;

import pt.unl.fct.di.apdc.individualproject.util.Users;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;


/**
 * @author Frederico Neves
 * CLass to init server creating admin SU (Bootstrap User)
 */
@WebListener
public class StartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        Users u = new Users("admin","fc.luz@fct.unl.pt","password","password");

        RegisterUser.registerV1(u, "admin");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // Perform action during application's shutdown
    }
}
