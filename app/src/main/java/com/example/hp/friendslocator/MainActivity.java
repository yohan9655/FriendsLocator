package com.example.hp.friendslocator;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity {


    private TextView username;
    private TextView password;
    private TextView email;
    private TextView signup_login,switchTo;
    private Boolean signUpActive = false;


    public void Switch(View view) {
        if(signUpActive)
        {
            email.setAlpha(0);
            signup_login.setText("Login");
            signUpActive = false;
            switchTo.setText("Sign up");

        }

        else
        {
            email.setAlpha(1);
            signup_login.setText("Sign up");
            signUpActive = true;
            switchTo.setText("Login");

        }
    }

    public void submit(View view) {
        if (signUpActive) {
            if (username.getText().toString().isEmpty() || password.getText().toString().isEmpty() || email.getText().toString().isEmpty()) {
                Toast.makeText(this, "Error Missing Password/Username/Email", Toast.LENGTH_SHORT).show();
            } else {
                final ParseUser user = new ParseUser();
                user.setUsername(username.getText().toString());
                user.setPassword(password.getText().toString());
                user.setEmail(email.getText().toString());
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(), List.class);
                            //intent.putExtra("userId",user.getObjectId());
                            startActivity(intent);

                        } else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }

        else
        {
            if (username.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please fill Password/Username fields", Toast.LENGTH_SHORT).show();
            }
            else
            {
                ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if(user != null)
                        {
                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), List.class);
                            //intent.putExtra("userId",user.getObjectId());
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,"Wrong Username/Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(ParseUser.getCurrentUser() != null)
        {
            Toast.makeText(this, "Already Logged In as " + ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), List.class);
            //intent.putExtra("userId",user.getObjectId());
            startActivity(intent);
        }
        super.onCreate(savedInstanceState);
        int orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(orientation);
        setContentView(R.layout.activity_main);


        final LottieAnimationView animationView = (LottieAnimationView) findViewById(R.id.animation_view);
        animationView.useExperimentalHardwareAcceleration(true);
        animationView.enableMergePathsForKitKatAndAbove(true);

        signup_login = findViewById(R.id.textView);
        switchTo = findViewById(R.id.textView2);
        username = findViewById(R.id.username);
        username.setY(-100);
        username.setAlpha(0);
        username.animate().translationYBy(100).alpha(1).setDuration(1500);
        password = findViewById(R.id.password);
        password.setY(-100);
        password.setAlpha(0);
        password.animate().translationYBy(100).alpha(1).setDuration(1500);
        email = findViewById(R.id.email);
        //email.setY(-100);
        //email.setAlpha(0);
        //email.animate().translationYBy(100).alpha(1).setDuration(1500);
        email.setAlpha(0);


        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }
}
