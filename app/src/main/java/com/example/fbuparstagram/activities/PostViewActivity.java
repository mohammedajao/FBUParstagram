package com.example.fbuparstagram.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fbuparstagram.EndlessRecyclerViewScrollListener;
import com.example.fbuparstagram.Queryer;
import com.example.fbuparstagram.R;
import com.example.fbuparstagram.adapters.CommentsAdapter;
import com.example.fbuparstagram.databinding.ActivityPostViewBinding;
import com.example.fbuparstagram.databinding.ToolbarBinding;
import com.example.fbuparstagram.fragments.ProfileFragment;
import com.example.fbuparstagram.helpers.Util;
import com.example.fbuparstagram.models.Comment;
import com.example.fbuparstagram.models.Post;
import com.example.fbuparstagram.models.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class PostViewActivity extends AppCompatActivity {
    public static final String TAG = PostViewActivity.class.getSimpleName();
    public static final String KEY_POST_ID = "postId";

    private ActivityPostViewBinding mBinding;
    private MenuItem mMiDone;

    private EndlessRecyclerViewScrollListener mEndlessScrollListener;

    private RecyclerView mRvComments;
    private CommentsAdapter mCmtsAdapter;
    private ProgressBar mProgressBar;

    private TextView mTVUsername;
    private TextView mTVUSN;
    private TextView mTVLikeCount;
    private TextView mTVDate;
    private TextView mTVCaption;

    private ImageView mIVAvatar;
    private ImageView mIVContent;

    private ImageView mIVMore;
    private ImageView mIVLike;
    private ImageView mIVComment;
    private ImageView mIVDirect;
    private ImageView mIVBookmark;
    private ImageView mIVAvatar2;
    private TextView mPostBtn;
    private EditText mPostBody;

    private Post mPost;
    private List<Comment> mPostComments;

    Queryer mQueryer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPostViewBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();

        ToolbarBinding toolbarBinding = (ToolbarBinding) mBinding.toolbarMain;
        Toolbar toolbar = toolbarBinding.toolbar;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        Intent intent = getIntent();
        String postId = intent.getStringExtra(KEY_POST_ID);


        mQueryer = Queryer.getInstance();
        mQueryer.queryPostsById(new Queryer.QueryCallback() {
            @Override
            public void done(List data) {

            }

            @Override
            public void done(ParseUser user) {

            }

            @Override
            public void done(Post data) {
                mPost = data;
            }

            @Override
            public void done(Comment cmt) {

            }
        }, postId);

        if(mPost == null) {
            Log.e(TAG, "Failed to get Post from query");
            finish();
        }

        mPostComments = new ArrayList<Comment>();
        if(mPostComments == null) {
            mPostComments = new ArrayList<Comment>();
        }
        mCmtsAdapter = new CommentsAdapter(this,mPostComments);

        mPostComments.clear();
        loadNextDataFromApi(1);

        mRvComments = mBinding.rvComments;
        mProgressBar = mBinding.pbLoading;
        mTVUsername = mBinding.tvUsername;
        mTVCaption = mBinding.tvCaption;
        mTVUSN = mBinding.tvUSN;
        mTVLikeCount = mBinding.tvLikeCount;
        mTVDate = mBinding.tvDate;
        mIVAvatar = mBinding.ivAvatar;
        mIVContent = mBinding.ivContent;
        mIVMore = mBinding.ivMore;
        mIVLike = mBinding.ivLike;
        mIVComment = mBinding.ivComment;
        mIVBookmark = mBinding.ivBookmark;
        mIVAvatar2 = mBinding.ivAvatar2;
        mPostBody = mBinding.etPostBody;
        mPostBtn = mBinding.tvPostBtn;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRvComments.setLayoutManager(linearLayoutManager);
        mRvComments.setAdapter(mCmtsAdapter);

        mEndlessScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page+1);
            }
        };
        // Adds the scroll listener to RecyclerView
        mRvComments.addOnScrollListener(mEndlessScrollListener);
        mRvComments.setItemViewCacheSize(Queryer.LOAD_AMOUNT);
        showProgressBar();

        bindContent();
        setContentView(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.miEditProfileDone:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void bindContent() {
        mTVUsername.setText(mPost.getUser().getUsername());
        mTVCaption.setText(mPost.getBody());
        mTVDate.setText(Util.getRelativeTimeAgo(mPost.getCreatedAt()));
        mTVUSN.setText(mPost.getUser().getUsername());

        List<ParseFile> mediaFiles = mPost.getMedia();
        final List<ParseUser> likes = mPost.getLikes();

        ParseFile avatarFile = mPost.getUser().getParseFile(User.KEY_AVATAR);
        if(avatarFile != null)
            Glide.with(this).load(avatarFile.getUrl()).into(mIVAvatar);
        ParseFile currentUserAvatar = ParseUser.getCurrentUser().getParseFile(User.KEY_AVATAR);
        if(currentUserAvatar != null)
            Glide.with(this).load(currentUserAvatar.getUrl()).into(mIVAvatar2);

        mPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mPostBody.getText().toString().isEmpty()) {
                    final Comment userComment = new Comment();
                    userComment.setBody(mPostBody.getText().toString());
                    userComment.setAuthor(ParseUser.getCurrentUser());
                    userComment.setPost(mPost);
                    mPost.saveInBackground();
                    userComment.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e != null) {
                                e.printStackTrace();
                                return;
                            }
                            mPostComments.add(0, userComment);
                            mCmtsAdapter.notifyItemInserted(0);
                            mRvComments.smoothScrollToPosition(0);
                            mPostBody.clearFocus();
                            mPostBody.setText("");
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        }
                    });
                }
            }
        });

        if(likes.size() == 0) {
            mTVLikeCount.setVisibility(View.GONE);
        } else {
            mTVLikeCount.setVisibility(View.VISIBLE);
            mTVLikeCount.setText("Liked by " + likes.size() + " others");
        }
        if(mediaFiles.size() > 0) {
            Glide.with(this).load(mPost.getMedia().get(0).getUrl()).into(mIVContent);
        }

        mIVLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<ParseUser> postLikes = mPost.getLikes();
                for(int i=0;i<postLikes.size();i++) {
                    if(postLikes.get(i).getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                        postLikes.remove(i);
                        mPost.put("likes", postLikes);
                        mPost.put("likes_count", postLikes.size());
                        mPost.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e != null) {
                                    Log.e(TAG, "Failed to save post on like", e);
                                    return;
                                }
                                mTVLikeCount.setText("Liked by " + (mPost.getLikesCount()) + " others");
                            }
                        });
                        mIVLike.setImageResource(R.drawable.ufi_heart_icon);
                        return;
                    }
                }
                likes.add(ParseUser.getCurrentUser());
                mPost.put("likes", postLikes);
                mPost.put("likes_count", postLikes.size());
                mPost.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null) {
                            Log.e(TAG, "Failed to save post on unlike", e);
                            return;
                        }
                        mTVLikeCount.setText("Liked by " + (mPost.getLikesCount()) + " others");
                    }
                });
                mIVLike.setImageResource(R.drawable.ufi_heart_active);
                if(mPost.getLikesCount() > 0) {
                    mTVLikeCount.setVisibility(View.VISIBLE);
                } else {
                    mTVLikeCount.setVisibility(View.GONE);
                }
            }
        });

        for(int i=0;i<likes.size();i++) {
            if (likes.get(i).getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                mIVLike.setImageResource(R.drawable.ufi_heart_active);
            }
        }
        hideProgressBar();
    }

    private void loadNextDataFromApi(int page) {
        showProgressBar();
        mQueryer.setPage(page);
        mQueryer.queryCommentsByPostId(new Queryer.QueryCallback() {
            @Override
            public void done(List data) {
                if(data.size() > 0)
                    mCmtsAdapter.addAll(data);
                hideProgressBar();
            }

            @Override
            public void done(ParseUser user) {

            }

            @Override
            public void done(Post post) {

            }

            @Override
            public void done(Comment cmt) {

            }
        }, mPost.getObjectId());
        mQueryer.setPage(0);
    }

    private void showProgressBar() {
        if(mProgressBar != null)
            mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        if(mProgressBar != null)
            mProgressBar.setVisibility(View.GONE);
    }
}