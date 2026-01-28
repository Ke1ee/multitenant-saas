package com.kelee.multitenantsaas.config;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String email) {
        super("User not found: " + email);
    }
}