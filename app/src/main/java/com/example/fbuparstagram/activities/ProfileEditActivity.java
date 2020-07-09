package com.example.fbuparstagram.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fbuparstagram.R;
import com.example.fbuparstagram.databinding.ToolbarBinding;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfileEditActivity extends AppCompatActivity {
    public static final String TAG = ProfileEditActivity.class.getSimpleName();
    public static final int GET_FROM_GALLERY = 3;

    private com.example.fbuparstagram.databinding.EditProfileLayoutBinding mBinding;

    private ImageView ivAvatar;
    private Button btnUploadAvatar;

    private EditText etUsername;
    private EditText etBio;

    private MenuItem mMiDone;

    private ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mBinding = com.example.fbuparstagram.databinding.EditProfileLayoutBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);

        ivAvatar = mBinding.ivAvatar;
        btnUploadAvatar = mBinding.btnUploadAvatar;
        etUsername = mBinding.etUsername;
        etBio = mBinding.etBio;

        ToolbarBinding toolbarBinding = (ToolbarBinding) mBinding.toolbarMain;
        Toolbar toolbar = toolbarBinding.toolbar;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        currentUser = ParseUser.getCurrentUser();

        ParseFile file = currentUser.getParseFile("avatar");
        if(file != null)
            Glide.with(this).load(file.getUrl()).into(ivAvatar);
        etUsername.setText(ParseUser.getCurrentUser().getUsername());
        etBio.setText(currentUser.getString("bio"));
        btnUploadAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem miChat = menu.findItem(R.id.miChat);
        miChat.setVisible(false);
        mMiDone = menu.findItem(R.id.miEditProfileDone);
        mMiDone.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.miEditProfileDone:
                saveUserData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                InputStream ims = getContentResolver().openInputStream(selectedImage);
                // just display image in imageview
                ivAvatar.setImageBitmap(BitmapFactory.decodeStream(ims));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveUserData() {
        currentUser.put("bio", etBio.getText().toString());
        currentUser.put("username", etUsername.getText().toString());
        BitmapDrawable drawable = (BitmapDrawable) ivAvatar.getDrawable();
        if(drawable != null) {
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Compress image to lower quality scale 1 - 100
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] image = stream.toByteArray();
            ParseFile file = new ParseFile(currentUser.getObjectId() + "_avatar.png", image);

            currentUser.put("avatar", file);
        }
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Failed to save user data", e);
                } else {
                    Toast.makeText(ProfileEditActivity.this, "Successfully saved profile!", Toast.LENGTH_SHORT);
                    finish();
                }
            }
        });
    }
}