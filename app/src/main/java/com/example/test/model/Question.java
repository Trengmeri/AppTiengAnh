package com.example.test.model;//package com.example.test.model;
//
//public class Question {
//    private int id;
//    private String content;
//
//    // Constructor
//    public Question(int id, String content) {
//        this.id = id;
//        this.content = content;
//    }
//
//    // Getter and Setter
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getContent() {
//        return content;
//    }
//
//    public void setContent(String content) {
//        this.content = content;
//    }
//}
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Question {

    @SerializedName("quesContent")
    private String quesContent;

    @SerializedName("quesType")
    private String quesType;

    @SerializedName("point")
    private int point;

    @SerializedName("choiceContent")
    private List<Choice> questionChoices;


    @SerializedName("choiceKey")
    private String choiceKey;

    // Getters và Setters
    public String getQuesContent() {
        return quesContent;
    }

    public void setQuesContent(String quesContent) {
        this.quesContent = quesContent;
    }

    public String getQuesType() {
        return quesType;
    }

    public void setQuesType(String quesType) {
        this.quesType = quesType;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
    public List<Choice> getQuestionChoices() {
        return questionChoices;
    }

    public void setQuestionChoices(List<Choice> questionChoices) {
        this.questionChoices = questionChoices;
    }

    public String getChoiceKey() {
        return choiceKey;
    }

    public void setChoiceKey(String choiceKey) {
        this.choiceKey = choiceKey;
    }
}

