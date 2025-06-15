package com.example.alephalpha.repository;

import com.example.alephalpha.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItem, Long> {
    Optional<ShoppingListItem> findByShoppingListAndItem(ShoppingList list, Item item);
    List<ShoppingListItem> findByShoppingList(ShoppingList list);
}