package com.example.alephalpha.service;

import com.example.alephalpha.model.*;
import com.example.alephalpha.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Optional;
import static org.mockito.Mockito.*;

class ShoppingListServiceTest {

    @Mock
    private ShoppingListRepository shoppingListRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ShoppingListItemRepository shoppingListItemRepository;

    @InjectMocks
    private ShoppingListService shoppingListService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteItemFromList_shouldDeleteSuccessfully() {
        Long listId = 1L;
        Long itemId = 2L;

        ShoppingList list = new ShoppingList();
        list.setId(listId);

        Item item = new Item();
        item.setId(itemId);

        ShoppingListItem sli = new ShoppingListItem();
        sli.setShoppingList(list);
        sli.setItem(item);

        when(shoppingListRepository.findById(listId)).thenReturn(Optional.of(list));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(shoppingListItemRepository.findByShoppingListAndItem(list, item)).thenReturn(Optional.of(sli));

        shoppingListService.deleteItemFromList(listId, itemId);
        verify(shoppingListItemRepository).delete(sli);
    }

    @Test
    void deleteItemFromList_shouldThrowIfListNotFound() {
        Long listId = 1L;
        Long itemId = 2L;

        when(shoppingListRepository.findById(listId)).thenReturn(Optional.empty());
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () ->
                shoppingListService.deleteItemFromList(listId, itemId));
    }

    @Test
    void deleteItemFromList_shouldThrowIfItemNotFound() {
        Long listId = 1L;
        Long itemId = 2L;

        ShoppingList list = new ShoppingList();
        list.setId(listId);

        when(shoppingListRepository.findById(listId)).thenReturn(Optional.of(list));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () ->
                shoppingListService.deleteItemFromList(listId, itemId));
    }

    @Test
    void deleteItemFromList_shouldThrowIfItemNotInList() {
        Long listId = 1L;
        Long itemId = 2L;

        ShoppingList list = new ShoppingList();
        Item item = new Item();

        when(shoppingListRepository.findById(listId)).thenReturn(Optional.of(list));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(shoppingListItemRepository.findByShoppingListAndItem(list, item)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () ->
                shoppingListService.deleteItemFromList(listId, itemId));
    }
}
