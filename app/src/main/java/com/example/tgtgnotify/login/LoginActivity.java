package com.example.tgtgnotify.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.tgtgnotify.MainActivity;

import com.example.tgtgnotify.R;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;

    private TextView loginerrorTextView;
    private Button loginButton;

    private LoginViewModel loginViewModel;

    // Switch to factory implementation later.
    private LoginRepository loginRepostory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d("TGTG", "Started");

        usernameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.login);
        loginerrorTextView = (TextView) findViewById(R.id.loginerror);

        loginRepostory = LoginRepository.getInstance(this.getApplicationContext());
        loginViewModel = new LoginViewModel(loginRepostory);

        // Button listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                login();
            }
        });


        // Observer for logged-in user (switch to main activity when logged in succesfully)
        loginViewModel.getLoggedInUser().observe(this, new Observer<LoggedInUser>() {
            @Override
            public void onChanged(LoggedInUser loggedInUser) {
                Log.d("TGTG", "loggedinuser change observed!");
                if (loggedInUser.isLoggedIn()){
                Log.d("TGTG", "LoggedinActivity received logged in user: " + loggedInUser.getUserId() + ", " + loggedInUser.getDisplayName() +", logged in: " + loggedInUser.isLoggedIn());
                loginButton.setEnabled(true);
                Log.d("TGTG", "Logged in succesfully! Switching to main activity.");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

                // Finish activity so user cannot go back.
                finish();
                }}
        });


        // Try to log in immediately
        //this.loginViewModel.login("","");
    }


    // Called when 'login' is pressed.
    public void login() {
        Log.d("TGTG", "LoginActivity: button login");
        String email = this.usernameEditText.getText().toString();
        String password = this.usernameEditText.getText().toString();

        this.loginViewModel.login(email, password);

        // Todo: Show loading symbol.
        this.loginButton.setEnabled(false);

    }

}
