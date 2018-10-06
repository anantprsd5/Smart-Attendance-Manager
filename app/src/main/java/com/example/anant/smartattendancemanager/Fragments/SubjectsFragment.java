package com.example.anant.smartattendancemanager.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anant.smartattendancemanager.Activities.DetailActivity;
import com.example.anant.smartattendancemanager.Adapters.SubjectAdapter;
import com.example.anant.smartattendancemanager.Days;
import com.example.anant.smartattendancemanager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;


// In this case, the fragment displays simple text based on the page
public class SubjectsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private String UID;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private SubjectAdapter mAdapter;
    private int page;
    private DatabaseReference mDatabase;
    private HashMap<String, String> subjects;

    public void SubjectsFragment() {
    }

    public static final String ARG_PAGE = "ARG_PAGE";

    public static SubjectsFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        SubjectsFragment fragment = new SubjectsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("ARG_PAGE", 0);
        subjects = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subjects, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        UID = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FloatingActionButton floatingActionButton = getActivity().findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                startActivity(intent);
            }
        });


        // Get a reference to our posts
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("/users/" + UID + "/subjects");
        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                final List<String> subjectDataset = new ArrayList<>(map.keySet());
                // specify an adapter (see also next example)
                mAdapter = new SubjectAdapter(subjectDataset, new SubjectAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, boolean isChecked) {
                        if (isChecked) {
                            subjects.put(Integer.toString(position), subjectDataset.get(position));
                            saveData(page-1);
                        } else {
                            subjects.remove(Integer.toString(position));
                            saveData(page-1);
                        }
                    }
                });
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void saveData(int position) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + UID + "/table" + "/" + Days.values()[position], subjects);
        mDatabase.updateChildren(childUpdates);
    }
}