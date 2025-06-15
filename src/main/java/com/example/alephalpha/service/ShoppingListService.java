package com.example.alephalpha.service;

import com.example.alephalpha.model.*;
import com.example.alephalpha.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ShoppingListService {
    private final ShoppingListRepository shoppingListRepository;
    private final ItemRepository itemRepository;
    private final ShoppingListItemRepository shoppingListItemRepository;

    // 1. Create a new shopping list
    public ShoppingList createList(String title) {
        ShoppingList list = new ShoppingList();
        list.setTitle(title);
        return shoppingListRepository.save(list);
    }


    // 2 Add item to list (default quantity = 1 or custom quantity)
    @Transactional
    public ShoppingListItem addItemToList(Long listId, Long itemId, int quantity) {
        ShoppingList list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("Shopping list not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        Optional<ShoppingListItem> existing = shoppingListItemRepository.findByShoppingListAndItem(list, item);

        if (existing.isPresent()) {
            ShoppingListItem sli = existing.get();
            sli.setQuantity(sli.getQuantity() + quantity);
            return shoppingListItemRepository.save(sli);
        }

        ShoppingListItem sli = new ShoppingListItem();
        sli.setItem(item);
        sli.setShoppingList(list);
        sli.setQuantity(quantity);
        return shoppingListItemRepository.save(sli);
    }

    // Decrease quantity of an item
    @Transactional
    public void decreaseItemQuantity(Long listId, Long itemId) {
        ShoppingList list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("List not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        ShoppingListItem sli = shoppingListItemRepository.findByShoppingListAndItem(list, item)
                .orElseThrow(() -> new RuntimeException("Item not in list"));

        if (sli.getQuantity() > 1) {
            sli.setQuantity(sli.getQuantity() - 1);
            shoppingListItemRepository.save(sli);
        } else {
            shoppingListItemRepository.delete(sli);
        }
    }

    // 7. Buy list → clear all items (simulate payment)
    @Transactional
    public void buyShoppingList(Long listId) {
        ShoppingList list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("List not found"));

        shoppingListItemRepository.deleteAll(list.getItems());
        list.getItems().clear();
    }

    // 8. Get list by ID
    public ShoppingList getListById(Long id) {
        return shoppingListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shopping list not found"));
    }



    // 3. Recommender Features
    public List<String> suggestItems(String partialName) {
        return itemRepository.findByName(partialName)
                .stream()
                .map(Item::getName)
                .distinct()
                .limit(5)
                .toList();
    }

    @Transactional
    public void deleteList(Long listId) {
        ShoppingList list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("Shopping list not found"));
        shoppingListRepository.delete(list);
    }

    public void deleteItemFromList(Long listId, Long itemId) {

    }


}