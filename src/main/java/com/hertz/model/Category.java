package com.hertz.model;

public enum Category {
    POETRY("poetry"),
    THRILLER("thriller"),
    MYSTERY("mystery"),
    SCIENCE_FICTION("science fiction");


    private final String name;

    Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
