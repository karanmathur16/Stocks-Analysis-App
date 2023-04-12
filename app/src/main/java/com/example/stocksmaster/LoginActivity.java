package com.example.stocksmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * The type Login activity.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * The User name.
     */
    EditText userName, /**
     * The Et password.
     */
    etPassword;
    /**
     * The Min password length.
     */
    final int MIN_PASSWORD_LENGTH = 6;
    /**
     * The Cl.
     */
    ConstraintLayout cl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewInitializations();
    }

    /**
     * View initializations.
     */
    void viewInitializations() {
        userName = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);

        // To show back button in actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Validate input boolean.
     *
     * @return the boolean
     */
// Checking if the input in form is valid
    boolean validateInput() {

        if (userName.getText().toString().equals("")) {
            userName.setError("Please Enter Username");
            return false;
        }
        if (etPassword.getText().toString().equals("")) {
            etPassword.setError("Please Enter Password");
            return false;
        }

        return true;
    }


    // Hook Click Event

    /**
     * Perform sign in.
     *
     * @param v the v
     */
    public void performSignUp (View v) {

        cl = (ConstraintLayout) findViewById(R.id.container);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(cl.getWindowToken(), 0);

        if (validateInput()) {

            String username = userName.getText().toString();
            String password = etPassword.getText().toString();

            if(username.equals("karan") && password.equals("karan")) {
                Bundle extras = getIntent().getExtras();
                Intent i = new Intent(this, MainActivity.class);
                i.putExtra("username", username);
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                (new Handler())
                        .postDelayed(
                                new Runnable() {
                                    public void run() {
                                        startActivityForResult(i, 5);
                                    }
                                }, 100);

            }

            else if (username.equals("admin") && password.equals("admin")){
                Bundle extras = getIntent().getExtras();
                Intent i = new Intent(this, MainActivity.class);
                i.putExtra("username", username);
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                (new Handler())
                        .postDelayed(
                                new Runnable() {
                                    public void run() {
                                        startActivityForResult(i, 5);
                                    }
                                }, 100);
            }
            else {
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
            }

        }
    }

}