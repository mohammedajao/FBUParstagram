package com.example.fbuparstagram.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_BODY = "body";
    public static final String KEY_MEDIA = "media";
    public static final String KEY_USER = "user";

    public String getBody() {
        return getString(KEY_BODY);
    }

    public void setBody(String body) {
        put(KEY_BODY, body);
    }

    public List<ParseFile> getMedia() {
        return getList(KEY_MEDIA);
    }

    public void setMedia(List<ParseFile> media) {
        put(KEY_MEDIA, media);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }
}
