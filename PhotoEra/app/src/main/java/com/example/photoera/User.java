package com.example.photoera;

public class User {
    private String name;
    private String email;
    private String birth;
    private String password;
    private String profile;

    public User(String name, String email, String birth, String password, String profile){
        this.name=name;
        this.email=email;
        this.birth=birth;
        this.password=password;
        this.profile=profile;
    }

    public void setName(String name){
        this.name=name;
    }

    public void setEmail(String email){
        this.email=email;
    }

    public void setBirth(String birth){
        this.birth=birth;
    }

    public void setPassword(String password){
        this.password=password;
    }

    public void setProfile(String profile){
        this.profile=profile;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public String getBirth(){
        return birth;
    }

    public String getPassword(){
        return password;
    }

    public String getProfile(){
        return profile;
    }
}
