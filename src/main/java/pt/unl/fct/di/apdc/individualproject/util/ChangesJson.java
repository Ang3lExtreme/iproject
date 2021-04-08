package pt.unl.fct.di.apdc.individualproject.util;

import pt.unl.fct.di.apdc.individualproject.resources.ChangeAtr;

public class ChangesJson {
    public String email;
    public String password;
    public String confirmation;
    public String profile;
    public String address;
    public String compAddress;
    public String location;
    public String phone;

    public ChangesJson(){

    }

    public ChangesJson(String password, String confirmation,String email,String phone ,String profile
    ,String address, String compAddress, String location){
       this.password = password;
       this.confirmation = confirmation;
       this.email = email;
       this.phone = phone;
       this.profile = profile;
       this.address = address;
       this.compAddress = compAddress;
       this.location = location;
    }
}
