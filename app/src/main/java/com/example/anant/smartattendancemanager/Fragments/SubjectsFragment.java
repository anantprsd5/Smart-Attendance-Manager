package com.example.anant.smartattendancemanager.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.anant.smartattendancemanager.Activities.AttendanceActivity;
import com.example.anant.smartattendancemanager.Adapters.SubjectAdapter;
import com.example.anant.smartattendancemanager.Presenter.TimeTablePresenter;
import com.example.anant.smartattendancemanager.R;
import com.example.anant.smartattendancemanager.View.TimeTableView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// In this case, the fragment displays simple text based on the page
public class SubjectsFragment extends Fragment implements TimeTableView {

    private FirebaseAuth mAuth;
    private String UID;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private SubjectAdapter mAdapter;
    private int page;
    private DatabaseReference mDatabase;
    private HashMap<String, String> subjects;
    private TimeTablePresenter timeTablePresenter;

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

        //Firebase Initializations
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        UID = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FloatingActionButton floatingActionButton = getActivity().findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), AttendanceActivity.class);
            startActivity(intent);
        });

        timeTablePresenter = new TimeTablePresenter(UID, this);
        timeTablePresenter.listSubjects();

        return view;
    }

    @Override
    public void onTableFetched(Map<String, Object> map) {
        if (map != null) {
            final List<String> subjectDataset = new ArrayList<>(map.keySet());
            // specify an adapter (see also next example)
            mAdapter = new SubjectAdapter(subjectDataset, (position, isChecked) -> {
                if (isChecked) {
                    subjects.put(subjectDataset.get(position), "0/0");
                    timeTablePresenter.saveData(page - 1, subjects, mDatabase);
                } else {
                    subjects.remove(subjectDataset.get(position));
                    timeTablePresenter.saveData(page - 1, subjects, mDatabase);
                }
            });
            mRecyclerView.setAdapter(mAdapter);
        }
    }
}