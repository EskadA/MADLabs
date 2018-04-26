package com.polito.ignorance.lab03;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.data.DataBufferSafeParcelable;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class SearchBookActivity extends AppCompatActivity implements View.OnClickListener {

    EditText searchbar;
    Button srcAuthor, srcGenre, srcPublisher, srcTitle;

    TextView prova;

    private FirebaseUser authUser;
    private DatabaseReference database;
    private DatabaseReference ref;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);

        searchbar = findViewById(R.id.searchbar);
        srcAuthor = findViewById(R.id.author);
        srcGenre = findViewById(R.id.genre);
        srcPublisher = findViewById(R.id.publisher);
        srcTitle = findViewById(R.id.title);
        prova = findViewById(R.id.textView3);

        srcAuthor.setOnClickListener(this);
        srcGenre.setOnClickListener(this);
        srcPublisher.setOnClickListener(this);
        srcTitle.setOnClickListener(this);

        database = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.author:

                break;
            case R.id.genre:
                break;
            case R.id.publisher:
                break;
            case R.id.title:
                String inputText = searchbar.getText().toString().toLowerCase();

                database.child("title").orderByKey().startAt(inputText).endAt(inputText + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String s = "";
                        for(DataSnapshot i : dataSnapshot.getChildren()){
                            s = s+i.getKey();
                        }
                        prova.setText(s);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;
        }
    }
}
