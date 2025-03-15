package com.example.helpafriend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecentPostsAdapter extends RecyclerView.Adapter<RecentPostsAdapter.PostViewHolder> {

    private List<Post> posts;

    public RecentPostsAdapter(List<Post> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);

        holder.usernameView.setText(post.getUsername());
        holder.titleView.setText(post.getTitle());
        holder.contentView.setText(post.getContent());
        holder.dateView.setText(post.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView usernameView, titleView, contentView, dateView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameView = itemView.findViewById(R.id.postUsername);
            titleView = itemView.findViewById(R.id.postTitle);
            contentView = itemView.findViewById(R.id.postContent);
            dateView = itemView.findViewById(R.id.postDate);
        }
    }
}
