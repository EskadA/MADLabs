package com.polito.ignorance.lab03;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polito.ignorance.lab03.tools.User;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG_CREDENTIAL = "Credentials";
    private static final String TAG_ERROR = "Error";

    private Button btnSignup;
    private EditText inputEmail, inputPass, inputPassRepeat, inputName, inputCity;
    private TextView btnBack;

    private FirebaseAuth auth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //View
        findViews();

        //Button listeners
        setButtonListener();

        //Init Firebase
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(inputEmail.getText().toString());
        arrayList.add(inputPass.getText().toString());
        arrayList.add(inputPassRepeat.getText().toString());
        arrayList.add(inputName.getText().toString());
        arrayList.add(inputCity.getText().toString());
        outState.putStringArrayList(TAG_CREDENTIAL, arrayList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<String> arrayList = savedInstanceState.getStringArrayList(TAG_CREDENTIAL);

        if (arrayList != null) {
            inputEmail.setText(arrayList.get(0));
            inputPass.setText(arrayList.get(1));
            inputPassRepeat.setText(arrayList.get(2));
            inputName.setText(arrayList.get(3));
            inputCity.setText(arrayList.get(4));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signup_btn_register:
                signUpUser(inputEmail.getText().toString(),
                        inputPass.getText().toString(),
                        inputPassRepeat.getText().toString(),
                        inputName.getText().toString(),
                        inputCity.getText().toString());
                break;
            case R.id.signup_btn_back:
                onBackPressed();
                finish();
                break;
        }
    }

    private void signUpUser(final String email,
                            final String password,
                            final String passRepeat,
                            final String name,
                            final String city) {

        if (controlStrings(email, password, passRepeat, name, city)) {
            auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()) {
                                taskExceptionManager(task);
                            } else{
                                auth.signInWithEmailAndPassword(email, password);
                                writeNewUser(name, email, city);
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpActivity.this);
                                alertDialog.setTitle(R.string.success);
                                alertDialog.setMessage(getString(R.string.register_success));
                                alertDialog.setPositiveButton(R.string.go_to_dashboard, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        startActivity(new Intent(SignUpActivity.this, DashboardActivity.class));
                                        finish();
                                    }
                                }).create().show();
                            }
                        }
                    });
        }

    }

    private void writeNewUser(String name, String email, String city) {
        User user = new User(name, email, city, "");
        String userID = email.replace(",",",,").replace(".", ",");
        database.child("users").child(userID).setValue(user);

    }

    private boolean controlStrings(String email, final String password, final String passRepeat, String name, String city){
        if (email.isEmpty() || password.isEmpty() || passRepeat.isEmpty() || name.isEmpty() || city.isEmpty()){
            if (city.isEmpty()){
                inputCity.setError(getString(R.string.error_invalid_city));
                inputCity.requestFocus();
            }
            if (name.isEmpty()){
                inputName.setError(getString(R.string.error_invalid_name));
                inputName.requestFocus();
            }
            if (passRepeat.isEmpty()){
                inputPassRepeat.setError(getString(R.string.error_invalid_repeat_password));
                inputPassRepeat.requestFocus();
            }
            if (password.isEmpty()){
                inputPass.setError(getString(R.string.error_invalid_password));
                inputPass.requestFocus();
            }
            if (email.isEmpty()){
                inputEmail.setError(getString(R.string.error_invalid_email));
                inputEmail.requestFocus();
            }
            return false;
        } else if (!password.equals(passRepeat)){
            inputPassRepeat.setError(getString(R.string.incorrect_password));
            inputPassRepeat.requestFocus();
            return false;
        }

        return true;
    }

    private void taskExceptionManager(Task<AuthResult> task){
        try {
            throw task.getException();
        } catch(FirebaseAuthWeakPasswordException e) {
            inputPass.setError(getString(R.string.error_weak_password));
            inputPass.requestFocus();
        } catch(FirebaseAuthInvalidCredentialsException e) {
            inputEmail.setError(getString(R.string.error_invalid_email));
            inputEmail.requestFocus();
        } catch(FirebaseAuthUserCollisionException e) {
            inputEmail.setError(getString(R.string.error_user_exists));
            inputEmail.requestFocus();
        } catch (FirebaseNetworkException e){
            Toast.makeText(SignUpActivity.this, R.string.error_network, Toast.LENGTH_SHORT).show();
        } catch(Exception e) {
            Toast.makeText(SignUpActivity.this, R.string.something_wrong, Toast.LENGTH_SHORT).show();
            Log.e(TAG_ERROR, e.getMessage());
        }
    }

    private void findViews(){
        btnSignup           = (Button)findViewById(R.id.signup_btn_register);
        inputEmail          = (EditText)findViewById(R.id.signup_email);
        inputPass           = (EditText)findViewById(R.id.signup_password);
        inputPassRepeat     = (EditText)findViewById(R.id.signup_repeat_password);
        inputName           = (EditText)findViewById(R.id.signup_displayed_name);
        inputCity           = (EditText)findViewById(R.id.signup_city);
        btnBack             = (TextView)findViewById(R.id.signup_btn_back);
    }

    private void setButtonListener(){
        btnSignup.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }
}
