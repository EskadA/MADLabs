package com.polito.ignorance.lab02;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.polito.ignorance.lab02.tools.AppCompatPermissionActivity;
import com.polito.ignorance.lab02.tools.ConditionImageManager;

import java.io.IOException;


public class InsertBookActivity extends AppCompatPermissionActivity {

     private static final int GET_FROM_GALLERY = 5;
     private static final int PHOTO_REQUEST_CODE = 6;
     private static final int UPLOAD_IMAGE = 10;
     private static final int RELOAD_IMAGE = 11;


    //ImageView
    private static final String filename = "profileImage.jpeg"; //used when the save button is clicked
    private static final String tempFilename= "profileImage(temp).jpeg"; //used when the save button has not yet been cliked (temporarly upload)
    private ImageView ConditionImageView;
    private ConditionImageManager imageManager;
    private Bitmap bitmap;
    private String path;
    private boolean isChanged = false;

    //UserInterface
    private Button retrieveButton;
    private Button scanButton;
    private EditText EditTextAuthor;
    private EditText EditTextTitle;
    private EditText EditTextISBN;
    private EditText EditTextPublisher;
    private EditText EditTextPublishedYear;

    private String author;
    private String title;
    private String ISBNCode;

    private SharedPreferences preferences;

     //Flow: Main activity with possibility to fill in the fields manually or to press the button "Scan a barcode"
    // "Scan a barcode -> Intent to the "ScannerActivity" page: surface view that shows the camera relevation and in result of pointing a barcode print n the screen the code
    //                    If you press the button "Import", this will  return to Main Activity the code using the putExtra of intent
    //                    InsertBookActivity: save the code in the field ISBN and (if ISBN field is setted with the right number of cipher or pushing the button retrieve infos) start an Async Task that will retrieve frome googleapi/books some data about the book using the code just scanned
    //                    Note that the Async Task will start if the isbn is filled manually or scanning the code

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        retrieveButton = findViewById(R.id.startRetrieveTask);
        scanButton = findViewById(R.id.startScan);
        EditTextAuthor=findViewById(R.id.textAuthor); //da passare al costruttore dell'AsyncTask che dovrà modificarne il valore in seguito al risultato
        EditTextTitle=findViewById(R.id.textTitle);
        EditTextISBN=findViewById(R.id.textISBN);
        EditTextPublisher=findViewById(R.id.textPublisher);
        EditTextPublishedYear=findViewById(R.id.textEditionY);
        //set photo condition image view
        setProfileImageListener();
        setImageView(UPLOAD_IMAGE);

        preferences = getSharedPreferences("ISBN", Context.MODE_PRIVATE);

        //Se c'è un valore acquisito lo mette in ISBNCode e lo stampa nell'editText
        ISBNCode = getIntent().getStringExtra("ISBNCode");
        EditTextISBN.setText(ISBNCode);
        makeRetrieveButtonClickable();

