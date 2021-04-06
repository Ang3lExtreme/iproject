package pt.unl.fct.di.apdc.individualproject.util;

public class BackOfficeUser extends  Users{

    public BackOfficeUser(String username, String email, String password, String confirmation) {
        super(username, email, password, confirmation);
        super.role = Roles.GBO;
    }
}
