package com.example.test.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Discussion {
    private Long id;
    private String content;

    @SerializedName("user_id")
    private User user;

    @SerializedName("lesson_id")
    private Lesson lesson;

    @SerializedName("parent_id")
    private Discussion parentDiscussion;

    private List<Discussion> replies;
    private int numLike;


    public Discussion(Long id, String content, User user, Lesson lesson, Discussion parentDiscussion, int numLike) {
        id = id;
        this.content = content;
        this.user = user;
        this.lesson = lesson;
        this.parentDiscussion = parentDiscussion;
        this.numLike = numLike;
    }

    // Getter và Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Lesson getLesson() { return lesson; }
    public void setLesson(Lesson lesson) { this.lesson = lesson; }

    public Discussion getParentDiscussion() { return parentDiscussion; }
    public void setParentDiscussion(Discussion parentDiscussion) { this.parentDiscussion = parentDiscussion; }

    public List<Discussion> getReplies() { return replies; }
    public void setReplies(List<Discussion> replies) { this.replies = replies; }

    public int getNumLike() { return numLike; }
    public void setNumLike(int numLike) { this.numLike = numLike; }
}
