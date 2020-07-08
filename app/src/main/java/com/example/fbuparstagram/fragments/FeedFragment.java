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
import android.widget.ProgressBar;

import com.example.fbuparstagram.EndlessRecyclerViewScrollListener;
import com.example.fbuparstagram.activities.MainActivity;
import com.example.fbuparstagram.adapters.PostsAdapter;
import com.example.fbuparstagram.R;
import com.example.fbuparstagram.databinding.FragmentFeedBinding;
import com.example.fbuparstagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {
    public static final String TAG = FeedFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentFeedBinding mBinding;
    private SwipeRefreshLayout mSwipeContainer;
    protected RecyclerView rvPosts;
    protected PostsAdapter mAdapter;
    protected List<Post> mAllPosts;
    protected EndlessRecyclerViewScrollListener mEndlessScrollListener;

    private MenuItem mMiActionProgress;
    protected ProgressBar mProgressBar;

    private int mSkipAmount = 20;
    protected int mPage = 1;

    public FeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAllPosts = new ArrayList<>();
        mAdapter = new PostsAdapter(getContext(), mAllPosts);
        mProgressBar = mBinding.pbLoading;
        rvPosts = view.findViewById(R.id.rvPosts);
        rvPosts.setAdapter(mAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvPosts.setLayoutManager(linearLayoutManager);
        mEndlessScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        mSwipeContainer = mBinding.swipeContainer;
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync(0);
            }
        });
        queryPosts();
    }

    private void fetchTimelineAsync(int page) {
        mPage = page;
        mAdapter.clear();
        queryPosts();
        mSwipeContainer.setRefreshing(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        mBinding = FragmentFeedBinding.inflate(inflater,container,false);
        View view = mBinding.getRoot();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
        mMiActionProgress = menu.findItem(R.id.miActionProgress);
    }

    public void queryPosts() {
        showProgressBar();
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setSkip(mSkipAmount * (mPage - 1));
        query.setLimit(mSkipAmount);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Failed to get all posts", e);
                    return;
                }
                for(Post post : posts) {
                    Log.i("POSTS:", post.getMedia().toString());
                }
                mAllPosts.addAll(posts);
                mAdapter.notifyDataSetChanged();
                hideProgressBar();
            }
        });
    }

    public void loadNextDataFromApi(int offset) {
        mPage = offset;
        Log.i(TAG, ""+offset);
        queryPosts();
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
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