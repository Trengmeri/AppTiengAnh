package com.example.test.model;

import java.util.List;

public class Meaning {
    private String partOfSpeech;
    private List<Definition> definitions;

    // Getters and Setters
    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public List<Definition> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(List<Definition> definitions) {
        this.definitions = definitions;
    }
}