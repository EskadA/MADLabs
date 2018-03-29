package com.polito.ignorance.lab01;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.polito.ignorance.lab01.tools.AppCompatPermissionActivity;
import com.polito.ignorance.lab01.tools.ProfileImageManager;

import java.io.IOException;

public class editActivity extends AppCompatPermissionActivity {

    private static final int GET_FROM_GALLERY = 5;
    private static final int PHOTO_REQUEST_CODE = 6;
    private static final int UPLOAD_IMAGE = 10;
    private static final int RELOAD_IMAGE = 11;

    private static final String filename = "profileImage.jpeg";
    private static final String tempFilename= "profileImage(temp).jpeg";

    //edit texts
    private EditText name;
    private EditText mail;
    private EditText bio;
    private String nameText;
    private String mailText;
    private String bioText;

    //ImageView
    private ImageView profileImage;
    private ProfileImageManager imageManager;
    private Bitmap bitmap;
    private String path;
    private boolean isChanged = false;

    //some kinds of variables
    private TextView counterView;
    private final TextWatcher characterWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int counter;
            counter = s.length();
            counterView.setText(String.format("%s/200", String.valueOf(counter)));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    //shared preferences
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        preferences = getSharedPreferences("Info", Context.MODE_PRIVATE);
        editor = preferences.edit();

        counterView = (TextView)findViewById(R.id.counter);

        setToolbar();

        //set image view
        setProfileImageListener();
        setImageView(UPLOAD_IMAGE);

        //set text in the edit boxes
        getEditTextViews();
        getTextsFromPreferences();
        setTexts();

        //set character counter
        counterView.setText(String.format("%s/200", String.valueOf(bioText.length())));
        bio.addTextChangedListener(characterWatcher);
    }

    @Override
    public void onPermissionGranted(int requestCode) {
        switch (requestCode) {
            case PHOTO_REQUEST_CODE:
                Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoIntent, PHOTO_REQUEST_CODE);
                break;
            case GET_FROM_GALLERY:
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GET_FROM_GALLERY);
                break;
        }
        if(path == null){
            Log.d("PathNull", "Path is null");
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        nameText = savedInstanceState.getString("Name");
        mailText = savedInstanceState.getString("Mail");
        bioText = savedInstanceState.getString("Bio");
        isChanged = savedInstanceState.getBoolean("isChanged");

        if(isChanged)
            setImageView(RELOAD_IMAGE);
        else
            setImageView(UPLOAD_IMAGE);

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
        outState.putBoolean("isChanged", isChanged);

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
                getTextFromEditTextView();

                if(checkEditTextViewInput()){
                    path = imageManager.saveToInternalStorage(bitmap, filename, getApplicationContext());
                    editor.putString("imagePath", path);
                    editor.commit();
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

        if(resultCode == RESULT_OK){
            switch (requestCode){
                case GET_FROM_GALLERY:
                    Uri uri = data.getData();
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        path = imageManager.saveToInternalStorage(bitmap, tempFilename, getApplicationContext());
                        editor.commit();
                        isChanged = true;
                        profileImage.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Log.e("bitmap", "Failure: error on bitmap upload");
                    }
                    break;

                case PHOTO_REQUEST_CODE:
                    bitmap = (Bitmap) data.getExtras().get("data");
                    path = imageManager.saveToInternalStorage(bitmap, tempFilename, getApplicationContext());
                    editor.commit();
                    isChanged = true;
                    profileImage.setImageBitmap(bitmap);
                    break;
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //Set app Toolbar
    private void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.edit_title));
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);
    }

    //Set all the texts
    private void setTexts(){
        name.setText(nameText);
        mail.setText(mailText);
        bio.setText(bioText);
    }

    private void setProfileImageListener(){
        profileImage = (ImageView)findViewById(R.id.profileImage);
        imageManager = new ProfileImageManager();

        profileImage.setOnClickListener(new ImageView.OnClickListener(){

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(editActivity.this);
                builder.setItems(new String[]{getString(R.string.camera), getString(R.string.gallery)}, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        requestAppPermission(new String[]{Manifest.permission.CAMERA}, R.string.msg, PHOTO_REQUEST_CODE);
                                        break;
                                    case 1:
                                        requestAppPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, R.string.msg, GET_FROM_GALLERY);
                                        break;
                                }
                            }
                        }).create().show();
            }
        });

    }

    private void setImageView(int action) {
        path = preferences.getString("imagePath", null);
        switch (action) {
            case UPLOAD_IMAGE:
                if (path != null) {
                    Log.d("PathNotNull", "Upload: success");
                    bitmap = imageManager.loadImageFromInternalStorage(path, filename);
                    if (bitmap != null)
                        profileImage.setImageBitmap(bitmap);
                }
                break;
            case RELOAD_IMAGE:
                if (path != null) {
                    Log.d("PathNotNull", "Reload: success");
                    bitmap = imageManager.loadImageFromInternalStorage(path, tempFilename);
                    if (bitmap != null)
                        profileImage.setImageBitmap(bitmap);
                }
                break;
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
