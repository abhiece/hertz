package com.hertz.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Member {
    @JsonProperty("name")
    private String name;
    @JsonProperty("listOfBooksLoaned")
    private List<Book> listOfBooksLoaned;
}
