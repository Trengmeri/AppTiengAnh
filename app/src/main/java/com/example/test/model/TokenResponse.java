package com.example.test.model;

public class TokenResponse {
    private String access_token;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
//    public String getTokenFromResponse(TokenResponse tokenResponse) {
//        return tokenResponse.getAccess_token(); // Trả về token
//    }
}
