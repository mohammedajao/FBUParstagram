package com.example.fbuparstagram.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.List;

@ParseClassName("User")
public class User extends ParseObject {
    public static final String KEY_USERNAME = "username";
    public static final String KEY_LIKES = "likes";
    public static final String KEY_AVATAR = "avatar";

    public List<String> getLikedPosts() { return getList(KEY_LIKES); }
    public void setLikes(List<String> postIds) { put(KEY_LIKES, postIds); }

    public ParseFile getAvatar() { return getParseFile(KEY_AVATAR); }
    public void setAvatar(ParseFile file) { put(KEY_AVATAR, file); }

    public String getUsername() { return getString(KEY_USERNAME); }
}
