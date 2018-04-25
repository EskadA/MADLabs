package com.polito.ignorance.lab03;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG_CREDENTIAL = "Credentials";
    private static final String TAG_ERROR = "Error";

    private EditText inputEmail;
    private Button btnResetPass;
    private TextView btnBack;
    private ScrollView activity_forgot;

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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TAG_CREDENTIAL, inputEmail.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        inputEmail.setText(savedInstanceState.getString(TAG_CREDENTIAL));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.forgot_btn_back:
                onBackPressed();
                finish();
                break;
            case R.id.forgot_btn_reset:
                resetPassword(inputEmail.getText().toString());
                break;
        }

    }

    private void resetPassword(final String email) {
        if(email.isEmpty()){
            inputEmail.setError(getString(R.string.error_invalid_email));
            inputEmail.requestFocus();
        } else{
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Snackbar snackBar = Snackbar.make(activity_forgot, String.format("%s%s", getString(R.string.valid_email), email),Snackbar.LENGTH_SHORT);
                                snackBar.show();
                            } else{
                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthInvalidCredentialsException e) {
                                    inputEmail.setError(getString(R.string.error_invalid_email));
                                    inputEmail.requestFocus();
                                } catch(Exception e) {
                                    Toast.makeText(ForgotPasswordActivity.this, R.string.error_network, Toast.LENGTH_SHORT).show();
                                    Log.e(TAG_ERROR, e.getMessage());
                                }
                            }
                        }
                    });
        }
    }

    private void findViews(){
        inputEmail      = (EditText)findViewById(R.id.forgot_email);
        btnResetPass    = (Button)findViewById(R.id.forgot_btn_reset);
        btnBack         = (TextView)findViewById(R.id.forgot_btn_back);
        activity_forgot = (ScrollView)findViewById(R.id.activity_forgot_password);
    }

    private void setButtonListener(){
        btnResetPass.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }
}