package com.polito.ignurance.lab01;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class showActivity extends AppCompatActivity {

    //Toolbar
    Toolbar toolbar;

    //TextViews
    TextView name;
    TextView mail;
    TextView bio;

    //SharedPreferences
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        preferences = getSharedPreferences("Info", Context.MODE_PRIVATE);

        getTextViews();
        setToolbar();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setTexts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTexts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_edit:
                //Edit user profile
                Intent edit = new Intent(showActivity.this, editActivity.class);
                startActivity(edit);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Set app Toolbar
    private void setToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(name.getText());
    }

    //Get all the text views
    private void getTextViews(){
        name = (TextView)findViewById(R.id.name);
        mail = (TextView)findViewById(R.id.mail);
        bio = (TextView)findViewById(R.id.bio);
    }

    //Sel all the texts
    private void setTexts(){
        name.setText(preferences.getString("Name", "Name"));
        mail.setText(preferences.getString("Mail", "Mail"));
        bio.setText(preferences.getString("Bio", "No Bio"));
    }
}
