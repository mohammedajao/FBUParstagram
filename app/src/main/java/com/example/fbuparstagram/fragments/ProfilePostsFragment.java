package com.example.fbuparstagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.fbuparstagram.EndlessRecyclerViewScrollListener;
import com.example.fbuparstagram.R;
import com.example.fbuparstagram.adapters.PostsAdapter;
import com.example.fbuparstagram.databinding.FragmentFeedBinding;
import com.example.fbuparstagram.databinding.FragmentProfilePostsBinding;
import com.example.fbuparstagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfilePostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfilePostsFragment extends FeedFragment {
    public static final String TAG = ProfilePostsFragment.class.getSimpleName();
    public static final String ARG_ITEM_POSITION ="ITEM_POSITION";

    private int mSkipAmount = 20;
    private int mItemPosition = 0;
    private FragmentProfilePostsBinding mBinding;

    private MenuItem mMiActionProgress;

    public ProfilePostsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mItemPosition = getArguments().getInt(ARG_ITEM_POSITION);
            Log.i(TAG, mItemPosition + "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPosts.scrollToPosition(mItemPosition);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        mMiActionProgress = menu.findItem(R.id.miActionProgress);
    }

    public void queryPosts() {
        showProgressBar();
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.setSkip(mSkipAmount * (mPage-1));
        query.setLimit(mSkipAmount);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Failed to get all posts", e);
                    return;
                }
                Log.i(TAG, ""+posts.size());
                mAllPosts.addAll(posts);
                mAdapter.notifyDataSetChanged();
                hideProgressBar();
            }
        });
    }

    public void showProgressBar() {
        if(mMiActionProgress != null)
            mMiActionProgress.setVisible(true);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        if(mMiActionProgress != null)
            mMiActionProgress.setVisible(false);
        mProgressBar.setVisibility(View.GONE);
    }
}