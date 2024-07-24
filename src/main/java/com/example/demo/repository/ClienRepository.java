package com.example.demo.repository;

import com.example.demo.entity.Clien;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienRepository extends JpaRepository<Clien, String> {
}
