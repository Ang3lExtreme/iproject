package pt.unl.fct.di.apdc.individualproject.util;

/**
 * @author Frederico Luz 51162
 * * INFO: This class make a fusion between token info and changes info, but it can be optimized on future
 */
public class TokenPlusChanges {
    public String username;
    public String role;
    public String tokenID;
    public long creationData;
    public long expirationData;
    public String email;
    public String lastpassword;
    public String newpassword;
    public String confirmation;
    public String profile;
    public String address;
    public String compAddress;
    public String location;
    public String phone;


    public TokenPlusChanges(){}

    /**
     * * INFO: Constructor
     * @param username
     * @param role
     * @param tokenID
     * @param creationData
     * @param expirationData
     * @param email
     * @param lastpassword
     * @param newpassword
     * @param confirmation
     * @param profile
     * @param address
     * @param compAddress
     * @param location
     * @param phone
     */
    public TokenPlusChanges(String username, String role, String tokenID,long creationData,
                            long expirationData, String email,String lastpassword,String newpassword,String confirmation,
                            String profile, String address, String compAddress, String location,
                            String phone){
        this.username = username;
        this.role = role;
        this.tokenID = tokenID;
        this.creationData = creationData;
        this.expirationData = expirationData;
        this.lastpassword = lastpassword;
        this.newpassword = newpassword;
        this.confirmation = confirmation;
        this.email = email;
        this.phone = phone;
        this.profile = profile;
        this.address = address;
        this.compAddress = compAddress;
        this.location = location;



    }

}
