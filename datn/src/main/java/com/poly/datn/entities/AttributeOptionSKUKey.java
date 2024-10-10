package com.poly.datn.entities;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeOptionSKUKey implements Serializable {
    @Column(name = "sku_id")
    private Integer skuId;

    @Column(name = "attribute_option_id")
    private Integer attributeOptionId;
}
