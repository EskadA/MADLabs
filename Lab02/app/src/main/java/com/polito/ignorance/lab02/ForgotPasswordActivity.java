package com.polito.ignorance.lab02;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText inputEmail;
    private Button btnResetPass;
    private TextView btnBack;
    private ConstraintLayout activity_forgot;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //View
        findViews();

        setButtonListener();

        //Init Firebase
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.forgot_btn_back)
        {
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }
        else  if(view.getId() == R.id.forgot_btn_reset)
        {
            resetPassword(inputEmail.getText().toString());
        }
    }

    private void resetPassword(final String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Snackbar snackBar = Snackbar.make(activity_forgot,"We have sent password to email: " + email,Snackbar.LENGTH_SHORT);
                            snackBar.show();
                        }
                        else{
                            Snackbar snackBar = Snackbar.make(activity_forgot,"Failed to send password",Snackbar.LENGTH_SHORT);
                            snackBar.show();
                        }
                    }
                });
    }

    private void findViews(){
        inputEmail = (EditText)findViewById(R.id.forgot_email);
        btnResetPass = (Button)findViewById(R.id.forgot_btn_reset);
        btnBack = (TextView)findViewById(R.id.forgot_btn_back);
        activity_forgot = (ConstraintLayout)findViewById(R.id.activity_forgot_password);
    }

    private void setButtonListener(){
        btnResetPass.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }
}

