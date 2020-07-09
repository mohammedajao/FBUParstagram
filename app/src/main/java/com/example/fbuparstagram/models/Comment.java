package com.example.fbuparstagram.models;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@ParseClassName("Comment")
public class Comment extends ParseObject {
    public static final String TAG = Comment.class.getSimpleName();
    public static final String KEY_POST = "post";
    public static final String KEY_BODY = "body";
    public static final String KEY_AUTHOR = "user";
    public Comment() {}

    public String getBody() { return getString(KEY_BODY); }
    public void setBody(String body) { put(KEY_BODY, body); }

    public ParseUser getAuthor() {
        return getParseUser(KEY_AUTHOR);
    }
    public void setAuthor(ParseUser author) { put(KEY_AUTHOR, author); }

    public ParseObject getPost() { return getParseObject(KEY_POST); }
    public void setPost(Post post) { put(KEY_POST, post); }
}
