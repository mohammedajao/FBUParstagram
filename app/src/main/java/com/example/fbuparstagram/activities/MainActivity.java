package com.example.fbuparstagram.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.fbuparstagram.R;
import com.example.fbuparstagram.databinding.ActivityFeedBinding;
import com.example.fbuparstagram.databinding.ToolbarBinding;
import com.example.fbuparstagram.fragments.ComposeFragment;
import com.example.fbuparstagram.fragments.FeedFragment;
import com.example.fbuparstagram.fragments.ProfileFragment;
import com.example.fbuparstagram.models.Comment;
import com.example.fbuparstagram.models.Post;
import com.example.fbuparstagram.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;

    private ActivityFeedBinding binding;

    public String mPhotoFileName;
    private File mPhotoFile;

    private BottomNavigationView mButtonNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start with fragment we want
//        if (savedInstanceState == null){
//            getSupportFragmentManager().beginTransaction()
//                    .add(android.R.id.content, new Fragment_name_which_you_wantto_open()).commit();}

        ActivityFeedBinding binding = ActivityFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ToolbarBinding toolbarBinding = (ToolbarBinding) binding.toolbarMain;
        Toolbar toolbar = toolbarBinding.toolbar;

        mButtonNavigationView = binding.bottomNavigation;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        mButtonNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.miCompose:
                        launchCamera();
                        break;
                    case R.id.miProfile:
                        Bundle bundle = new Bundle();
                        bundle.putString("USER_TARGET", ParseUser.getCurrentUser().getUsername());
                        loadFragment(new ProfileFragment(), bundle);
                        break;
                    default:
                        loadFragment(new FeedFragment(), null);
                        break;
                }
                return true;
            }
        });
        loadFragment(new FeedFragment(), null);
    }


    private void launchCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mPhotoFile = getPhotoFileUri();

        Uri fileProvider = FileProvider.getUriForFile(MainActivity.this, "com.codepath.fileprovider", mPhotoFile);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If the phone has a camera and can resolve this intent start the activity
        if(cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the file for a photo stored on disk
    private File getPhotoFileUri() {
        // Get safe storage directory for photos
        // Use getExternalFilesDir on context to access package-specific directories
        // This is so we do not need to request external read/write runtime permissions
        File file = new File(getPhotoFilePath());
        return file;
    }

    private String getPhotoFilePath() {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        if(!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "Failed to create directory");
        }
        return mediaStorageDir.getPath() + File.separator + mPhotoFileName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle bundle = new Bundle();
            File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

            if(!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Failed to create directory");
            }
            bundle.putString("file", getPhotoFilePath());
            FragmentManager manager = getSupportFragmentManager();
            Fragment frag = new ComposeFragment();
            frag.setArguments(bundle);
            manager.beginTransaction().replace(R.id.fragmentContainer, frag).commit();

        }
    }

    private void loadFragment(Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
    }
}