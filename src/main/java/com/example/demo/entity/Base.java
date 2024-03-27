package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class Base {
    @Column(length = 80)
    String status;

    LocalDateTime created_at;

    LocalDateTime updated_at;

    LocalDateTime deleted_at;

    @Column(length = 80)
    String created_by;

    @Column(length = 80)
    String updated_by;

    @Column(length = 80)
    String deleted_by;
}
