package com.example.fbuparstagram.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbuparstagram.Queryer;
import com.example.fbuparstagram.R;
import com.example.fbuparstagram.helpers.Util;
import com.example.fbuparstagram.models.Comment;
import com.example.fbuparstagram.models.Post;
import com.example.fbuparstagram.models.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    public static final String TAG = CommentsAdapter.class.getSimpleName();

    private Context mContext;

    private TextView mTVUsername;
    private TextView mTVBody;
    private TextView mTVDate;

    private ImageView mIVAvatar;

    private List<Comment> mComments;
    public CommentsAdapter(Context context, List<Comment> comments) {
        mContext = context;
        mComments = comments;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = mComments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTVUsername = itemView.findViewById(R.id.tvUsername);
            mTVBody = itemView.findViewById(R.id.tvBody);
            mTVDate = itemView.findViewById(R.id.tvDate);
            mIVAvatar = itemView.findViewById(R.id.ivAvatar);
        }

        public void bind(Comment comment) {
            ParseUser cmtUser = comment.getAuthor();
            ParseFile file = cmtUser.getParseFile(User.KEY_AVATAR);
            if (file != null)
                Glide.with(mContext).load(file.getUrl()).into(mIVAvatar);
            mTVUsername.setText(cmtUser.getUsername());
            mTVBody.setText(comment.getBody());
            mTVDate.setText(Util.getRelativeTimeAgo(comment.getCreatedAt()));
        }
    }
}
