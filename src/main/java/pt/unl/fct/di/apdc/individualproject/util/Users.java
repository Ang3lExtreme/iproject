package pt.unl.fct.di.apdc.individualproject.util;

import pt.unl.fct.di.apdc.individualproject.util.interfaces.User;


enum Roles {
    USER,
    GBO,
    GA,
    SU
        }
enum State {
    ENABLED,
    DISABLED
}

enum Prof {
    PUB, PRIVY
}


public class Users implements User {
    private String username;
    private String email;
    private String password;
    private String confirmation;
    private String phone;
    private Address address;
    public Roles role = Roles.USER;
    public State state =  State.ENABLED;
    private Prof profile = Prof.PUB;

    public Users(String username, String email, String password, String confirmation){
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmation = confirmation;
        this.phone = "";
        this.address = new Address();

    }

    //Getters


    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public Address getAddress(){
        return address;
    }

    public String getState(){
        return state.toString();
    }

    public String getProfile(){
        return profile.toString();
    }

    public String getConfirmation() {
        return confirmation;
    }

    //Setters


    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public void setProfile(String profile) {
        if (profile.toUpperCase().equals(Prof.PUB.toString()))
            this.profile = Prof.PUB;
        else this.profile = Prof.PRIVY;
    }
}
