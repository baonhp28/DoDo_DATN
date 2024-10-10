package com.poly.datn.entities;



import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attribute_option")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String value;

    @ManyToOne
    @JoinColumn(name = "attribute_id")
    private Attributes attribute;
}
