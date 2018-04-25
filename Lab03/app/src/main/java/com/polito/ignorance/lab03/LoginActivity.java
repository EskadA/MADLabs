package com.polito.ignorance.lab03;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG_CREDENTIAL = "Credentials";
    private static final String TAG_ERROR = "Error";

    private Button btnLogin;
    private EditText inputEmail, inputPassword;
    private TextView btnSignup, btnForgotPass;

    private ScrollView activity_login;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //View
        findViews();

        //Buttons
        setButtonListener();

        //Init Firebase Auth
        auth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(inputEmail.getText().toString());
        arrayList.add(inputPassword.getText().toString());
        outState.putStringArrayList(TAG_CREDENTIAL, arrayList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<String> arrayList = savedInstanceState.getStringArrayList(TAG_CREDENTIAL);
        if (arrayList != null) {
            inputEmail.setText(arrayList.get(0));
            inputPassword.setText(arrayList.get(1));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null)
            updateUI();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_btn_forgot_password:
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                break;
            case R.id.login_btn_signup:
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                break;
            case R.id.login_btn_login:
                firebaseAuthWithCredentials(inputEmail.getText().toString(), inputPassword.getText().toString());
                break;
        }
    }

    private void firebaseAuthWithCredentials(String email, final String password) {
        if(controlStrings(email, password)) {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()) {
                                taskExceptionManager(task);
                            }
                            else{
                                updateUI();
                            }
                        }
                    });
        }
    }

    private boolean controlStrings(String email, final String password) {
        if (email.isEmpty() || password.isEmpty()) {
            if (password.isEmpty()) {
                inputPassword.setError(getString(R.string.error_invalid_password));
                inputPassword.requestFocus();
            }
            if (email.isEmpty()) {
                inputEmail.setError(getString(R.string.error_invalid_email));
                inputEmail.requestFocus();
            }
            return false;
        }

        return true;
    }

    private void taskExceptionManager(Task<AuthResult> task){
        try {
            throw task.getException();
        } catch(FirebaseAuthInvalidCredentialsException e) {
            inputPassword.setError(getString(R.string.error_invalid_password));
            inputPassword.requestFocus();
        } catch (FirebaseAuthInvalidUserException e){
            inputEmail.setError(getString(R.string.error_invalid_email));
            inputEmail.requestFocus();;
        } catch (FirebaseNetworkException e){
            Toast.makeText(LoginActivity.this, R.string.error_network, Toast.LENGTH_SHORT).show();
        } catch(Exception e) {
            Toast.makeText(LoginActivity.this, R.string.something_wrong, Toast.LENGTH_SHORT).show();
            Log.e(TAG_ERROR, e.getMessage());
        }
    }

    private void findViews(){
        btnLogin        = (Button)findViewById(R.id.login_btn_login);
        inputEmail      = (EditText)findViewById(R.id.login_email);
        inputPassword   = (EditText)findViewById(R.id.login_password);
        btnSignup       = (TextView)findViewById(R.id.login_btn_signup);
        btnForgotPass   = (TextView)findViewById(R.id.login_btn_forgot_password);
        activity_login  = (ScrollView) findViewById(R.id.activity_login);
    }

    private void setButtonListener(){
        btnSignup.setOnClickListener(this);
        btnForgotPass.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    private void updateUI() {
        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(activity_login, R.string.error_login, Snackbar.LENGTH_SHORT);
    }
}
