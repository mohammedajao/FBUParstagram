package com.example.fbuparstagram.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fbuparstagram.Queryer;
import com.example.fbuparstagram.R;
import com.example.fbuparstagram.activities.LoginActivity;
import com.example.fbuparstagram.activities.ProfileEditActivity;
import com.example.fbuparstagram.adapters.ProfileGridViewAdapter;
import com.example.fbuparstagram.databinding.FragmentProfileBinding;
import com.example.fbuparstagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    public static final String TAG = ProfileFragment.class.getSimpleName();

    public interface OnClickListener {
        public void onClick(int position);
    }

    private FragmentProfileBinding mBinding;
    private ParseUser mUser;

    private MenuItem mMiActionProgress;

    private GridView mGVPosts;

    private TextView mUsername;
    private TextView mDescription;

    private Button mEditProfile;
    private Button mLogOut;

    private ImageView mIVAvatar;

    private List<Post> mAllPosts;
    private ProfileGridViewAdapter mGVAdapter;

    protected OnClickListener onClickListener;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
            String username = getArguments().getString("USER_TARGET");
//            if(username == ParseUser.getCurrentUser().getUsername()) {
//                mUser = ParseUser.getCurrentUser();
//                return;
//            }
            ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
            query.whereEqualTo("username", username);
            try {
                mUser = query.getFirst();
            } catch (ParseException e) {
                e.printStackTrace();
            }
//            query.findInBackground(new FindCallback<ParseUser>() {
//                @Override
//                public void done(List<ParseUser> returnedUser, ParseException e) {
//                    if(e != null) {
//                        Log.e(TAG, "Failed to find user", e);
//                    } else {
//                        mUser = returnedUser.get(0);
//                    }
//                }
//            });
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        mBinding = FragmentProfileBinding.inflate(inflater,container,false);
        View view = mBinding.getRoot();
        mAllPosts = new ArrayList<Post>();
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
        mGVPosts = mBinding.gvPosts;
        mUsername = mBinding.tvName;
        mDescription = mBinding.tvDescription;
        mEditProfile = mBinding.btnEditProfile;
        mLogOut = mBinding.btnLogOut;
        mIVAvatar = mBinding.ivAvatar;
        // Set to user
        mUsername.setText(mUser.getUsername());
        mDescription.setText(mUser.getString("bio"));

        if(mUser.equals(ParseUser.getCurrentUser()))
            mLogOut.setVisibility(View.GONE);

        onClickListener = new OnClickListener() {
            @Override
            public void onClick(int position) {
                FragmentManager manager = getFragmentManager();
                Fragment frag = new ProfilePostsFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("ITEM_POSITION", position);
                bundle.putString("USER_TARGET", getArguments().getString("USER_TARGET"));
                frag.setArguments(bundle);
                manager.beginTransaction().replace(R.id.fragmentContainer, frag).commit();
            }
        };

        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
                startActivity(intent);
            }
        });

        mGVAdapter = new ProfileGridViewAdapter(getContext(), mAllPosts,mGVPosts,onClickListener);
        mGVPosts.setAdapter(mGVAdapter);
        mLogOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        ParseFile userAvatar = mUser.getParseFile("avatar");
        if(userAvatar != null)
            Glide.with(getContext()).load(userAvatar.getUrl()).into(mIVAvatar);
        queryPosts();
    }


    public void queryPosts() {
        showProgressBar();
        Queryer query = Queryer.getInstance();
        query.queryPosts(new Queryer.QueryCallback() {
            @Override
            public void done(List data) {
                mAllPosts.addAll(data);
                mGVAdapter.notifyDataSetChanged();
                hideProgressBar();
            }

            @Override
            public void done(ParseUser user) {

            }
        }, mUser);
    }

    public void showProgressBar() {
        if(mMiActionProgress != null)
            mMiActionProgress.setVisible(true);
        mBinding.pbLoading.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        if(mMiActionProgress != null)
            mMiActionProgress.setVisible(false);
        mBinding.pbLoading.setVisibility(View.GONE);
    }
}