package com.polito.ignorance.lab02;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.polito.ignorance.lab02.tools.ProfileImageManager;
import com.polito.ignorance.lab02.tools.User;

import java.io.File;
import java.io.IOException;

public class ShowActivity extends AppCompatActivity {

    private static final String filename = "profileImage.jpeg";
    private static final String TAG = "DatabaseError";

    //Toolbar
    Toolbar toolbar;

    //TextViews
    private TextView name;
    private TextView mail;
    private TextView bio;

    //SharedPreferences
    private SharedPreferences preferences;
    private FirebaseUser authUser;
    private DatabaseReference database;
    private DatabaseReference ref;
    private StorageReference storageRef;
    private User user;
    private String email;

    private ImageView profileImage;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        database = FirebaseDatabase.getInstance().getReference();
        ref = database.child("users");
        storageRef = FirebaseStorage.getInstance().getReference();
        preferences = getSharedPreferences("Info", Context.MODE_PRIVATE);
        authUser = FirebaseAuth.getInstance().getCurrentUser();
        getTextViews();
        setToolbar();
        progressDialog = new ProgressDialog(this);

        email = authUser.getEmail().replace(",",",,").replace(".", ",");
        getUserReference();
        profileImage = (ImageView) findViewById(R.id.profileImage);
        setImageView();



    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setImageView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserReference();
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
                Intent edit = new Intent(ShowActivity.this, EditActivity.class);
                startActivity(edit);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //Set app Toolbar
    private void setToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);
    }

    private void setImageView() {
        downloadToLocalFile(storageRef.child("images").child(email).child("profile image"));
    }

    //Get all the text views
    private void getTextViews(){
        name = (TextView)findViewById(R.id.name);
        mail = (TextView)findViewById(R.id.mail);
        bio = (TextView)findViewById(R.id.bio);
    }

    //Sel all the texts
    private void setTexts(){
        name.setText(user.getUsername());
        mail.setText(user.getEmail());
        bio.setText(user.getBio());
    }

    private void getUserReference(){
        ref.child(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                setTexts();
                setTitle(user.getUsername());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    private void downloadToLocalFile(StorageReference fileRef) {
        if (fileRef != null) {
            progressDialog.setTitle(getString(R.string.downloading));
            progressDialog.setMessage(null);
            progressDialog.show();

            try {
                final File localFile = File.createTempFile("profileImage", "jpeg");

                fileRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bmp = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        profileImage.setImageBitmap(bmp);
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(ShowActivity.this, R.string.profile_not_exists, Toast.LENGTH_LONG).show();
                    }
                }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // progress percentage
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        // percentage in progress dialog
                        progressDialog.setMessage(getString(R.string.downloaded) + ((int) progress) + getString(R.string.perc));
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(ShowActivity.this, R.string.profile_not_exists, Toast.LENGTH_LONG).show();
        }
    }
}
