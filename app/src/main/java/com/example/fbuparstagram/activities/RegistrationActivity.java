package com.example.fbuparstagram.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.fbuparstagram.R;
import com.example.fbuparstagram.databinding.ActivityRegistrationBinding;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegistrationActivity extends AppCompatActivity {
    public static final String TAG = LoginActivity.class.getSimpleName();

    private EditText mEtUsername;
    private EditText mEtPassword;
    private EditText mETEmail;
    private TextView mTvLogin;
    private Button mBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRegistrationBinding binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        mEtUsername = binding.etUsername;
        mEtPassword = binding.etPassword;
        mTvLogin = binding.tvLogin;
        mBtnRegister = binding.btnSignUp;
        mETEmail = binding.etEmail;

        mTvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToLogInPage();
            }
        });
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
        setContentView(view);
    }

    private void register() {
        ParseUser user = new ParseUser();
        String username = mEtUsername.getText().toString();
        String password = mEtPassword.getText().toString();
        String email = mETEmail.getText().toString();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Failed to sign up user", e);
                    return;
                }
                navigateToApp();
            }
        });
    }

    private void navigateToApp() {
        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToLogInPage() {
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}