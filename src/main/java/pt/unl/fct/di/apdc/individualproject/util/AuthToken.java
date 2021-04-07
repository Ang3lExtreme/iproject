package pt.unl.fct.di.apdc.individualproject.util;

import java.util.UUID;

public class AuthToken {
    private static final String SERVER = "APDC2021";
    public static final long EXPIRATION_TIME = 1000 * 60 * 60 * 2; // 2h
    public String username;
    public String role;
    public String tokenID;
    public long creationData;
    public long expirationData;

    public AuthToken(String username,String role) {
        this.username = username;
        this.role = role;
        this.tokenID = UUID.randomUUID().toString()+ SERVER;
        this.creationData = System.currentTimeMillis();
        this.expirationData = this.creationData + AuthToken.EXPIRATION_TIME;
    }
}