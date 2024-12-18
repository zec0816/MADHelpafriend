package com.example.helpafriend;

public class Post {
    private String title;
    private String content;
    private String createdAt;
    private String username; // Add username

    public Post(String title, String content, String createdAt, String username) {
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.username = username; // Initialize username
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUsername() {
        return username; // Getter for username
    }
}
