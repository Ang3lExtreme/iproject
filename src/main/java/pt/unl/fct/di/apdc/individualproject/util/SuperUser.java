package pt.unl.fct.di.apdc.individualproject.util;

public class SuperUser extends Users {

    public SuperUser(String username, String email, String password,String confirmation) {
        super(username, email, password,confirmation);
        super.role = Roles.SU;
    }


}
