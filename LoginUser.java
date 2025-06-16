package com.example.localloop;

public class LoginUser {
    protected String username;
    protected String password;
    protected String role;

    public LoginUser(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getRole() { return role; }
}
