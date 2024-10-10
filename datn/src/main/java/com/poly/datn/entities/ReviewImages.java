package com.poly.datn.entities;



import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "review_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewImages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Reviews review;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "alt_text")
    private String altText;
}
