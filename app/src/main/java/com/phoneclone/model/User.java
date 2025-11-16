package com.phoneclone.model;

public class User {
    private Long id;
    private String username;
    private String email;
    private String membershipLevel;
    
    public User() {
    }
    
    public User(Long id, String username, String email, String membershipLevel) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.membershipLevel = membershipLevel;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getMembershipLevel() {
        return membershipLevel;
    }
    
    public void setMembershipLevel(String membershipLevel) {
        this.membershipLevel = membershipLevel;
    }
}

