package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Branch extends Base {
    @Id
    @Column(length = 80)
    String id;

    @Column(length = 80)
    String name;

    String address;

    @Column(length = 50)
    String phone;

    @Column(length = 50)
    String email;

    String description;

}
