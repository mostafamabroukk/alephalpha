package com.example.alephalpha.model;
import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Recipe {
    private String name;
    private List<String> requiredIngredients;
}
