package com.example.anant.smartattendancemanager.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.anant.smartattendancemanager.R;


// In this case, the fragment displays simple text based on the page
public class SubjectsFragment extends Fragment {

    public void SubjectsFragment() {
    }

    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

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
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subjects, container, false);
        TextView textView = (TextView) view.findViewById(R.id.text_view);
        textView.setText("Fragment #" + mPage);
        return view;
    }
}