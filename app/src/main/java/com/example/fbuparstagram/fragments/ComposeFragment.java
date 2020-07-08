package com.example.fbuparstagram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fbuparstagram.R;
import com.example.fbuparstagram.databinding.FragmentComposeBinding;
import com.example.fbuparstagram.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComposeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeFragment extends Fragment {
    public static final String TAG = ComposeFragment.class.getSimpleName();
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;

    private File mPhotoFile;
    private String mPhotoFilePath;

    private TextView mETCaption;
    private ImageView mIVPreview;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_FILEPATH = "file";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentComposeBinding binding;

    public ComposeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param filePath Parameter 1.
     * @return A new instance of fragment ComposeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComposeFragment newInstance(String filePath) {
        ComposeFragment fragment = new ComposeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILEPATH, filePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPhotoFilePath = getArguments().getString(ARG_FILEPATH);
            mPhotoFile = getPhotoFileUri();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentComposeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bitmap takenImage = BitmapFactory.decodeFile(mPhotoFile.getAbsolutePath());
        mETCaption = view.findViewById(R.id.etCaption);
        mIVPreview = view.findViewById(R.id.ivPreview);
        binding.ivPreview.setImageBitmap(takenImage);
        binding.btnShare.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String description = binding.etCaption.getText().toString();
                Log.i(TAG, "Checking to see if post can be saved.");
                mETCaption.clearFocus();
                if(description.isEmpty()) {
                    Toast.makeText(getContext(), "Description cannot be empty!", Toast.LENGTH_SHORT);
                    return;
                }
                if(mPhotoFile == null || binding.ivPreview.getDrawable() == null) {
                    Toast.makeText(getContext(), "There is no image.", Toast.LENGTH_SHORT);
                    return;
                }
                Log.i(TAG, "Saving post!");
                ParseUser currentUser = ParseUser.getCurrentUser();
                savePost(description, currentUser, mPhotoFile);
                getActivity().getSupportFragmentManager().beginTransaction().remove(ComposeFragment.this).commit();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            Bitmap takenImage = BitmapFactory.decodeFile(mPhotoFile.getAbsolutePath());
            binding.ivPreview.setImageBitmap(takenImage);
        }
    }

    private void savePost(String description, ParseUser currentUser, File mPhotoFile) {
        List<ParseFile> media = new ArrayList<>();
        media.add(new ParseFile(mPhotoFile));
        Post post = new Post();
        post.setBody(description);
        post.setMedia(media);
        post.setUser(currentUser);
        post.setLikesCount(0);
        post.setLikes(new ArrayList<String>());
        post.setComments(new ArrayList<String>());

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Error while saving post", e);
                    Toast.makeText(getContext(), "Your post failed to go through!", Toast.LENGTH_SHORT);
                }
                mETCaption.setText("");
                mIVPreview.setImageResource(0);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Returns the file for a photo stored on disk
    private File getPhotoFileUri() {
        // Get safe storage directory for photos
        // Use getExternalFilesDir on context to access package-specific directories
        // This is so we do not need to request external read/write runtime permissions
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        if(!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "Failed to create directory");
        }

        File file = new File(mPhotoFilePath);
        return file;
    }
}