package com.example.alephalpha.controller;

import com.example.alephalpha.model.ShoppingList;
import com.example.alephalpha.repository.ShoppingListRepository;
import com.example.alephalpha.service.ShoppingListService;
import com.example.alephalpha.service.SuggestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    private final SuggestionService suggestionService;

    @Operation(summary = "Autocomplete item names",
            description = "Given a partial item name, returns a list of matching grocery items.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autocomplete results")
    })
    @GetMapping
    public List<String> suggestItems(
            @Parameter(description = "Partial item name prefix", example = "br") @RequestParam String q) {
        return suggestionService.autocomplete(q);
    }

    @Operation(summary = "Recommend complementary items",
            description = "Given a shopping list ID, returns items that complement the current items in the list.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of complementary items"),
            @ApiResponse(responseCode = "404", description = "Shopping list not found")
    })
    @GetMapping("/{listId}/recommendations")
    public List<String> recommendItems(
            @Parameter(description = "Shopping list ID", example = "1") @PathVariable Long listId) {
        return suggestionService.suggestComplementaryItems(listId);
    }

    @Operation(summary = "Suggest recipes based on shopping list items",
            description = "Suggests recipes and highlights missing ingredients for the given shopping list.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of recipe suggestions with missing ingredients"),
            @ApiResponse(responseCode = "404", description = "Shopping list not found")
    })
    @GetMapping("/{listId}/recipes")
    public List<Map<String, Object>> recommendRecipes(
            @Parameter(description = "Shopping list ID", example = "1") @PathVariable Long listId) {
        return suggestionService.suggestRecipes(listId);
    }
}