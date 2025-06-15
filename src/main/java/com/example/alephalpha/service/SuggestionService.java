package com.example.alephalpha.service;

import com.example.alephalpha.model.Item;
import com.example.alephalpha.model.Recipe;
import com.example.alephalpha.model.ShoppingList;
import com.example.alephalpha.model.ShoppingListItem;
import com.example.alephalpha.repository.ShoppingListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SuggestionService {

    // In-memory DB
    private static final Map<Set<String>, List<String>> COMPLEMENT_MAP = new HashMap<>();
    private static final List<Recipe> RECIPES = new ArrayList<>();
    private final ItemService itemService;
    private ShoppingListRepository shoppingListRepository;
    private static final int PREFIX_LEN = 1;

    static{
        COMPLEMENT_MAP.put(Set.of("gin", "tonic"), List.of("Crodino", "Sanbitter"));
        COMPLEMENT_MAP.put(Set.of("bread"), List.of("butter", "jam"));
        COMPLEMENT_MAP.put(Set.of("pasta"), List.of("sauce", "cheese"));

        RECIPES.add(new Recipe("Chicken Rice", List.of("chicken", "rice")));
        RECIPES.add(new Recipe("Pasta with Sauce", List.of("pasta", "sauce", "cheese")));
    }

    // 1. Autocomplete suggestions for item names
    public List<String> autocomplete(String prefix) {
        if (prefix == null || prefix.length() < PREFIX_LEN)
            return Collections.emptyList();

        String lowerPrefix = prefix.toLowerCase();
        List<String> results = new ArrayList<>();

        List<Item> alLItems = itemService.getAllItems();
        for (Item item : alLItems) {
            String itemName = item.getName();
            if (itemName.toLowerCase().startsWith(lowerPrefix)) {
                results.add(itemName);
            }
        }
        return results;
    }

    // 2. Suggest complementary items based on current shopping list
    public List<String> suggestComplementaryItems(Long listId) {
        ShoppingList list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new NoSuchElementException("ShoppingList not found"));

        Set<String> currentItems = new HashSet<>();
        for (ShoppingListItem sli : list.getItems()) {
            currentItems.add(sli.getItem().getName().toLowerCase());
        }

        List<String> suggestions = new ArrayList<>();
        for (Map.Entry<Set<String>, List<String>> entry : COMPLEMENT_MAP.entrySet()) {
            if (currentItems.containsAll(entry.getKey())) {
                suggestions.addAll(entry.getValue());
            }
        }
        suggestions.removeIf(currentItems::contains);
        return new ArrayList<>(new LinkedHashSet<>(suggestions));
    }

    // 3. Suggest recipes based on current shopping list items
    public List<Map<String, Object>> suggestRecipes(Long listId) {
        ShoppingList list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new NoSuchElementException("ShoppingList not found"));

        Set<String> currentItems = new HashSet<>();
        for (ShoppingListItem sli : list.getItems()) {
            currentItems.add(sli.getItem().getName().toLowerCase());
        }

        List<Map<String, Object>> suggestions = new ArrayList<>();
        for (Recipe recipe : RECIPES) {
            Set<String> required = new HashSet<>();
            for (String ing : recipe.getRequiredIngredients()) {
                required.add(ing.toLowerCase());
            }

            if (currentItems.containsAll(required)) {
                suggestions.add(Map.of(
                        "recipe", recipe.getName(),
                        "missingIngredients", Collections.emptyList()));
            } else {
                List<String> missing = new ArrayList<>();
                for (String ing : required) {
                    if (!currentItems.contains(ing)) {
                        missing.add(ing);
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
}