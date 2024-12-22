package com.example.helpafriend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    private final ArrayList<LeaderboardEntry> leaderboardEntries;

    public LeaderboardAdapter(ArrayList<LeaderboardEntry> leaderboardEntries) {
        this.leaderboardEntries = leaderboardEntries;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_item, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        LeaderboardEntry entry = leaderboardEntries.get(position);
        holder.rankTextView.setText(String.valueOf(position + 1));
        holder.usernameTextView.setText(entry.getUsername());
        holder.numHelpedTextView.setText("Helped " + entry.getNumHelped() + " people");
        holder.pointsTextView.setText("Earned " + entry.getPoints() + " points");
    }

    @Override
    public int getItemCount() {
        return leaderboardEntries.size();
    }

    static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        TextView rankTextView, usernameTextView, numHelpedTextView, pointsTextView;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            rankTextView = itemView.findViewById(R.id.rank_text);
            usernameTextView = itemView.findViewById(R.id.username_text);
            numHelpedTextView = itemView.findViewById(R.id.num_helped_text);
            pointsTextView = itemView.findViewById(R.id.points_text);
        }
    }
}
