package com.example.test.model;

public class Point {
    private int id;
    private int value;

    // Constructor
    public Point(int id, int value) {
        this.id = id;
        this.value = value;
    }

    // Getter and Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
