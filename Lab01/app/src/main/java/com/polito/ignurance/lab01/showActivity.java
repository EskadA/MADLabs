package com.polito.ignurance.lab01;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.polito.ignurance.lab01.tools.AppCompatPermissionActivity;
import com.polito.ignurance.lab01.tools.ProfileImageManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class showActivity extends AppCompatPermissionActivity {

    private static final int UPLOAD_IMAGE = 10;
    private static final String filename = "profileImage.png";

    //TextViews
    private TextView name;
    private TextView mail;
    private TextView bio;

    //ImageView
    private ImageView profileImage;
    private ProfileImageManager imageManager;

    //SharedPreferences
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        preferences = getSharedPreferences("Info", Context.MODE_PRIVATE);

        getTextViews();
        setImageView();
        setToolbar();
    }

    @Override
    public void onPermissionGranted(int requestCode) {
        switch (requestCode){
            case UPLOAD_IMAGE:
                String path = preferences.getString("imagePath", null);
                if(path != null){
                    Bitmap bitmap = imageManager.loadImageFromInternalStorage(path, filename);
                    if(bitmap != null)
                        profileImage.setImageBitmap(bitmap);
                    Log.d("image", "Success" + path);
                }
                break;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setTexts();
        setImageView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTexts();
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(name.getText());
    }

    private void setImageView() {
        profileImage = (ImageView)findViewById(R.id.profileImage);
        imageManager = new ProfileImageManager();
        requestAppPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, R.string.msg, UPLOAD_IMAGE);
    }

    //Get all the text views
    private void getTextViews(){
        name = (TextView)findViewById(R.id.name);
        mail = (TextView)findViewById(R.id.mail);
        bio = (TextView)findViewById(R.id.bio);
    }

    //Sel all the texts
    private void setTexts(){
        name.setText(preferences.getString("Name", getString(R.string.name)));
        mail.setText(preferences.getString("Mail", getString(R.string.mail)));
        bio.setText(preferences.getString("Bio", getString(R.string.no_bio)));
    }

}
