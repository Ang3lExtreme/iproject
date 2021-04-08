package pt.unl.fct.di.apdc.individualproject.util;

import java.util.UUID;

public class AuthToken {

    public String username;
    public String role;
    public String tokenID;
    public long creationData;
    public long expirationData;

    public AuthToken(){}



    public AuthToken( String id,String username, String role, long creationData, long expirationData) {
        this.tokenID = id;
        this.username = username;
        this.role = role;
        this.creationData = creationData;
        this.expirationData = expirationData;
    }
}