package pt.unl.fct.di.apdc.individualproject.util;

public class Address {

    private String address = "";
    private String compAddress = "";
    private String location = "";


    //default constr
    public Address(){

    }


    public Address(String address,String compAddress, String location){
        this.address = address;
        this.compAddress = compAddress;
        this.location = location;
    }


    //Getters
    public String getAddress() {
        return address;
    }

    public String getCompAddress() {
        return compAddress;
    }

    public String getLocation() {
        return location;
    }

    //Setters
    public void setAddress(String address) {
        this.address = address;
    }

    public void setCompAddress(String compAddress) {
        this.compAddress = compAddress;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
