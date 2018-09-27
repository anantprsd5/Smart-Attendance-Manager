package com.example.anant.smartattendancemanager.Adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.anant.smartattendancemanager.R;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.MyViewHolder> {

    private List<String> mDataset;
    private final OnItemClickListener listener;
    SparseBooleanArray sparseBooleanArray;

    public interface OnItemClickListener {
        void onItemClick(int position, boolean isChecked);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView subject_textView;

        public MyViewHolder(View view) {
            super(view);
            subject_textView = view.findViewById(R.id.subject_text);
            subject_textView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (!sparseBooleanArray.get(position))
            {
                sparseBooleanArray.put(position,true);
                listener.onItemClick(position, true);
                // calling the method in main activity Because: in our case mainActivity our created interface for clicklisteners
                notifyItemChanged(position);
            }
            else // if clicked item is already selected
            {
                sparseBooleanArray.put(position,false);
                listener.onItemClick(position, false);
                 // calling the method in main activity Because: in our case mainActivity our created interface for clicklisteners
                notifyItemChanged(position);
            }
        }
    }

    public SubjectAdapter(List<String> myDataset, OnItemClickListener listener) {
        mDataset = myDataset;
        this.listener = listener;
        sparseBooleanArray = new SparseBooleanArray();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SubjectAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subjects_list, parent, false);

        return new MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.subject_textView.setText(mDataset.get(position));
        if (sparseBooleanArray.get(position))
        {
            holder.subject_textView.setBackgroundColor(Color.BLUE);
        }
        else
        {
            holder.subject_textView.setBackgroundColor(Color.WHITE);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
