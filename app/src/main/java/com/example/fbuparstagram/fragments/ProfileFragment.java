package com.example.fbuparstagram.fragments;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fbuparstagram.R;
import com.example.fbuparstagram.activities.LoginActivity;
import com.example.fbuparstagram.adapters.ProfileGridViewAdapter;
import com.example.fbuparstagram.databinding.FragmentProfileBinding;
import com.example.fbuparstagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
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

    private GridView mGVPosts;

    private TextView mUsername;
    private TextView mDescription;

    private Button mEditProfile;
    private Button mLogOut;

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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentProfileBinding.inflate(inflater,container,false);
        View view = mBinding.getRoot();
        mAllPosts = new ArrayList<Post>();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUser = ParseUser.getCurrentUser();
        mGVPosts = mBinding.gvPosts;
        mUsername = mBinding.tvName;
        mDescription = mBinding.tvDescription;
        mEditProfile = mBinding.btnEditProfile;
        mLogOut = mBinding.btnLogOut;
        // Set to user
        mUsername.setText(mUser.getUsername());
        // TODO Get description user set from Parse
        mDescription.setText("");
        onClickListener = new OnClickListener() {
            @Override
            public void onClick(int position) {
                FragmentManager manager = getFragmentManager();
                Fragment frag = new ProfilePostsFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("ITEM_POSITION", position);
                frag.setArguments(bundle);
                manager.beginTransaction().replace(R.id.fragmentContainer, frag).commit();
            }
        };

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

        queryPosts();
    }

    public void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(20);
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
                mGVAdapter.notifyDataSetChanged();
            }
        });
    }
}