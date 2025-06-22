package com.example.alephalpha.service;

import com.example.alephalpha.model.*;
import com.example.alephalpha.repository.ShoppingListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SuggestionServiceTest {

    @Mock
    private ItemService itemService;

    @Mock
    private ShoppingListRepository shoppingListRepository;

    @InjectMocks
    private SuggestionService suggestionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        suggestionService = new SuggestionService(itemService);
        java.lang.reflect.Field field;
        try {
            field = SuggestionService.class.getDeclaredField("shoppingListRepository");
            field.setAccessible(true);
            field.set(suggestionService, shoppingListRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void autocomplete_shouldReturnMatchingItems() {
        List<Item> items = getDummyItems();
        when(itemService.getAllItems()).thenReturn(items);

        List<String> result = suggestionService.autocomplete("bu");
        assertThat(result).containsExactly("Butter");
    }

    private List<Item> getDummyItems() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Bread");
        item1.setPrice(1.0);
        item1.setCategory("Bakery");

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Butter");
        item2.setPrice(1.5);
        item2.setCategory("Dairy");

        Item item3 = new Item();
        item3.setId(3L);
        item3.setName("Jam");
        item3.setPrice(2.0);
        item3.setCategory("Dairy");

        return List.of(item1, item2, item3);
    }

    @Test
    void autocomplete_shouldReturnEmptyForShortPrefix() {
        List<String> result = suggestionService.autocomplete(null);
        assertThat(result).isEmpty();

        result = suggestionService.autocomplete("");
        assertThat(result).isEmpty();
    }

    @Test
    void suggestComplementaryItems_shouldReturnSuggestions() {
        Item item1 = new Item();

        Item gin = new Item(1L, "gin", 10.0, "drinks");
        Item tonic = new Item(2L, "tonic", 5.0, "drinks");

        ShoppingListItem sli1 = new ShoppingListItem();
        sli1.setItem(gin);

        ShoppingListItem sli2 = new ShoppingListItem();
        sli2.setItem(tonic);

        ShoppingList list = new ShoppingList();
        list.setItems(List.of(sli1, sli2));

        when(shoppingListRepository.findById(1L)).thenReturn(Optional.of(list));

        List<String> result = suggestionService.suggestComplementaryItems(1L);

        assertThat(result).contains("Crodino", "Sanbitter");
    }

    @Test
    void suggestComplementaryItems_shouldExcludeAlreadyPresentSuggestions() {
        Item pasta = new Item(1L, "pasta", 2.0, "food");
        Item sauce = new Item(2L, "sauce", 1.0, "food");
        Item cheese = new Item(3L, "cheese", 3.0, "food");

        ShoppingListItem sli1 = new ShoppingListItem();
        sli1.setItem(pasta);
        ShoppingListItem sli2 = new ShoppingListItem();
        sli2.setItem(sauce);
        ShoppingListItem sli3 = new ShoppingListItem();
        sli3.setItem(cheese);

        ShoppingList list = new ShoppingList();
        list.setItems(List.of(sli1, sli2, sli3));

        when(shoppingListRepository.findById(1L)).thenReturn(Optional.of(list));

        List<String> result = suggestionService.suggestComplementaryItems(1L);

        // all complementary items already present, should return empty
        assertThat(result).isEmpty();
    }

    @Test
    void suggestComplementaryItems_shouldThrowIfListNotFound() {
        when(shoppingListRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                suggestionService.suggestComplementaryItems(100L));
    }

    @Test
    void suggestRecipes_shouldReturnCompletedAndPartialMatches() {
        ShoppingList list = getDummyShoppingList();

        when(shoppingListRepository.findById(1L)).thenReturn(Optional.of(list));

        List<Map<String, Object>> suggestions = suggestionService.suggestRecipes(1L);

        assertThat(suggestions).isNotEmpty();
        assertThat(suggestions).anyMatch(s -> s.get("recipe").equals("Chicken Rice"));
        assertThat(suggestions).anyMatch(s -> {
            s.get("missingIngredients");
            return true;
        });
    }

    private static ShoppingList getDummyShoppingList() {
        Item chicken = new Item(1L, "chicken", 4.0, "meat");
        Item rice = new Item(2L, "rice", 1.5, "grains");
        Item sauce = new Item(3L, "sauce", 1.0, "sauce");

        ShoppingListItem sli1 = new ShoppingListItem();
        sli1.setItem(chicken);

        ShoppingListItem sli2 = new ShoppingListItem();
        sli2.setItem(rice);

        ShoppingListItem sli3 = new ShoppingListItem();
        sli3.setItem(sauce);

        ShoppingList list = new ShoppingList();
        list.setItems(List.of(sli1, sli2, sli3));
        return list;
    }

    @Test
    void suggestRecipes_shouldThrowIfListNotFound() {
        when(shoppingListRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                suggestionService.suggestRecipes(99L));
    }
}
