package com.example.fbuparstagram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbuparstagram.models.Post;
import com.parse.ParseFile;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    private Context mContext;
    private List<Post> mPosts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.mContext = context;
        this.mPosts = posts;
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

        public void bind(Post post) {
            mTVCaption.setText(post.getBody());
            mTVUsername.setText(post.getUser().getUsername());
            mTVUSN.setText(post.getUser().getUsername());
            List<ParseFile> mediaFiles = post.getMedia();
            if(mediaFiles.size() > 0)
                Glide.with(mContext).load(post.getMedia().get(0).getUrl()).into(mIVContent);
        }
    }
}
