package io.iwa.instamojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * //TODO Add Class Description
 * Author: harshit  on 28/2/16.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class User {
    String email;
    String name;
    String phoneNumber;

    public User(String email) {
        this.email = email;
    }
    public User(){

    }

    public User(String email, String name, String phoneNumber) {
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    @JsonIgnore
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @JsonIgnore
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "User{email='"+email+"', name='"+name+"',phone='"+phoneNumber+"'}";
    }
}
