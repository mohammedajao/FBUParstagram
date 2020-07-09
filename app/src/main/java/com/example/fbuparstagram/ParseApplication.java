package com.example.fbuparstagram;

import android.app.Application;
import android.content.res.Resources;

import com.example.fbuparstagram.models.Comment;
import com.example.fbuparstagram.models.Post;
import com.example.fbuparstagram.models.User;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ParseApplication extends Application {
//    public static final String APP_ID = System.getenv("APP_ID");
//    public static final String CLIENT_KEY = System.getenv("MASTER_KEY"); // Use https so android does not complain
//    public static final String SERVER_URL = System.getenv("SERVER_URL");
    @Override
    public void onCreate() {
        super.onCreate();

        Post.registerSubclass(Post.class);
        ParseObject.registerSubclass(Comment.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("fbuparstagram") // should correspond to APP_ID env variable
                .clientKey("73e8bbd5523e90c40bcb5524026b8c90")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://fbuparstagram.herokuapp.com/parse").build());
    }
}
