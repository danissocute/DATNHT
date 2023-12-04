package com.example.demo.repository;

import com.example.demo.entity.ChatLieu;
import com.example.demo.entity.XuatXu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface XuatXuDAO extends JpaRepository<XuatXu,UUID> {
    XuatXu findXuatXuByTen(String ten);
}
