package com.example.fbuparstagram.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbuparstagram.customui.SquareImageView;
import com.example.fbuparstagram.fragments.ProfileFragment;
import com.example.fbuparstagram.models.Post;

import java.util.List;

public class ProfileGridViewAdapter extends BaseAdapter {
    public static final String TAG = ProfileGridViewAdapter.class.getSimpleName();
    private List<Post> mPosts;
    private Context mContext;
    private GridView mGVPosts;
    private ProfileFragment.OnClickListener mOnClickListener;

    public ProfileGridViewAdapter(Context context, List<Post> posts, GridView gv, ProfileFragment.OnClickListener onClickListener) {
        mPosts = posts;
        mContext = context;
        mGVPosts = gv;
        mOnClickListener = onClickListener;
    }

    @Override
    public int getCount() {
        return mPosts.size();
    }

    @Override
    public Post getItem(int i) {
        return mPosts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return Long.parseLong(mPosts.get(i).getObjectId(), 16);
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        SquareImageView imageView;
        if(view == null) {
            imageView = new SquareImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
            imageView.setVisibility(View.VISIBLE);
            imageView.setMaxHeight(mGVPosts.getColumnWidth());
        } else {
            imageView = (SquareImageView) view;
        }
        Glide.with(mContext).load(mPosts.get(i).getMedia().get(0).getUrl()).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickListener.onClick(i);
            }
        });
        return imageView;
    }
}
