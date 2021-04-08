package pt.unl.fct.di.apdc.individualproject.util;

import pt.unl.fct.di.apdc.individualproject.resources.ChangeAtr;

public class ChangesJson {
    public String email;
    public String lastpassword;
    public String newpassword;
    public String confirmation;
    public String profile;
    public String address;
    public String compAddress;
    public String location;
    public String phone;

    public ChangesJson(){

    }

    public ChangesJson(String lastpassword,String newpassword,String confirmation,String email,String phone ,String profile
    ,String address, String compAddress, String location){
        this.lastpassword = lastpassword;
        this.newpassword = newpassword;
        this.confirmation = confirmation;
        this.confirmation = confirmation;
        this.email = email;
        this.phone = phone;
        this.profile = profile;
        this.address = address;
        this.compAddress = compAddress;
        this.location = location;
    }
}
