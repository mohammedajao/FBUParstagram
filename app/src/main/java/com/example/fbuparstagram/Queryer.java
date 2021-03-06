package com.example.fbuparstagram;

import android.util.Log;

import com.example.fbuparstagram.models.Comment;
import com.example.fbuparstagram.models.Post;
import com.example.fbuparstagram.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

// Top-level singleton to query for various datasets inside our Parse Database
public class Queryer {
    public static final String TAG = Queryer.class.getSimpleName();
    public static final int LOAD_AMOUNT = 20;
    private int mPage = 1;

    private boolean mDescendingOrder = true;

    public interface QueryCallback {
        void done(List data);
        void done(ParseUser user);
        void done(Post post);
        void done(Comment cmt);
    }

    private int mSkipAmount = 20;
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

    public void queryPostsById(final QueryCallback callback, String objectId) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_OBJECT_ID, objectId);
        try {
            Post post = query.getFirst();
            callback.done(post);
        } catch (ParseException e) {
            Log.e(TAG, "Failed to get post by objectId: " + objectId, e);
            e.printStackTrace();
        }
    }

    public void queryCommentsByPostId(final QueryCallback callback, Post post) {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(Comment.KEY_POST);
        query.whereEqualTo(Comment.KEY_POST, post);
        query.include(Comment.KEY_AUTHOR + "." + User.KEY_USN);
        query.include(Comment.KEY_AUTHOR + "." + User.KEY_AVATAR);
        query.setSkip(LOAD_AMOUNT * (mPage - 1));
        query.setLimit(LOAD_AMOUNT);
        if(mDescendingOrder)
            query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Failed to get all posts", e);
                    return;
                }
                callback.done(comments);
            }
        });
    }

}
