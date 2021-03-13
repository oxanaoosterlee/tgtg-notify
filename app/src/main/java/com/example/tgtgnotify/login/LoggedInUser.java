package com.example.tgtgnotify.login;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String displayName;
    private boolean loggedIn;
    private String error;

    public LoggedInUser(String userId, String displayName) {
        this.userId = userId;
        this.displayName = displayName;

        this.loggedIn = false;
        this.error = "";
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isLoggedIn(){
        return loggedIn;
    }
    public void setLoggedIn(boolean value){this.loggedIn = value;}

    public String getError(){return this.error;}
    public void setError(String error){this.error = error;}
}