package com.example.alephalpha.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "SHOPPING_LIST")
public class ShoppingList {
    @Schema(description = "Shopping List ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Shopping List Title")
    @Column(nullable = false)
    private String title;

    @Schema(description = "Items in the Shopping List")
    @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonManagedReference
    private List<ShoppingListItem> items = new ArrayList<>();

    @Schema(description = "Count of Items")
    @Transient
    public Integer getTotalItemCount() {
        return items.stream()
                .mapToInt(ShoppingListItem::getQuantity)
                .sum();
    }

    @Schema(description = "Total Price of Shopping List")
    @Transient
    public Double getTotalValue() {
        return items.stream()
                .mapToDouble(ShoppingListItem::getTotalPrice)
                .sum();
    }
}