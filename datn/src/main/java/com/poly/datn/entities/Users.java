package com.poly.datn.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "fullname", nullable = false)
    private String fullName;

    @Column(name = "birthday", nullable = false)  // Đảm bảo rằng ngày sinh không được null
    private Date birthday;

    @Column(name = "gender")
    private Boolean gender;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email", unique = true, nullable = false)  // Đảm bảo rằng email không được null
    private String email;

    @Column(name = "password", nullable = false)
    @JsonIgnore
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "status")
    private Boolean status;  // Nếu không cần giá trị null, thay đổi thành `boolean`

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Address> addresses;

    public Users(Integer id) {
        this.id = id;
    }
}
