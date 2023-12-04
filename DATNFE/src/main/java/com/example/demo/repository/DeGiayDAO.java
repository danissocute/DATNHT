package com.example.demo.repository;

import com.example.demo.entity.ChatLieu;
import com.example.demo.entity.DeGiay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeGiayDAO extends JpaRepository<DeGiay,UUID> {
    DeGiay findDeGiayByTen(String ten);
}
