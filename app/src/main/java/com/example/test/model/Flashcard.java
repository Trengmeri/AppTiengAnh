package com.example.test.model;

import android.text.TextUtils;

import java.util.List;

public class Flashcard {
    private int id;
    private String word;
    private String definitions;
    private String partOfSpeech;
    private String phoneticText;
    private String phoneticAudio;
    private String addedDate;
    private String lastReviewed;
    private String examples;
    private boolean learnedStatus;
    private String vietNameseMeaning;

    public Flashcard(int id, String word, List<Integer> definitionIndices, int partOfSpeechIndex) {
        this.id = id;
        this.word = word;
        this.definitions = convertDefinitionsToString(definitionIndices);
        this.partOfSpeech = convertPartOfSpeech(partOfSpeechIndex);
    }
    // Chuyển danh sách định nghĩa thành chuỗi, ngăn cách bằng dấu phẩy
    private String convertDefinitionsToString(List<Integer> definitionIndices) {
        return definitionIndices != null ? TextUtils.join(", ", definitionIndices) : "No definitions";
    }
    // Hàm chuyển đổi chỉ mục thành loại từ
    private String convertPartOfSpeech(int index) {
        String[] partsOfSpeech = {"Noun", "Verb", "Adjective", "Adverb"}; // Cập nhật danh sách theo app của bạn
        return (index >= 0 && index < partsOfSpeech.length) ? partsOfSpeech[index] : "Unknown";
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getDefinitions() {
        return definitions;
    }

    public void setDefinitions(String definitions) {
        this.definitions = definitions;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public String getPhoneticText() {
        return phoneticText;
    }

    public void setPhoneticText(String phoneticText) {
        this.phoneticText = phoneticText;
    }

    public String getPhoneticAudio() {
        return phoneticAudio;
    }

    public void setPhoneticAudio(String phoneticAudio) {
        this.phoneticAudio = phoneticAudio;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getLastReviewed() {
        return lastReviewed;
    }

    public void setLastReviewed(String lastReviewed) {
        this.lastReviewed = lastReviewed;
    }

    public String getExamples() {
        return examples;
    }

    public void setExamples(String examples) {
        this.examples = examples;
    }

    public boolean isLearnedStatus() {
        return learnedStatus;
    }

    public void setLearnedStatus(boolean learnedStatus) {
        this.learnedStatus = learnedStatus;
    }

    public String getVietNameseMeaning() {
        return vietNameseMeaning;
    }

    public void setVietNameseMeaning(String vietNameseMeaning) {
        this.vietNameseMeaning = vietNameseMeaning;
    }
}