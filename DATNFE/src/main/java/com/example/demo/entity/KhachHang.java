package com.example.demo.entity;

import lombok.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.UUID;

@Getter
@Setter
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "khach_hang")
public class KhachHang implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String ma;

    @Column(name = "ho_ten")
    private String hoten;

    @Column(name = "ngay_sinh")
    private Date ngaysinh;

    @Column(name = "sdt")
    private String sdt;

    @Column(name = "email")
    private String email;

    @Column(name = "mat_khau")
    private String matkhau;

    @Column(name = "trangthai")
    private Integer trangthai;

    @OneToOne(mappedBy = "khach_hang")
    private GioHang gio_hang;
    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL)
    private List<DiaChi> diaChiList;
}
