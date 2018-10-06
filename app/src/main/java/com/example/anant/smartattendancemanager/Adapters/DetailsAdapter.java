package com.example.anant.smartattendancemanager.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.anant.smartattendancemanager.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.MyViewHolder> {

    private final ArrayList<String> subjectDataset;
    private List<String> mDataset;
    private TextView textView;
    private Map<String, Object> attendanceMap;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(String key);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView subject_textView;
        private CardView detailsCardView;
        private TextView percentageTextView;
        private TextView bunkTextView;
        private TextView attendanceTextView;

        public MyViewHolder(View view) {
            super(view);
            subject_textView = view.findViewById(R.id.subject_text_view);
            attendanceTextView = view.findViewById(R.id.attendance_text_view);
            bunkTextView = view.findViewById(R.id.bunk_text_view);
            percentageTextView = view.findViewById(R.id.percentage_text_view);
            detailsCardView = view.findViewById(R.id.details_card_view);
            detailsCardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            onItemClickListener.onItemClick(subjectDataset.get(position));
        }
    }

    public DetailsAdapter(Map<String, Object> map, OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        attendanceMap = map;
        subjectDataset = new ArrayList<>(map.keySet());
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DetailsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendance_card, parent, false);

        return new MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.subject_textView.setText(subjectDataset.get(position));
        int attended = Integer.parseInt(attendanceMap.get(subjectDataset.get(position)).toString());
        int noOfClasses = Integer.parseInt(attendanceMap.get(subjectDataset.get(position)).toString());
        int percentage;
        if (noOfClasses != 0)
            percentage = (attended / noOfClasses) * 100;
        else percentage = 0;
        holder.percentageTextView.setText(Integer.toString(percentage));
        holder.attendanceTextView.setText("Attended " + attended + "/" + noOfClasses);
        int i = 0;
        while (percentage > 75) {
            noOfClasses++;
            percentage = (attended / noOfClasses) * 100;
            i++;
        }
        holder.bunkTextView.setText("You can bunk next " + i + " classes");
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return subjectDataset.size();
    }
}