        //When Scan Code is clicked is started an intent that bring to the scanner activity
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent toScanner_intent = new Intent(InsertBookActivity.this, ScannerActivity.class); //intent that leads to the ScannerActivity
                startActivity(toScanner_intent);
            }
        });

        retrieveButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                //Put a message in the textView
                EditTextAuthor.setText("Search info...");
                EditTextTitle.setText("Search info...");
                EditTextPublisher.setText("Search info...");
                EditTextPublishedYear.setText("Search info...");

                // Start the AsyncTask.
                // The AsyncTask has a callback that will update the text view.
                ISBNCode = EditTextISBN.getText().toString(); //ISBN Scanned or if manually modified, the last inserted
                makeRetrieveButtonClickable();
                new AsyncTaskQueryISBN(EditTextAuthor, EditTextTitle, EditTextPublisher, EditTextPublishedYear).execute(ISBNCode); //Insert here the ISBN code scanned
                //Note: The execute() method is where you pass in the parameters (separated by commas)
                //that are then passed into doInBackground() by the system. Since this AsyncTask has no parameters, you will leave it blank.


            }
        });


    }

    @Override
    protected void onRestart() {
        super.onRestart();

        String isbn = preferences.getString("isbnNumber", "");
        EditTextISBN.setText(isbn);
    }

    //Implementing the upload or instant camera of a photo to document the book condition
    // After the permission (givenor not given by user on request) to access the camera or the gallery
    //The correspondent intent are started to 1) take a photo or 2) upload it from the gallery using the "start activity for result"
    // depending on if the permission is given or not ("onActivityResult")

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

    //Linked to the startActivityForResult of "onPermissionGranted"
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            switch (requestCode){
                //in case of upload from gallery, the image is taken opening the uri of the resource as a stream,saved with a
                // temporary filename (to retrieve it later)  and setted in the imageview
                case GET_FROM_GALLERY:
                    Uri uri = data.getData();
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        path = imageManager.saveToInternalStorage(bitmap, tempFilename, getApplicationContext());
                        //editor.commit();
                        isChanged = true;
                        ConditionImageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Log.e("bitmap", "Failure: error on bitmap upload");
                    }
                    break;

                //in case of new photo taken -> the photo is saved in internal storage with a
                // temporary filename (to retrieve it later), and the image view is setted with it
                case PHOTO_REQUEST_CODE:
                    bitmap = (Bitmap) data.getExtras().get("data");
                    path = imageManager.saveToInternalStorage(bitmap, tempFilename, getApplicationContext());
                    //editor.commit();
                    isChanged = true;
                    ConditionImageView.setImageBitmap(bitmap);
                    break;
            }
        }
    }

    //insert an "onClickListener" on the image view, that displays the alert dialog questioning if the user want to upload or take the photo
    //The user answer is inserted in the int which (0 for using the camera, 1 for accessing the gallery)
    //The method proceeds then to request the correspondent permissions -> "onPermissionGranted"
    private void setProfileImageListener(){
        ConditionImageView = findViewById(R.id.conditionPhoto);
        imageManager = new ConditionImageManager();

        ConditionImageView.setOnClickListener(new ImageView.OnClickListener(){

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(InsertBookActivity.this);
                builder.setItems(new String[]{getString(R.string.camera), getString(R.string.gallery)}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                requestAppPermission(new String[]{Manifest.permission.CAMERA}, R.string.permission_msg, PHOTO_REQUEST_CODE);
                                break;
                            case 1:
                                requestAppPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},R.string.permission_msg, GET_FROM_GALLERY);
                                break;
                        }
                    }
                }).create().show();
            }
        });

    }


    //Restorethe picture uploaded, taking the path in which is saved from preferences

    private void setImageView(int action) {
       // path = preferences.getString("imagePath", null);
        switch (action) {
            case UPLOAD_IMAGE:
                if (path != null) {
                    Log.d("PathNotNull", "Upload: success");
                    bitmap = imageManager.loadImageFromInternalStorage(path, filename);
                    if (bitmap != null)
                        ConditionImageView.setImageBitmap(bitmap);
                }
                break;
            case RELOAD_IMAGE:
                if (path != null) {
                    Log.d("PathNotNull", "Reload: success");
                    bitmap = imageManager.loadImageFromInternalStorage(path, tempFilename);
                    if (bitmap != null)
                        ConditionImageView.setImageBitmap(bitmap);
                }
                break;
        }

    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //setTexts();
    }

    @Override
    protected void onResume() {
        super.onResume();
       // setTexts();
    }

    //Set all the texts in the XML View elements from the java variables
    private void setTexts(){
        EditTextAuthor.setText(author);
        EditTextTitle.setText(title);
        EditTextISBN.setText(ISBNCode);
        makeRetrieveButtonClickable();

    }

    private void makeRetrieveButtonClickable()
    {
        if(EditTextISBN.getText().toString().length() == 13 || EditTextISBN.getText().toString().length()==10)
            retrieveButton.isClickable();

    }






}
