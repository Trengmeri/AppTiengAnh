package com.example.test.model;

import java.util.List;

public class Course {
    private int id;
    private String name;
    private String intro;
    private int diffLevel;
    private int recomLevel;
    private String courseType;
    private String speciField;
    private String createBy;
    private String createAt;
    private String updateBy;
    private String updateAt;
    private int sumLesson;
    private List<Integer> lessonIds;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public int getDiffLevel() {
        return diffLevel;
    }

    public void setDiffLevel(int diffLevel) {
        this.diffLevel = diffLevel;
    }

    public int getRecomLevel() {
        return recomLevel;
    }

    public void setRecomLevel(int recomLevel) {
        this.recomLevel = recomLevel;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getSpeciField() {
        return speciField;
    }

    public void setSpeciField(String speciField) {
        this.speciField = speciField;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public int getSumLesson() {
        return sumLesson;
    }

    public void setSumLesson(int sumLesson) {
        this.sumLesson = sumLesson;
    }

    public List<Integer> getLessonIds() {
        return lessonIds;
    }

    public void setLessonIds(List<Integer> lessonIds) {
        this.lessonIds = lessonIds;
    }
}