package com.polito.ignorance.lab03;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLogout, btnProfile, btnInsertBook, btnSecretDoor;

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Views
        findViews();

        //Button Listener
        setButtonListener();
        setToolbar();

        //Init Firebase
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dashboard_logout:
                logoutUser();
                break;
            case R.id.dashboard_book:
                Intent bookIntent = new Intent(DashboardActivity.this, InsertBookActivity.class);
                startActivity(bookIntent);
                break;
            case R.id.dashboard_profile:
                Intent profileIntent = new Intent(DashboardActivity.this, ShowProfileActivity.class);
                startActivity(profileIntent);
                break;
            case R.id.ehicomehere:
                Intent secretIntent = new Intent(DashboardActivity.this, SearchBookActivity.class);
                startActivity(secretIntent);
                break;
        }
    }

    //Set app Toolbar
    private void setToolbar(){
        Toolbar toolbar;
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Dashbard");
    }

    private void findViews(){
        btnLogout     = (Button) findViewById(R.id.dashboard_logout);
        btnInsertBook = (Button) findViewById(R.id.dashboard_book);
        btnProfile    = (Button) findViewById(R.id.dashboard_profile);
        btnSecretDoor = (Button) findViewById(R.id.ehicomehere);
    }

    private void setButtonListener(){
        btnLogout.setOnClickListener(this);
        btnInsertBook.setOnClickListener(this);
        btnProfile.setOnClickListener(this);
        btnSecretDoor.setOnClickListener(this);
    }

    private void logoutUser() {
        auth.signOut();
        if(auth.getCurrentUser() == null) {
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish();
        }
    }
}