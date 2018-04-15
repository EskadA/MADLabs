package com.polito.ignorance.lab02;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLogin;
    private EditText inputEmail, inputPassword;
    private TextView btnSignup, btnForgotPass;

    private ConstraintLayout activity_login;

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

        //Check already session , if ok-> DashboardActivity
        if(auth.getCurrentUser() != null)
            startActivity(new Intent(LoginActivity.this,DashboardActivity.class));
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.login_btn_forgot_password)
        {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            finish();
        }
        else if(view.getId() == R.id.login_btn_signup)
        {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            finish();
        }
        else if(view.getId() == R.id.login_btn_login)
        {
            loginUser(inputEmail.getText().toString(), inputPassword.getText().toString());
        }
    }

    private void loginUser(String email, final String password) {
        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            if(password.length() < 6)
                            {
                                Snackbar snackBar = Snackbar.make(activity_login,"Password length must be over 6", Snackbar.LENGTH_SHORT);
                                snackBar.show();
                            }
                        }
                        else{
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                        }
                    }
                });
    }

    private void findViews(){
        btnLogin = (Button)findViewById(R.id.login_btn_login);
        inputEmail = (EditText)findViewById(R.id.login_email);
        inputPassword = (EditText)findViewById(R.id.login_password);
        btnSignup = (TextView)findViewById(R.id.login_btn_signup);
        btnForgotPass = (TextView)findViewById(R.id.login_btn_forgot_password);
        activity_login = (ConstraintLayout) findViewById(R.id.activity_login);
    }

    private void setButtonListener(){
        btnSignup.setOnClickListener(this);
        btnForgotPass.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }
}
