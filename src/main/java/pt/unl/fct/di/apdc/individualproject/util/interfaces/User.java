package pt.unl.fct.di.apdc.individualproject.util.interfaces;

import pt.unl.fct.di.apdc.individualproject.util.Address;


public interface User {
    public String getUsername();

    public String getEmail();

    public String getPassword();
    

    public String getPhone();

    public Address getAddress();

    public String getState();

    public String getProfile();

    public String getConfirmation();
    //Setters


    public void setUsername(String username);

    public void setEmail(String email);

    public void setPassword(String password);

    public void setAddress(Address address);

    public void setPhone(String phone);

    public void setProfile(String profile);
}
