package com.example.myyoutube;

public class TokenService {
    private static TokenService instance;
    private String token;

    private TokenService() {
        this.token = null;
    }

    public static synchronized TokenService getInstance() {
        if (instance == null) {
            instance = new TokenService();
        }
        return instance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void clearToken() {
        this.token = null;
    }
}
