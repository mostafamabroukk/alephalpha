package com.example.alephalpha.controller;

import com.example.alephalpha.model.*;
import com.example.alephalpha.service.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    @Operation(
            summary = "Create a new grocery item",
            description = "Create a new global item with name, price, and optional category"
    )
    @PostMapping("/new")
    public Item createItem(
            @Parameter(description = "Item name", example = "Milk") @RequestParam String name,
            @Parameter(description = "Item price", example = "2.49") @RequestParam Double price,
            @Parameter(description = "Optional item category", example = "Dairy") @RequestParam(required = false) String category
    ) {
        return itemService.createItem(name, price, category);
    }


    @Operation(summary = "Get All Items", description = "Retrieves all grocery items in the global inventory.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Items retrieved successfully")
    })
    @GetMapping
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    @Operation(
            summary = "Delete a grocery item from inventory",
            description = "Remove an item from the global inventory by its ID"
    )
    @DeleteMapping("/{itemId}")
    public void deleteItem(
            @Parameter(description = "ID of the item to delete", example = "1") @PathVariable Long itemId
    ) {
        itemService.deleteItem(itemId);
    }
}
