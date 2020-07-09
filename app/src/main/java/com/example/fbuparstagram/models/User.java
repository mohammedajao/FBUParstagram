package com.example.fbuparstagram.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("User")
public class User extends ParseUser {
    public static final String KEY_USN = "username";
    public static final String KEY_BIO = "bio";
    public static final String KEY_AVATAR = "avatar";
    public static final String KEY_LIKES = "likes";
    public User(){}

    public  String getUsername(){
        return getString(KEY_USN);
    }
    public void setUsername(String username){
        put(KEY_USN,username);
    }

    public  String getBio(){
        return getString("bio");
    }
    public void setBio(String bio){
        put(KEY_BIO,bio);
    }
    public ParseFile getAvatar(){
        return getParseFile("avatar");
    }
    public void setAvatar(ParseFile avatar){
        put(KEY_AVATAR,avatar);
    }

    public List<Post> getLikes() {
        return getList("likes");
    }
    public void setLikes(List<Post> likes) {
        put(KEY_LIKES, likes);
    }

    public static User newUserFromParseUser(ParseUser oldUser) {
        User user = new User();
        user.setUsername(oldUser.getUsername());

        return user;
    }
}