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
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtWelcome;
    private EditText inputNewPassword;
    private Button btnChangePass,btnLogout;
    private ConstraintLayout activity_dashboard;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //View
        findViews();

        //Button listener
        setButtonListener();

        //Init Firebase
        auth = FirebaseAuth.getInstance();

        //Session check
        if(auth.getCurrentUser() != null)
            txtWelcome.setText("Welcome , " + auth.getCurrentUser().getDisplayName());


    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.dashboard_btn_change_pass)
            changePassword(inputNewPassword.getText().toString());
        else if(view.getId() == R.id.dashboard_btn_logout)
            logoutUser();
    }

    private void logoutUser() {
        auth.signOut();
        if(auth.getCurrentUser() == null)
        {
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void changePassword(String newPassword) {
        FirebaseUser user = auth.getCurrentUser();
        user.updatePassword(newPassword).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Snackbar snackBar = Snackbar.make(activity_dashboard,"Password changed",Snackbar.LENGTH_SHORT);
                    snackBar.show();
                }
            }
        });
    }

    private void findViews(){
        txtWelcome = (TextView)findViewById(R.id.dashboard_welcome);
        inputNewPassword = (EditText)findViewById(R.id.dashboard_new_password);
        btnChangePass = (Button)findViewById(R.id.dashboard_btn_change_pass);
        btnLogout = (Button)findViewById(R.id.dashboard_btn_logout);
        activity_dashboard = (ConstraintLayout)findViewById(R.id.activity_dash_board);
    }

    private void setButtonListener(){
        btnChangePass.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }
}
