package pt.unl.fct.di.apdc.individualproject.util;

import pt.unl.fct.di.apdc.individualproject.resources.Roles;

import java.util.UUID;

public class AuthToken {
    public static final long EXPIRATION_TIME = 1000 * 60 * 60 * 2; // 2h
    public String username;
    public String role;
    public String tokenID;
    public long creationData;
    public long expirationData;

    public AuthToken(String username,String role) {
        this.username = username;
        this.role = role;
        this.tokenID = UUID.randomUUID().toString();
        this.creationData = System.currentTimeMillis();
        this.expirationData = this.creationData + AuthToken.EXPIRATION_TIME;
    }
}