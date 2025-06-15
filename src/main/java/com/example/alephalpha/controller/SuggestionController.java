package com.example.alephalpha.controller;

import com.example.alephalpha.model.ShoppingList;
import com.example.alephalpha.repository.ShoppingListRepository;
import com.example.alephalpha.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/suggestions")
@RequiredArgsConstructor
public class SuggestionController {
    private final ShoppingListService shoppingListService;
    private final ShoppingListRepository shoppingListRepository;

    //GET /api/suggestions?q={partialName}
    @GetMapping
    public List<String> suggestItems(@RequestParam String q) {
        return shoppingListService.suggestItems(q);
    }

//    GET /api/suggestions/{listId}/recommendations
//    given a shopping list, suggest relevant items.
//    @GetMapping("/{listId}/recommendations")
//    public List<String> recommendItems(@PathVariable Long listId) {
//        return shoppingListService.suggestComplementaryItems(listId);
//    }

    // Add to ShoppingListService.java
    public List<String> suggestComplementaryItems(Long listId) {
        ShoppingList list = shoppingListRepository.findById(listId).orElseThrow();

        // Simple hardcoded rules (e.g., "bread" → "butter")
        Map<String, List<String>> rules = Map.of(
                "bread", List.of("butter", "jam"),
                "pasta", List.of("sauce", "cheese")
        );

        return list.getItems().stream()
                .flatMap(shoppingListItem -> rules.getOrDefault(shoppingListItem.getId(), List.of()).stream())
                .distinct()
                .toList();
    }
}