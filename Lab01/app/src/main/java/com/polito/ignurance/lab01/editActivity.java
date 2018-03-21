package com.polito.ignurance.lab01;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.TokenWatcher;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;

public class editActivity extends AppCompatActivity {

    //toolbar
    Toolbar toolbar;

    //edit texts
    EditText name;
    EditText mail;
    EditText bio;

    //ImageView
    ImageView profileImage;

    //some kinds of variables
    String nameText;
    String mailText;
    String bioText;
    TextView counterView;
    int counter;
    private final TextWatcher characterWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            counter = s.length();
            counterView.setText(String.valueOf(counter) + "/200");
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    //shared preferences
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        preferences = getSharedPreferences("Info", Context.MODE_PRIVATE);
        counterView = (TextView)findViewById(R.id.counter);

        setToolbar();
        setProfileImageListener();
        getEditTextViews();
        getTextsFromPreferences();
        setTexts();

        //set character counter
        counterView.setText(bioText.length() + "/200");
        bio.addTextChangedListener(characterWatcher);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        nameText = savedInstanceState.getString("Name");
        mailText = savedInstanceState.getString("Mail");
        bioText = savedInstanceState.getString("Bio");
        setTexts();

        //get previous cursor focus
        getCursorFocus(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getTextFromEditTextView();
        outState.putString("Name", nameText);
        outState.putString("Mail", mailText);
        outState.putString("Bio", bioText);

        //set cursor position
        setCursorFocus(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_toolbar_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_save:
                editor = preferences.edit();
                getTextFromEditTextView();

                if(checkEditTextViewInput()){
                    finish();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                profileImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    //Set app Toolbar
    private void setToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.edit_title));
    }

    //Sel all the texts
    private void setTexts(){
        name.setText(nameText);
        mail.setText(mailText);
        bio.setText(bioText);
    }

    private void setProfileImageListener(){
        profileImage = (ImageView)findViewById(R.id.profileImage);
        profileImage.setOnClickListener(new ImageView.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });
    }

    private void setCursorFocus(Bundle outState){
        View focusedChild = getCurrentFocus();

        if (focusedChild != null) {
            int focusID = focusedChild.getId();
            int cursorLoc = 0;

            if (focusedChild instanceof EditText) {
                cursorLoc = ((EditText) focusedChild).getSelectionStart();
            }

            outState.putInt("focusID", focusID);
            outState.putInt("cursorLoc", cursorLoc);
        }
    }

    private boolean checkEditTextViewInput(){
        Toast missing;

        if (!nameText.isEmpty()) {
            editor.putString("Name", nameText);
        } else {
            missing = Toast.makeText(this, R.string.name_insert, Toast.LENGTH_SHORT);
            missing.show();
            return false;
        }

        if (!mailText.isEmpty()) {
            editor.putString("Mail", mailText);
        } else {
            missing = Toast.makeText(this, R.string.mail_insert, Toast.LENGTH_SHORT);
            missing.show();
            return false;
        }

        if (!bioText.isEmpty()) {
            editor.putString("Bio", bioText);
        } else {
            editor.remove("Bio");
        }

        editor.commit();
        return true;
    }

    private void getTextFromEditTextView(){
        nameText = name.getText().toString();
        mailText = mail.getText().toString();
        bioText = bio.getText().toString();
    }

    private void getTextsFromPreferences(){
        nameText = preferences.getString("Name", getString(R.string.name));
        mailText = preferences.getString("Mail", getString(R.string.mail));
        bioText = preferences.getString("Bio", getString(R.string.no_bio));
    }

    private void getEditTextViews(){
        name = (EditText) findViewById(R.id.name);
        mail = (EditText)findViewById(R.id.mail);
        bio = (EditText)findViewById(R.id.bio);

    }

    private void getCursorFocus(Bundle savedInstanceState){
        int focusID = savedInstanceState.getInt("focusID", View.NO_ID);

        View focusedChild = findViewById(focusID);
        if (focusedChild != null) {
            focusedChild.requestFocus();

            if (focusedChild instanceof EditText) {
                int cursorLoc = savedInstanceState.getInt("cursorLoc", 0);
                ((EditText) focusedChild).setSelection(cursorLoc);
            }
        }
    }
}
