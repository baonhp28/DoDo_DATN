package com.poly.datn.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "skus")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Skus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

   @NotNull(message = "Giá tiền không được để trống")
    @Min(value = 1000, message = "Giá tiền phải lớn hơn hoặc bằng 1.000")
    @Column(name = "price", nullable = false)
    private Double price;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Products product;

    @OneToMany(mappedBy = "sku", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inbound> inbounds;

    @OneToMany(mappedBy = "sku", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Outbound> outbounds;

    @JsonProperty("product_id")
    public Integer getProductId() {
        return this.product != null ? this.product.getId() : null;
    }

    @OneToMany(mappedBy = "sku")
    @JsonIgnore
    private List<AttributeOptionSKU> attributeOptions;

}
