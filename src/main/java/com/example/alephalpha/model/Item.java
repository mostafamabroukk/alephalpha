package com.example.alephalpha.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "ITEM")
public class Item {
    @Schema(description = "Item ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Item Name")
    @Column(nullable = false)
    private String name;

    @Schema(description = "Item Price")
    @Column(nullable = false)
    private Double price;

    @Schema(description = "Item Category")
    @Column(nullable = true)
    private String category;
}