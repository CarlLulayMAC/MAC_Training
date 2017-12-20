package com.example.user.firebase.view.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.firebase.R;
import com.example.user.firebase.view.movie.MovieActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity implements LoginContract.View {

    private EditText etEmail;
    private EditText etPassword;
    private FirebaseAuth firebaseAuth;
    private LoginAuthenticator loginAuthenticator;
    private LoginPresenter presenter;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        bindViews();

        firebaseAuth = FirebaseAuth.getInstance();

        loginAuthenticator = new LoginAuthenticator(firebaseAuth, this);
        presenter = new LoginPresenter(loginAuthenticator);
        presenter.attachView(this);
        presenter.checkSession();

    }

    private void bindViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
    }

    public void onFireBaseSignUp(View view) {

        getCredentials();
        presenter.createUser(email, password);

    }

    private void getCredentials() {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
    }

    public void onFireBaseSignIn(View view) {

        getCredentials();
        presenter.validateUser(email,password);

    }

    @Override
    public void showError(String error) {

    }

    @Override
    public void onUserCreation(boolean isCreated) {

        Log.d(TAG, "onUserCreation: " + isCreated);

        if(isCreated)
            showToast("User created");
        else
            showToast("User not created");
    }

    @Override
    public void onUserValidation(boolean isValid) {

        Log.d(TAG, "onUserValidation: " + isValid);
        if(isValid)
            showToast("Signed In");
        else
            showToast("Sign in failed");


    }

    @Override
    public void isSessionValid(boolean isValid) {


        if(isValid){
            Intent intent = new Intent(getApplicationContext(), MovieActivity.class);
            startActivity(intent);
        }

    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
