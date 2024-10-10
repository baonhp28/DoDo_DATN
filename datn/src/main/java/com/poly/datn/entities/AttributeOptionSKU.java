package com.poly.datn.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attribute_option_sku")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeOptionSKU {
    @EmbeddedId
    private AttributeOptionSKUKey id;

    @ManyToOne
    @MapsId("skuId")
    @JoinColumn(name = "sku_id")
    private Skus sku;

    @ManyToOne
    @MapsId("attributeOptionId")
    @JoinColumn(name = "attribute_option_id")
    private AttributeOption attributeOption;
}



