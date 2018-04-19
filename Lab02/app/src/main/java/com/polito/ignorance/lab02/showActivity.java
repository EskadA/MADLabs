package com.polito.ignorance.lab02;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.polito.ignorance.lab02.tools.ProfileImageManager;

public class showActivity extends AppCompatActivity {

    private static final String filename = "profileImage.jpeg";

    //Toolbar
    Toolbar toolbar;

    //TextViews
    private TextView name;
    private TextView mail;
    private TextView bio;
    private String nameText;
    private String mailText;
    private String bioText;

    //SharedPreferences
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        preferences = getSharedPreferences("Info", Context.MODE_PRIVATE);

        getTextViews();
        getTextsFromPreferences();
        setTexts();
        setImageView();
        setToolbar();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        getTextsFromPreferences();
        setTexts();
        setTitle(nameText);
        setImageView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTextsFromPreferences();
        setTexts();
        setTitle(nameText);
        setImageView();
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
        setTitle(nameText);
    }

    private void setImageView() {
        ImageView profileImage = (ImageView) findViewById(R.id.profileImage);
        ProfileImageManager imageManager = new ProfileImageManager();
        String path = preferences.getString("imagePath", null);
        if(path != null){
            Bitmap bitmap = imageManager.loadImageFromInternalStorage(path, filename);
            if(bitmap != null){
                profileImage.setImageBitmap(bitmap);
                Log.d("bitmap", "Success: bitmap successfully loaded");
            }
            Log.d("image", "Success: " + path);
        }
    }

    //Get all the text views
    private void getTextViews(){
        name = (TextView)findViewById(R.id.name);
        mail = (TextView)findViewById(R.id.mail);
        bio = (TextView)findViewById(R.id.bio);
    }

    //Sel all the texts
    private void setTexts(){
        name.setText(nameText);
        mail.setText(mailText);
        bio.setText(bioText);
    }

    private void getTextsFromPreferences(){
        nameText = preferences.getString("Name", getString(R.string.name));
        mailText = preferences.getString("Mail", getString(R.string.mail));
        bioText = preferences.getString("Bio", getString(R.string.no_bio));
    }

}
