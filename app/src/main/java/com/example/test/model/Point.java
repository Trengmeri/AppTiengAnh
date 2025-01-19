package com.example.test.model;

public class Point {
    private int id;
    private int stuTime;
    private int totalPoints;
    private User user;
    private Lesson lesson;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStuTime() {
        return stuTime;
    }

    public void setStuTime(int stuTime) {
        this.stuTime = stuTime;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }
}
