package pt.unl.fct.di.apdc.individualproject.util;


/**
 * @author Frederico Luz 51162
 * * INFO: User data for login or other stuffs
 */
public class Users  {
    public String username;
    public String email;
    public String password;
    public String confirmation;
    public String role;
    public String profile;
    public String address;
    public String compAddress;
    public String location;
    public String phone;

    public Users(){}

    /**
     * INFO: Constructor
     * this constructor is used when i want to see user info after login
     * @param username
     * @param email
     * @param phone
     * @param role
     * @param profile
     * @param address
     * @param compAddress
     * @param location
     */
    public Users(String username, String email, String phone,String role,String profile
            ,String address, String compAddress, String location){
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.profile = profile;
        this.address = address;
        this.compAddress = compAddress;
        this.location = location;
        this.role = role;



    }

    /**
     * INFO: Constructor
     * This constructor is used to register user
     * @param username
     * @param email
     * @param password
     * @param confirmation
     */
    public Users(String username, String email, String password, String confirmation){
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmation = confirmation;


    }

    /**
     * INFO:
     * This constructor is used to list users
     * @param username
     * @param role
     * @param profile
     */
    public Users(String username, String role, String profile){
        this.username = username;
        this.role = role;
        this.profile = profile;
    }


}
