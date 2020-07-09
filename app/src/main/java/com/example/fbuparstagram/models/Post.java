package com.example.fbuparstagram.models;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_BODY = "body";
    public static final String KEY_MEDIA = "media";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_LIKES = "likes";
    public static final String KEY_LIKES_COUNT = "likes_count";

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

    public List<ParseUser> getLikes() { return getList(KEY_LIKES); }

    public void setLikes(List<String> likes) { put(KEY_LIKES, likes); }

    public int getLikesCount() { return getInt(KEY_LIKES_COUNT); }

    public void setLikesCount(int numOfLikes) { put(KEY_LIKES_COUNT, numOfLikes); }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public static String getRelativeTimeAgo(Date createdAt) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(createdAt.toString()).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
