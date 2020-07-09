package com.example.fbuparstagram;

import android.util.Log;

import com.example.fbuparstagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

public class Queryer {
    public static final String TAG = Queryer.class.getSimpleName();
    public static final int LOAD_AMOUNT = 20;
    private int mPage = 1;

    private boolean mDescendingOrder = true;

    public interface QueryCallback {
        public void done(List data);
        public void done(ParseUser user);
    }

    private int mSkipAmount = 10;
    private static Queryer singleton;

    public static Queryer getInstance() {
        if(singleton == null) {
            singleton = new Queryer();
        }
        return singleton;
    }

    public void setPage(int pageNum) {
        mPage = pageNum;
    }

    public void setDescendingOrder(boolean toDescend) {
        mDescendingOrder = toDescend;
    }

    public void queryPosts(final QueryCallback callback, ParseUser user) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        if(user != null)
            query.whereEqualTo(Post.KEY_USER, user);
        query.include(Post.KEY_USER);
        query.setSkip(mSkipAmount * (mPage - 1));
        query.setLimit(LOAD_AMOUNT);
        if(mDescendingOrder)
            query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Failed to get all posts", e);
                    return;
                }
                callback.done(posts);
            }
        });
    }

}
