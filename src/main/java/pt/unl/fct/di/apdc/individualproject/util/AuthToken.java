package pt.unl.fct.di.apdc.individualproject.util;

import java.util.UUID;

/**
 * @author Frederico Luz 51162
 * * INFO: Token Class
 */
public class AuthToken {

    public String username;
    public String role;
    public String tokenID;
    public long creationData;
    public long expirationData;

    public AuthToken(){}


    /**
     * * Constructor
     *
     * @param id
     * @param username
     * @param role
     * @param creationData
     * @param expirationData
     */
    public AuthToken( String id,String username, String role, long creationData, long expirationData) {
        this.tokenID = id;
        this.username = username;
        this.role = role;
        this.creationData = creationData;
        this.expirationData = expirationData;
    }
}