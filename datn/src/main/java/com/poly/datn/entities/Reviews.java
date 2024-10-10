package com.poly.datn.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reviews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Products product;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    private Integer rating;

    @Column(name = "review_text")
    private String reviewText;

    private Date date;

    @OneToMany(mappedBy = "review")
    private List<ReviewImages> reviewImages;
}

