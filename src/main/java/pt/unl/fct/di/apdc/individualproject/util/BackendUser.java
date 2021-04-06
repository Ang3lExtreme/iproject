package pt.unl.fct.di.apdc.individualproject.util;

public class BackendUser extends Users{


    public BackendUser(String username, String email, String password, String confirmation) {
        super(username, email, password, confirmation);
        super.role = Roles.GA;
    }
}
