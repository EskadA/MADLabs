package com.polito.ignurance.lab01;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class editActivity extends AppCompatActivity {

    EditText name;
    EditText mail;
    EditText bio;

    CharSequence nameText;
    CharSequence mailText;
    CharSequence bioText;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Edit your profile");

        name = (EditText)findViewById(R.id.name);
        mail = (EditText)findViewById(R.id.mail);
        bio = (EditText)findViewById(R.id.bio);

        SharedPreferences prefs = getSharedPreferences("Info", Context.MODE_PRIVATE);
        String textData = prefs.getString(TEXT_DATA_KEY, "No Preferences!");
        TextView outputView = (TextView) findViewById(R.id.outputData);
        outputView.setText(textData);
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
                preferences = getSharedPreferences("Info", Context.MODE_PRIVATE);
                Toast missing;

                SharedPreferences.Editor editor = preferences.edit();
                nameText = name.getText();
                mailText = mail.getText();
                bioText = bio.getText();

                if (nameText != null) {
                    editor.putString("Name", nameText.toString());
                    editor.commit();
                }
                else{
                    missing = Toast.makeText(this, "Insert your name", Toast.LENGTH_SHORT);
                    missing.show();
                    return true;
                }

                if (mailText != null) {
                    editor.putString("Mail", mailText.toString());
                    editor.commit();
                }
                else {
                    missing = Toast.makeText(this, "Insert your mail", Toast.LENGTH_SHORT);
                    missing.show();
                    return true;
                }

                if (bioText != null) {
                    editor.putString("Bio", bioText.toString());
                    editor.commit();
                }
                else {
                    editor.remove("Bio");
                }
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
