package com.example.alephalpha.service;

import com.example.alephalpha.exception.NotFoundException;
import com.example.alephalpha.model.Item;
import com.example.alephalpha.model.ShoppingListItem;
import com.example.alephalpha.repository.ItemRepository;
import com.example.alephalpha.repository.ShoppingListItemRepository;
import com.example.alephalpha.repository.ShoppingListRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ShoppingListItemRepository shoppingListItemRepository;

    public Item createItem(String name, Double price, String category) {
        Item item = new Item();
        item.setName(name);
        item.setPrice(price);
        item.setCategory(category);
        return itemRepository.save(item);
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Transactional
    public void deleteItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        List<ShoppingListItem> references = shoppingListItemRepository.findAll()
                .stream()
                .filter(sli -> sli.getItem().getId().equals(itemId))
                .toList();

        shoppingListItemRepository.deleteAll(references);
        itemRepository.delete(item);
        if (!itemRepository.existsById(itemId)) {
            throw new EntityNotFoundException("Item not found with ID: " + itemId);
        }
        itemRepository.deleteById(itemId);
    }
}
