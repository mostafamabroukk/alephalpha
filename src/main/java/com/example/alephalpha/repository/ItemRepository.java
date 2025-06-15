package com.example.alephalpha.repository;

import com.example.alephalpha.model.Item;
import com.example.alephalpha.model.ShoppingList;
import com.example.alephalpha.model.ShoppingListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByName(String name);

    List<Item> findByNameStartingWithIgnoreCase(String prefix);

    Optional<Item> findByNameIgnoreCase(String name);

}


