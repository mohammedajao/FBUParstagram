package com.example.fbuparstagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.fbuparstagram.R;
import com.example.fbuparstagram.databinding.FragmentPostDetailViewBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostDetailViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostDetailViewFragment extends Fragment {

    public static final String TAG = PostDetailViewFragment.class.getSimpleName();
    private FragmentPostDetailViewBinding mBinding;
    private MenuItem mMiActionProgress;

    private ProgressBar mProgressBar;

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostDetailViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostDetailViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostDetailViewFragment newInstance(String param1, String param2) {
        PostDetailViewFragment fragment = new PostDetailViewFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentPostDetailViewBinding.inflate(inflater,container,false);
        View view = mBinding.getRoot();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
        mMiActionProgress = menu.findItem(R.id.miActionProgress);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar = mBinding.pbLoading;
        mTVUsername = mBinding.tvUsername;
        mTVCaption = mBinding.tvCaption;
        mTVUSN = mBinding.tvUSN;
        mTVCommentBtn = mBinding.tvCommentBtn;
        mTVLikeCount = mBinding.tvLikeCount;
        mTVDate = mBinding.tvDate;
        mIVAvatar = mBinding.ivAvatar;
        mIVContent = mBinding.ivContent;
        mIVMore = mBinding.ivMore;
        mIVLike = mBinding.ivLike;
        mIVComment = mBinding.ivComment;
        mIVBookmark = mBinding.ivBookmark;
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