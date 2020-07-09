package com.example.fbuparstagram.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbuparstagram.Queryer;
import com.example.fbuparstagram.R;
import com.example.fbuparstagram.activities.PostViewActivity;
import com.example.fbuparstagram.fragments.ProfileFragment;
import com.example.fbuparstagram.helpers.Util;
import com.example.fbuparstagram.models.Post;
import com.example.fbuparstagram.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    public static final String TAG = PostsAdapter.class.getSimpleName();

    private Context mContext;
    private List<Post> mPosts;
    private FragmentManager mFragmentManager;

    public PostsAdapter(Context context, List<Post> posts, FragmentManager fragmentManager) {
        this.mContext = context;
        this.mPosts = posts;
        this.mFragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public void addAll(List<Post> list) {
        mPosts.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTVUsername;
        private TextView mTVUSN;
        private TextView mTVCommentBtn;
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

        private boolean mLiked = false;
        private int mUserPosition = -1;

        public ViewHolder(@NonNull View view) {
            super(view);
            mTVUsername = view.findViewById(R.id.tvUsername);
            mTVCaption = view.findViewById(R.id.tvCaption);
            mTVUSN = view.findViewById(R.id.tvUSN);
            mTVCommentBtn = view.findViewById(R.id.tvCommentBtn);
            mTVLikeCount = view.findViewById(R.id.tvLikeCount);
            mTVDate = view.findViewById(R.id.tvDate);
            mIVAvatar = view.findViewById(R.id.ivAvatar);
            mIVContent = view.findViewById(R.id.ivContent);
            mIVMore = view.findViewById(R.id.ivMore);
            mIVLike = view.findViewById(R.id.ivLike);
            mIVComment = view.findViewById(R.id.ivComment);
            mIVBookmark = view.findViewById(R.id.ivBookmark);
        }

        private void displayPostView(String postId) {
            Intent intent = new Intent(mContext, PostViewActivity.class);
            intent.putExtra(PostViewActivity.KEY_POST_ID, postId);
            mContext.startActivity(intent);
        }

        public void bind(final Post post) {
            final List<ParseUser> likes = post.getLikes();
            mIVLike.clearColorFilter();
            mLiked = false;
            for(int i=0;i<likes.size();i++) {
                if (likes.get(i).getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                    mIVLike.setImageResource(R.drawable.ufi_heart_active);
                    mLiked = true;
                    break;
                }
            }
            mTVCaption.setText(post.getBody());
            mTVUsername.setText(post.getUser().getUsername());
            mTVUSN.setText(post.getUser().getUsername());
            mTVDate.setText(Util.getRelativeTimeAgo(post.getCreatedAt()));
            List<ParseFile> mediaFiles = post.getMedia();
            if(likes.size() == 0) {
                mTVLikeCount.setVisibility(View.GONE);
            } else {
                mTVLikeCount.setVisibility(View.VISIBLE);
                mTVLikeCount.setText("Liked by " + likes.size() + " others");
            }
            if(mediaFiles.size() > 0) {
                Glide.with(mContext).load(post.getMedia().get(0).getUrl()).into(mIVContent);
            }

            ParseFile file = post.getUser().getParseFile("avatar");
            if(file != null)
                Glide.with(mContext).load(file.getUrl()).into(mIVAvatar);

            mTVCommentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayPostView(post.getObjectId());
                }
            });

            mIVComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayPostView(post.getObjectId());
                }
            });
            mIVAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProfileFragment profileFrag = new ProfileFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("USER_TARGET", post.getUser().getUsername());
                    profileFrag.setArguments(bundle);
                    mFragmentManager.beginTransaction().replace(R.id.fragmentContainer, profileFrag).commit();
                }
            });

            mIVLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<ParseUser> postLikes = post.getLikes();
                    if(mUserPosition != -1)
                        return;
                    if(mLiked) {
                        for(int i=0;i<postLikes.size();i++) {
                            if(postLikes.get(i).getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                                postLikes.remove(i);
                            }
                        }
//                       ParseUser.getCurrentUser().put("likes", likedPosts);
                       post.put("likes", postLikes);
                       post.put("likes_count", postLikes.size());
                       post.saveInBackground(new SaveCallback() {
                           @Override
                           public void done(ParseException e) {
                               if(e != null) {
                                   Log.e(TAG, "Failed to save post on like", e);
                                   return;
                               }
                               mTVLikeCount.setText("Liked by " + (post.getLikesCount()) + " others");
                           }
                       });
                        mLiked = false;
                        mIVLike.setImageResource(R.drawable.ufi_heart_icon);
                    } else {
                        likes.add(ParseUser.getCurrentUser());
                        post.put("likes", postLikes);
                        post.put("likes_count", postLikes.size());
                        post.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e != null) {
                                    Log.e(TAG, "Failed to save post on unlike", e);
                                    return;
                                }
                                mTVLikeCount.setText("Liked by " + (post.getLikesCount()) + " others");
                            }
                        });
                        mLiked = true;
                        mIVLike.setImageResource(R.drawable.ufi_heart_active);
                    }
                    if(post.getLikesCount() > 0) {
                        mTVLikeCount.setVisibility(View.VISIBLE);
                    } else {
                        mTVLikeCount.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
}
