package com.polito.ignurance.lab01;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class editActivity extends AppCompatActivity {

    //toolbar
    Toolbar toolbar;

    //edit texts
    EditText name;
    EditText mail;
    EditText bio;

    //some kinds of variables
    String nameText;
    String mailText;
    String bioText;

    //shared preferences
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        preferences = getSharedPreferences("Info", Context.MODE_PRIVATE);

        setToolbar();
        getEditTexts();
        getTextsFromPreferences();
        setTexts();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setTexts();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getTexts();
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
                editor = preferences.edit();
                getTexts();
                return checkInput();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Set app Toolbar
    private void setToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Edit your profile");
    }

    private boolean checkInput(){
        Toast missing;

        if (!nameText.isEmpty()) {
            editor.putString("Name", nameText.toString());
        }
        else{
            missing = Toast.makeText(this, "Insert your name", Toast.LENGTH_SHORT);
            missing.show();
            return false;
        }

        if (!mailText.isEmpty()) {
            editor.putString("Mail", mailText.toString());
        }
        else {
            missing = Toast.makeText(this, "Insert your mail", Toast.LENGTH_SHORT);
            missing.show();
            return false;
        }

        if (!bioText.isEmpty()) {
            editor.putString("Bio", bioText.toString());
        }
        else {
            editor.remove("Bio");
        }

        editor.commit();
        finish();
        return true;
    }

    private void getTexts(){
        nameText = name.getText().toString();
        mailText = mail.getText().toString();
        bioText = bio.getText().toString();
    }

    private void getTextsFromPreferences(){
        nameText = preferences.getString("Name", "Name");
        mailText = preferences.getString("Mail", "Mail");
        bioText = preferences.getString("Bio", "No Bio");
    }

    //Sel all the texts
    private void setTexts(){
        name.setText(nameText);
        mail.setText(mailText);
        bio.setText(bioText);
    }

    //Get all the text views
    private void getEditTexts(){
        name = (EditText) findViewById(R.id.name);
        mail = (EditText)findViewById(R.id.mail);
        bio = (EditText)findViewById(R.id.bio);
    }
}
