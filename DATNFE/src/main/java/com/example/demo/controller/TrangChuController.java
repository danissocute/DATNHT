package com.example.demo.controller;

import com.example.demo.config.Config;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Email;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Controller
public class TrangChuController {
    @Autowired
    GiayDAO giayDAO;
    @Autowired
    GiayChiTietDAO giayChiTietDAO;
    @Autowired
    GioHangDAO gioHangDAO;
    @Autowired
    GioHangChiTietDAO gioHangChiTietDAO;
    @Autowired
    KhachHangDao khachHangDao;

    @RequestMapping("/login")
    public String login() {
        return "layout/login";
    }

    @RequestMapping(value = "/login", params = "error")
    public String loginfail() {
        return "layout/login";
    }

    @RequestMapping(value = "/login", params = "logout")
    public String logout() {
        return "layout/logout";
    }

    @RequestMapping("/trangchu")
    public String trangchu(Model model) {
        model.addAttribute("items", giayDAO.findAll());
        return "home/index";
    }

    @RequestMapping("/sanpham")
    public String sanpham(Model model) {
        model.addAttribute("items", giayDAO.findAll());
        LocalDate currentDateMinus7Days = LocalDate.now().minusDays(7);
        model.addAttribute("sevenDaysAgo", currentDateMinus7Days);
        return "home/sanpham";
    }

    @RequestMapping("/contact")
    public String contact() {
        return "home/contact";
    }

    @PostMapping("/contact/send-your-opininon")
    public String guiphanhoi(@RequestParam("ten") String ten,
                             @RequestParam("email")
                             @Email(message = "Địa chỉ Email không hợp lệ") String email,
                             @RequestParam("ykienphanhoi") String ykienphanhoi,
                             @RequestParam("agree") Integer agree) {
        if (ten != null && email != null && ykienphanhoi != null && agree != null)
        {
            System.out.println(ten + email + ykienphanhoi + agree);
        }
        return "redirect:/contact";
    }

}
