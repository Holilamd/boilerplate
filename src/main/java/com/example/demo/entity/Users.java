package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Users extends Base {
    @Id
    @Column(length = 50)
    String id;

    @Column(length = 70)
    String name;

    @Column(length = 50)
    String username;

    @Column(length = 50)
    String password;

    @Column(length = 50)
    String email;

}
