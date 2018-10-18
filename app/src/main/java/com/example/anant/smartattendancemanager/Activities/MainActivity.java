package com.example.anant.smartattendancemanager.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.anant.smartattendancemanager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private static int editTextID = 1;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                Intent intent = new Intent(MainActivity.this, TimeTableActivity.class);
                startActivity(intent);
            }
        });
        toolbar = findViewById(R.id.toolbar);
        final ProgressBar progressBar = findViewById(R.id.indeterminateBar);
        setSupportActionBar(toolbar);
        linearLayout = findViewById(R.id.subject_linear_layout);
        createTextView();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        UID = user.getUid();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users/" + UID + "/subjects");
        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map != null) {
                        Intent intent = new Intent(MainActivity.this, TimeTableActivity.class);
                        ref.removeEventListener(this);
                        startActivity(intent);
                    } else progressBar.setVisibility(View.GONE);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }


    private void saveData() {
        HashMap<String, String> subjects = new HashMap();
        for (int i = 1; i <= editTextID; i++) {
            EditText editText = (EditText) findViewById(i);
            try {
                String subject = editText.getText().toString();
                if (subject != null && subject.length() > 0)
                    subjects.put(subject, "0/0");
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + UID + "/subjects", subjects);
        mDatabase.updateChildren(childUpdates);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_subjects_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_subjects) {
            createTextView();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void createTextView() {
        EditText editText = new EditText(this);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        editText.setId(editTextID);
        editText.setBackgroundResource(R.drawable.edit_text_border);
        editText.setHint(R.string.edit_text_hint);
        editText.setPadding(padding, padding, padding, padding);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        lp.setMargins(padding, padding, padding, padding);
        editText.setLayoutParams(lp);
        linearLayout.addView(editText);
        editText.requestFocus();
        editTextID++;
    }
}
