package com.polito.ignorance.lab02;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnSignup;
    EditText inputEmail, inputPass;
    ConstraintLayout activity_sign_up;

    private FirebaseAuth auth;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //View
        btnSignup = (Button)findViewById(R.id.signup_btn_register);
        inputEmail = (EditText)findViewById(R.id.signup_email);
        inputPass = (EditText)findViewById(R.id.signup_password);
        activity_sign_up = (ConstraintLayout)findViewById(R.id.activity_sign_up);

        btnSignup.setOnClickListener(this);

        //Init Firebase
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.signup_btn_register){
            signUpUser(inputEmail.getText().toString(), inputPass.getText().toString());
        }
    }

    private void signUpUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            Toast t = Toast.makeText(SignUpActivity.this, "Error: " + task.getException(), Toast.LENGTH_LONG);
                            t.show();
                        }
                        else{
                            snackbar = Snackbar.make(activity_sign_up,"Register success! ",Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    }
                });
    }
}