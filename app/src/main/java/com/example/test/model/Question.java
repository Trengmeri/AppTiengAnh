package com.example.test.model;

import java.util.List;

public class Question {
    private int id;
    private String quesContent;
    private String keyword;
    private String quesType;
    private String skillType;
    private int point;
    private String createBy;
    private String createAt;
    private String updateBy;
    private String updateAt;
    private List<Answer> answers;
    private List<Lesson> lessonQuestions;
    private List<QuestionChoice> questionChoices;
    private Object learningMaterial;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuesContent() {
        return quesContent;
    }

    public void setQuesContent(String quesContent) {
        this.quesContent = quesContent;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getQuesType() {
        return quesType;
    }

    public void setQuesType(String quesType) {
        this.quesType = quesType;
    }

    public String getSkillType() {
        return skillType;
    }

    public void setSkillType(String skillType) {
        this.skillType = skillType;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
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

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public List<Lesson> getLessonQuestions() {
        return lessonQuestions;
    }

    public void setLessonQuestions(List<Lesson> lessonQuestions) {
        this.lessonQuestions = lessonQuestions;
    }

    public List<QuestionChoice> getQuestionChoices() {
        return questionChoices;
    }

    public void setQuestionChoices(List<QuestionChoice> questionChoices) {
        this.questionChoices = questionChoices;
    }

    public Object getLearningMaterial() {
        return learningMaterial;
    }

    public void setLearningMaterial(Object learningMaterial) {
        this.learningMaterial = learningMaterial;
    }
}