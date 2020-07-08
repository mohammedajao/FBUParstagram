package com.example.fbuparstagram.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fbuparstagram.databinding.ActivityLoginBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = LoginActivity.class.getSimpleName();

    private EditText etUsername;
    private EditText etPassword;
    private TextView tvRegister;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if(ParseUser.getCurrentUser() != null) {
            navigateToApp();
        }

        etUsername = binding.etUsername;
        etPassword = binding.etPassword;
        btnLogin = binding.btnLogin;
        tvRegister = binding.tvRegister;

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToSignUpPage();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                login(username, password);
            }
        });
    }

    // Logs user in background so the network request does not delay the main thread
    // Allows user to use app while we log them in
    // Call finish upon login so user exits app when they go back in application rather than logging out
    private void login(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Failed to login user", e);
                    Toast.makeText(LoginActivity.this, "Please try again!", Toast.LENGTH_SHORT);
                    return;
                } else {
                    navigateToApp();
                }
            }
        });
    }

    private void navigateToApp() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToSignUpPage() {
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(intent);
        finish();
    }
}