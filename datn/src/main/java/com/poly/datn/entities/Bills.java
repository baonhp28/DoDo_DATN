package com.poly.datn.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "bills")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bills {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    private Double total;

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    @JsonManagedReference
    private Status status;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "number_phone")
    private String numberPhone;

    private String address;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<BillDetail> billDetails;
}
