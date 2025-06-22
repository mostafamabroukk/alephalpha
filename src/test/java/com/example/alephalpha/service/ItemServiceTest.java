package com.example.alephalpha.service;

import com.example.alephalpha.exception.NotFoundException;
import com.example.alephalpha.model.*;
import com.example.alephalpha.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ShoppingListItemRepository shoppingListItemRepository;

    @InjectMocks
    private ItemService itemService;

    @Test
    void createItem_shouldSaveAndReturnItem() {
        Item item = new Item();
        item.setName("Milk");
        item.setPrice(2.49);
        item.setCategory("Dairy");

        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Item result = itemService.createItem("Milk", 2.49, "Dairy");

        assertThat(result.getName()).isEqualTo("Milk");
        assertThat(result.getPrice()).isEqualTo(2.49);
        assertThat(result.getCategory()).isEqualTo("Dairy");
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void getAllItems_shouldReturnItemList() {
        List<Item> mockItems = List.of(new Item(), new Item());
        when(itemRepository.findAll()).thenReturn(mockItems);

        List<Item> result = itemService.getAllItems();
        assertThat(result).hasSize(2);
        verify(itemRepository).findAll();
    }

    @Test
    void deleteItem_shouldDeleteWhenItemExists() {
        Long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);

        ShoppingListItem sli = new ShoppingListItem();
        sli.setItem(item);
        List<ShoppingListItem> shoppingListItems = List.of(sli);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(shoppingListItemRepository.findAll()).thenReturn(shoppingListItems);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        itemService.deleteItem(itemId);

        verify(shoppingListItemRepository).deleteAll(shoppingListItems);
        verify(itemRepository).delete(item);
        verify(itemRepository).deleteById(itemId);
    }

    @Test
    void deleteItem_shouldThrowWhenItemNotFoundInitially() {
        Long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.deleteItem(itemId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Item not found");
    }
}
