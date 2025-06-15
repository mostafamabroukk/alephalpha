package com.example.alephalpha.service;

import com.example.alephalpha.model.Recipe;
import com.example.alephalpha.model.ShoppingList;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SuggestionService {

    // In-memory DB
    private static final Map<Set<String>, List<String>> COMPLEMENT_MAP = new HashMap<>();
    private static final List<String> GROCERY_ITEMS = new ArrayList<>();
    private static final List<Recipe> RECIPES = new ArrayList<>();

    public SuggestionService(){
        COMPLEMENT_MAP.put(Set.of("gin", "tonic"), List.of("Crodino", "Sanbitter"));
        GROCERY_ITEMS.addAll(List.of("gin", "tonic", "Crodino", "Sanbitter","chicken", "red curry", "rice", "tofu","milk", "bread", "butter", "eggs"));
        RECIPES.add(new Recipe("Chicken Rice", List.of("chicken", "rice")));
    }

//        public List<String> suggestComplementaryItems(Long listId) {
//        ShoppingList list = getListById(listId);
//
//        // Simple pairing rules (could be moved to a database later)
//        Map<String, List<String>> pairingRules = Map.of(
//                "bread", List.of("butter", "jam"),
//                "pasta", List.of("sauce", "parmesan"),
//                "coffee", List.of("milk", "sugar"),
//                "gin", List.of("tonic", "lime")
//        );
//
//        return list.getItems().stream()
//                .filter(item -> !item.isBought())
//                .flatMap(item -> pairingRules.getOrDefault(
//                        item.getName().toLowerCase(),
//                        List.of()
//                ).stream())
//                .distinct()
//                .toList();
//    }

    // --- 1. Autocomplete ---
    public List<String> autocomplete(String prefix) {
        if (prefix == null || prefix.length() < 3)
            return Collections.emptyList();

        String lowerPrefix = prefix.toLowerCase();
        List<String> results = new ArrayList<>();
        for (String item : GROCERY_ITEMS) {
            if (item.toLowerCase().startsWith(lowerPrefix)) {
                results.add(item);
            }
        }
        return results;
    }

    // --- 2. Suggest Complementary Items ---
    public List<String> suggestComplements(List<String> currentItems) {
        List<String> suggestions = new ArrayList<>();
        Set<String> currentSet = new HashSet<>();
        for (String item : currentItems)
            currentSet.add(item.toLowerCase());

        for (Map.Entry<Set<String>, List<String>> entry : COMPLEMENT_MAP.entrySet()) {
            if (currentSet.containsAll(entry.getKey())) {
                suggestions.addAll(entry.getValue());
            }
        }
        return suggestions;
    }

    // --- 3. Propose Recipes ---
    public List<Map<String, Object>> suggestRecipes(List<String> currentItems) {
        List<Map<String, Object>> suggestions = new ArrayList<>();
        Set<String> currentSet = new HashSet<>();
        for (String item : currentItems) currentSet.add(item.toLowerCase());

        for (Recipe recipe : RECIPES) {
            if (currentSet.containsAll(recipe.getRequiredIngredients())) {
                // User already has all ingredients, no missing
                suggestions.add(Map.of(
                        "recipe", recipe.getName(),
                        "missingIngredients", Collections.emptyList()));
            } else {
                // Check missing ingredients
                List<String> missing = new ArrayList<>();
                for (String req : recipe.getRequiredIngredients()) {
                    if (!currentSet.contains(req)) {
                        missing.add(req);
                    }
                }
                if (!missing.isEmpty()) {
                    suggestions.add(Map.of(
                            "recipe", recipe.getName(),
                            "missingIngredients", missing));
                }
            }
        }
        return suggestions;
    }

    // --- 4. Highlight Missing Ingredients for Classic Dishes ---
    // (Merged with recipe missing ingredients above)
}
