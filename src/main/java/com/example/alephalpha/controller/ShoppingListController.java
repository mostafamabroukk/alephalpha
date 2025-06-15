package com.example.alephalpha.controller;

import com.example.alephalpha.exception.NotFoundException;
import com.example.alephalpha.model.*;
import com.example.alephalpha.repository.*;
import com.example.alephalpha.service.*;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
@Tag(name = "Shopping List APIs", description = "Manage shopping list")
public class ShoppingListController {
    private final ShoppingListService shoppingListService;
    private final ShoppingListRepository shoppingListRepository;

    // [1]
    @Operation(summary = "Create New List",
            description = "Create a new shopping list with a given title.")
    @ApiResponse(responseCode = "200", description = "List created successfully")
    @PostMapping
    public ResponseEntity<ShoppingList> createList(
            @Parameter(description = "Title of the shopping list", example = "Grocery")
            @RequestParam String title) {
        return ResponseEntity.ok(shoppingListService.createList(title));
    }
    // ======================================================
    // [2]`
    @DeleteMapping("/{listId}")
    @Operation(summary = "Delete a shopping list by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List deleted successfully"),
            @ApiResponse(responseCode = "404", description = "List not found")
    })
    public ResponseEntity<Void> deleteList(@PathVariable Long listId) {
        shoppingListService.deleteList(listId);
        return ResponseEntity.ok().build();
    }
    // ======================================================
    // [3]
    @Operation(summary = "Retrieve a shopping list", description = "Retrieves a shopping list with all items and calculated totals.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List found"),
            @ApiResponse(responseCode = "404", description = "List not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ShoppingList> getListById(@PathVariable Long id) {
        return shoppingListRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Shopping list not found with ID: " + id));
    }
    // ======================================================
    // [4]
    @Operation(summary = "Get All shopping lists", description = "Retrieves all shopping lists with their names.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lists retrieved successfully")
    })
    @GetMapping()
    public List<ShoppingList> getAllLists() {
        return shoppingListRepository.findAll();
    }

    // ======================================================
    // ======================================================

    @Operation(summary = "Add item to list", description = "Adds an existing item into a shopping list. Default quantity is 1.")
    @PostMapping("/{listId}/additem/{itemId}")
    public ResponseEntity<ShoppingListItem> addItemToList(
            @PathVariable Long listId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(shoppingListService.addItemToList(listId, itemId, 1));
    }


    // ======================================================
    @Operation(summary = "Add item with specific quantity", description = "Adds a specific quantity of an existing item to the list.")
    @PostMapping("/{listId}/additem/{itemId}/quantity/{qty}")
    public ResponseEntity<ShoppingListItem> addItemToListWithQty(
            @PathVariable Long listId,
            @PathVariable Long itemId,
            @PathVariable Integer qty) {
        return ResponseEntity.ok(shoppingListService.addItemToList(listId, itemId, qty));
    }
    // ======================================================
    @Operation(
            summary = "Decrease item quantity in list",
            description = "Decreases the quantity of an item in the shopping list by 1."
    )
    @PatchMapping("/{listId}/items/{itemId}/decrease")
    public void decreaseItemQuantity(
            @PathVariable Long listId,
            @PathVariable Long itemId) {
        shoppingListService.decreaseItemQuantity(listId, itemId);
    }
    // ======================================================
    @Operation(
            summary = "Delete item from a list",
            description = "Delete the item with all quantities from the shopping."
    )
    @DeleteMapping("/{listId}/items/{itemId}/delete")
    public void deleteItemFromList(
            @PathVariable Long listId,
            @PathVariable Long itemId) {
        shoppingListService.deleteItemFromList(listId, itemId);
    }
    // ======================================================
    @Operation(
            summary = "Buy shopping list",
            description = "Simulates payment and clears the list of all items."
    )
    @PostMapping("/{listId}/buy")
    public String buyShoppingList(@PathVariable Long listId) {
        shoppingListService.buyShoppingList(listId);
        return "Payment successful. List cleared!";
    }

}