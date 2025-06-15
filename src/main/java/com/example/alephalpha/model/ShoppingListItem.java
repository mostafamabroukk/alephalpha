package com.example.alephalpha.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter @NoArgsConstructor
@Table(name = "SHOPPING_LIST_ITEM")
public class ShoppingListItem {
    @Schema(description = "Unique ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Item ID")
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Schema(description = "List ID")
    @ManyToOne
    @JoinColumn(name = "list_id", nullable = false)
    @JsonBackReference
    private ShoppingList shoppingList;

    @Schema(description = "Quantity of Items in the Shopping List")
    @Column(nullable = false)
    private Integer quantity = 1;

    @Schema(description = "Total price of item units")
    @Transient
    public Double getTotalPrice() {
        return item.getPrice() * quantity;
    }
}