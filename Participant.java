package com.example.localloop;

public class Participant extends LoginUser {
    public Participant(String username, String password) {
        super(username, password, "admin");
    }
}