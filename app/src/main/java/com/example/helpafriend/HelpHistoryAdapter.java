package com.example.helpafriend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HelpHistoryAdapter extends RecyclerView.Adapter<HelpHistoryAdapter.HelpHistoryViewHolder> {

    private List<HelpHistory> helpHistoryList;

    public HelpHistoryAdapter(List<HelpHistory> helpHistoryList) {
        this.helpHistoryList = helpHistoryList;
    }

    @NonNull
    @Override
    public HelpHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_help_history, parent, false);
        return new HelpHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HelpHistoryViewHolder holder, int position) {
        HelpHistory history = helpHistoryList.get(position);

        // Bind data to the views
        holder.okuNameTextView.setText(history.getOkuName());
        holder.helpDateTextView.setText(history.getHelpDate());
        holder.pointsTextView.setText("+50 points"); // Static text for points
    }

    @Override
    public int getItemCount() {
        return helpHistoryList.size();
    }

    // Static Inner Class for the ViewHolder
    public static class HelpHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView okuNameTextView;
        TextView helpDateTextView;
        TextView pointsTextView;

        public HelpHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views from the layout
            okuNameTextView = itemView.findViewById(R.id.okuNameTextView);
            helpDateTextView = itemView.findViewById(R.id.helpDateTextView);
            pointsTextView = itemView.findViewById(R.id.pointsTextView); // New TextView for points
        }
    }
}