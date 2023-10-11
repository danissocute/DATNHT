package com.example.demo.controller;

import com.example.demo.entity.DiaChi;
import com.example.demo.entity.GiamGiaChiTietHoaDon;
import com.example.demo.entity.GiamGiaHoaDon;
import com.example.demo.entity.GiayChiTiet;
import com.example.demo.entity.HoaDon;
import com.example.demo.entity.HoaDonChiTiet;
import com.example.demo.entity.KhachHang;
import com.example.demo.entity.NhanVien;
import com.example.demo.entity.PageDTO;
import com.example.demo.repository.DiaChiRepo;
import com.example.demo.repository.DiachiDao;
import com.example.demo.repository.GiamGiaChiTietHoaDonRepo;
import com.example.demo.repository.GiamGiaHoaDonRepo;
import com.example.demo.repository.GiayChiTietDAO;
import com.example.demo.repository.GiayChiTietRepo;
import com.example.demo.repository.HoaDonChiTietDAO;
import com.example.demo.repository.HoaDonChiTietRepo;
import com.example.demo.repository.HoaDonRepo;
import com.example.demo.repository.KhachHangRepo;
import com.example.demo.repository.NhanVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Controller
public class BanHangTaiQuayController {


    @Autowired
    private HoaDonRepo hoaDonRepo;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private GiayChiTietRepo giayChiTietRepo;

    @Autowired
    private HoaDonChiTietRepo hoaDonChiTietRepo;

    @Autowired
    private KhachHangRepo khachHangRepo;

    @Autowired
    private DiaChiRepo diaChiRepo;

    @Autowired
    private DiachiDao diachiDao;

    @Autowired
    private GiamGiaHoaDonRepo giamGiaHoaDonRepo;

    @Autowired
    private GiamGiaChiTietHoaDonRepo giamGiaChiTietHoaDonRepo;

    @Autowired
    private GiayChiTietDAO giayChiTietDAO;

    @Autowired
    private HoaDonChiTietDAO hoaDonChiTietDAO;

    // Hiển thị danh sách hóa đơn đang chờ
    @RequestMapping("/admin/ban-hang-tai-quay")
    public String giamGiaHoaDon(@RequestParam("page") Optional<Integer> page,
                                Model model) {
        PageDTO<HoaDon> hoaDonPageDTO = hoaDonRepo.getAllHDchuaTTPage(page.orElse(0));
        model.addAttribute("hoaDonListchuaTT", hoaDonPageDTO);
        model.addAttribute("i", 0);
        return "banhangtaiquay/ban_hang_tai_quay";
    }

    // Xóa Hóa Đơn
    @RequestMapping("/admin/ban-hang-tai-quay/delete/{id}")
    public String deleteGiamGiaHoaDon(@PathVariable("id") UUID id) {
        hoaDonRepo.deleteHD(id);
        return "redirect:/admin/ban-hang-tai-quay";
    }

    // tìm kiếm hóa đơn theo mã hoặc theo tên khách hàng
    @RequestMapping("/admin/ban-hang-tai-quay/tim-kiem-hoa-don")
    public String locGiamGiaHoaDonTheoTen(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam("page") Optional<Integer> page,
            Model model) {
        PageDTO<HoaDon> pageResult = hoaDonRepo.timkiemHoaDon(keyword, page.orElse(0));
        model.addAttribute("i", 0);
        model.addAttribute("hoaDonListchuaTT", pageResult);
        return "banhangtaiquay/ban_hang_tai_quay";
    }

    //Tạo đơn hàng
    @PostMapping("/admin/ban-hang-tai-quay/tao-don-hang")
    public String taoHoaDonTaiQuay(RedirectAttributes redirectAttributes) {
        NhanVien nhanVien = nhanVienRepository.getByMa("nv02");
        HoaDon hoaDon = new HoaDon();
        String ma = "HD" + new Random().nextInt(100000);
        hoaDon.setMa(ma);
        hoaDon.setNhanVien(nhanVien);
        LocalDate currentDate = LocalDate.now();
        java.sql.Date sqlDate = Date.valueOf(currentDate);
        hoaDon.setNgay_tao(sqlDate);
        hoaDon.setTrangthai(0);
        hoaDonRepo.createHoaDon(hoaDon);

        HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
        hoaDonChiTiet.setHoaDon(hoaDonRepo.getHoaDonByMa(ma));
        hoaDonChiTietRepo.createHDCT(hoaDonChiTiet);
        redirectAttributes.addAttribute("maHD", hoaDon.getMa());
        return "redirect:/admin/ban-hang-tai-quay/view-cart/{maHD}";
    }

    //View tạo đơn hàng
    @GetMapping("/admin/ban-hang-tai-quay/view-cart/{maHD}")
    public String showsanpham(@PathVariable("maHD") String ma,
                              @RequestParam("page1") Optional<Integer> page1,
                              Model model) {

        model.addAttribute("ListGiayChiTiet", giayChiTietDAO.findAll());
        Pageable pageable = PageRequest.of(page1.orElse(0), 5);
        PageDTO<HoaDonChiTiet> pageDTO = new PageDTO<>(hoaDonChiTietDAO.findByHoaDonMaPage(ma,pageable));

        model.addAttribute("hoaDonChiTietPage",pageDTO);

//        model.addAttribute("hoaDonChiTietPage", hoaDonChiTietRepo.getPageHDCTbyMaHD(ma, page1.orElse(0)));
        model.addAttribute("ListKhachHang", khachHangRepo.getAll());
        model.addAttribute("ListGiamGiaHoaDon", giamGiaHoaDonRepo.getAllGGHDtrangthai1());
        model.addAttribute("i", 0);
        model.addAttribute("maHD", ma);
        HoaDon hoaDon = hoaDonRepo.getHoaDonByMa(ma);
        model.addAttribute("KhachHangcheck", hoaDon.getKhachHang());
        if (hoaDon.getKhachHang() != null) {
            model.addAttribute("KhachHangDetail", hoaDon.getKhachHang());
            List<DiaChi> diaChiList = diachiDao.getdiachibyma(hoaDon.getKhachHang().getMa());
            model.addAttribute("ListDiaChi", diaChiList);
            DiaChi diaChi = diachiDao.getDiachiBytendiachi(hoaDon.getDia_chi());
            if (diaChi != null) {
                model.addAttribute("diachiHoaDon", diaChi);

            }
        }

        BigDecimal tongTienTruocGiam = calculateTotal(hoaDon);
        model.addAttribute("TongTienTruocGiam", tongTienTruocGiam);

        // Kiểm tra nếu hóa đơn có giá trị tổng tiền, nếu không thì gán giá trị là BigDecimal.ZERO
        BigDecimal tongTienHoaDon = hoaDon.getTong_tien() != null ? hoaDon.getTong_tien() : BigDecimal.ZERO;

        model.addAttribute("TongTienSauGiam", tongTienHoaDon);

        // Kiểm tra nếu tổng tiền trước giảm lớn hơn tổng tiền hóa đơn
        if (tongTienTruocGiam.compareTo(tongTienHoaDon) > 0) {
            BigDecimal soTienGiam = tongTienTruocGiam.subtract(tongTienHoaDon);
            model.addAttribute("soTienGiam", soTienGiam);
        } else {
            // Nếu không giảm giá thì giá trị soTienGiam là BigDecimal.ZERO
            model.addAttribute("soTienGiam", BigDecimal.ZERO);
        }

        GiamGiaChiTietHoaDon giamGiaChiTietHoaDon = giamGiaChiTietHoaDonRepo.getGiamGiaCTHoaDonByHD(hoaDon.getId());
        if (giamGiaChiTietHoaDon != null) {
            model.addAttribute("detailMaGGHD", giamGiaChiTietHoaDon.getGghd().getMa());

        } else {

            model.addAttribute("checkGGHD", 1);
        }


        return "banhangtaiquay/tao_don_hang";
    }

    @PostMapping("/admin/ban-hang-tai-quay/tao-don-hang/add-to-cart")
    public String themGiayVaoHoaDonChiTiet(@RequestParam("maHD") String maHD,
                                           @RequestParam("selectedIds") List<UUID> selectedIds,
                                           @RequestParam Map<String, String> quantities,
                                           RedirectAttributes redirectAttributes) {

        for (UUID idctsp : selectedIds) {
            HoaDon hoaDon = hoaDonRepo.getHoaDonByMa(maHD);
            List<HoaDonChiTiet> hoaDonChiTietList = hoaDonChiTietRepo.getListHDCTbyMaHD(hoaDon.getMa());
            GiayChiTiet giayChiTiet = giayChiTietRepo.getGiayChiTietById(idctsp);
            boolean foundExistingChiTiet = false;
            int soLuong = Integer.parseInt(quantities.get("quantity[" + idctsp + "]"));
            for (HoaDonChiTiet hdct : hoaDonChiTietList) {
                if (hdct.getGiayChiTiet() == null) {
                    hdct.setHoaDon(hoaDon);
                    hdct.setGiayChiTiet(giayChiTiet);
                    hdct.setSo_luong(soLuong);
                    hdct.setDon_gia(giayChiTiet.getGiay().getGiaban());
                    hdct.setTrangthai(1);
                    foundExistingChiTiet = true;
                    break;
                } else if (hdct.getGiayChiTiet().getId().equals(idctsp)) {
                    hdct.setSo_luong(hdct.getSo_luong() + soLuong);
                    hdct.setDon_gia(giayChiTiet.getGiay().getGiaban());
                    foundExistingChiTiet = true;
                    break;
                }
            }

            if (!foundExistingChiTiet) {
                HoaDonChiTiet newHoaDonChiTiet = new HoaDonChiTiet();
                newHoaDonChiTiet.setGiayChiTiet(giayChiTiet);
                newHoaDonChiTiet.setHoaDon(hoaDon);
                newHoaDonChiTiet.setSo_luong(soLuong);
                newHoaDonChiTiet.setDon_gia(giayChiTiet.getGiay().getGiaban());
                newHoaDonChiTiet.setTrangthai(1);
                hoaDonChiTietRepo.createHDCT(newHoaDonChiTiet);
            }
            hoaDonChiTietRepo.createHoaDonChiTietList(hoaDonChiTietList);
            List<HoaDonChiTiet> hoaDonChiTietList2 = hoaDonChiTietRepo.getListHDCTbyMaHD(hoaDon.getMa());
            BigDecimal tongTien = tinhTongTienHoaDon(hoaDonChiTietList2);
            hoaDon.setTong_tien(tongTien);
            hoaDonRepo.createHoaDon(hoaDon);

        }

        redirectAttributes.addAttribute("maHD", maHD);
        return "redirect:/admin/ban-hang-tai-quay/view-cart/{maHD}";
    }

    private BigDecimal tinhTongTienHoaDon(List<HoaDonChiTiet> hoaDonChiTietList) {
        BigDecimal tongTien = BigDecimal.ZERO; // Khởi tạo tổng tiền ban đầu là 0
        for (HoaDonChiTiet hdct : hoaDonChiTietList) {
            if (hdct.getTrangthai() == 1) { // Chỉ tính tổng tiền cho các sản phẩm có trạng thái là 1 (đã được chọn)
                BigDecimal donGia = hdct.getDon_gia();
                int soLuong = hdct.getSo_luong();
                BigDecimal giaTienSanPham = donGia.multiply(BigDecimal.valueOf(soLuong));
                tongTien = tongTien.add(giaTienSanPham);
            }
        }
        return tongTien;
    }

    // Xóa Giảm Giá Hóa Đơn
    @RequestMapping("/admin/ban-hang-tai-quay/tao-don-hang/delete/{mahd}/{idctsp}/{soLuong}")
    public String deleteHoaDonChiTietByGCT(@PathVariable("mahd") String mahd,
                                           @PathVariable("idctsp") UUID idctsp,
                                           @PathVariable("soLuong") int soLuong,
                                           RedirectAttributes redirectAttributes) {

        HoaDon hoaDon = hoaDonRepo.getHoaDonByMa(mahd);

        // Lấy tổng tiền hiện tại của hóa đơn
        BigDecimal tongTienHienTai = hoaDon.getTong_tien();

        // Lấy giá trị của sản phẩm đã bị xóa
        GiayChiTiet giayChiTiet = giayChiTietRepo.getGiayChiTietById(idctsp);
        BigDecimal giaTienSanPhamDaXoa = giayChiTiet.getGiay().getGiaban();

        // Trừ giá trị sản phẩm đã bị xóa (nhân với số lượng đã xóa)
        BigDecimal giaTienDaXoa = giaTienSanPhamDaXoa.multiply(BigDecimal.valueOf(soLuong));
        tongTienHienTai = tongTienHienTai.subtract(giaTienDaXoa);

        // Xóa chi tiết hóa đơn
        hoaDonChiTietRepo.deleteHDCT(hoaDon.getId(), idctsp);

        // Cập nhật tổng tiền cho hóa đơn
        hoaDon.setTong_tien(tongTienHienTai);
        hoaDonRepo.createHoaDon(hoaDon);

        redirectAttributes.addAttribute("maHD", hoaDon.getMa());
        return "redirect:/admin/ban-hang-tai-quay/view-cart/{maHD}";
    }

    @PostMapping("/admin/ban-hang-tai-quay/tao-don-hang/add-khach-hang")
    public String themKhachHangVaoHoaDon(@RequestParam("maHD") String maHD,
                                         @RequestParam("maKH") String maKH,
                                         RedirectAttributes redirectAttributes,
                                         Model model) {
        HoaDon hoaDon = hoaDonRepo.getHoaDonByMa(maHD);
        KhachHang khachHang = khachHangRepo.getBykhachhangma(maKH);
        hoaDon.setKhachHang(khachHang);
        hoaDonRepo.createHoaDon(hoaDon);

        redirectAttributes.addAttribute("maHD", maHD);
        return "redirect:/admin/ban-hang-tai-quay/view-cart/{maHD}";
    }

    @PostMapping("/admin/ban-hang-tai-quay/tao-don-hang/add-dia-chi")
    public String themDiaChiKhachHangVaoHoaDon(@RequestParam("maHD") String maHD,
                                               @RequestParam("maDC") String maDC,

                                               RedirectAttributes redirectAttributes) {

        if(maDC != null || maDC != null)
        {
            HoaDon hoaDon = hoaDonRepo.getHoaDonByMa(maHD);
            DiaChi diaChi = diachiDao.getDiachiBytendiachi(maDC);
            String maDiaChi = diaChi.getTendiachi();
            hoaDon.setDia_chi(maDiaChi);
            hoaDonRepo.createHoaDon(hoaDon);
        }

        redirectAttributes.addAttribute("maHD", maHD);
        return "redirect:/admin/ban-hang-tai-quay/view-cart/{maHD}";
    }


    @PostMapping("/admin/ban-hang-tai-quay/tao-don-hang/add-dia-chi1")
    public String themDiaChiKhachHangVaoHoaDon1(@RequestParam("maHD") String maHD,
                                               @RequestParam("dia_chi") String diaChi1,
                                               @RequestParam("ward") String xa1,
                                               @RequestParam("district") String huyen1,
                                               @RequestParam("province") String thanhPho1,
                                               RedirectAttributes redirectAttributes) {

            HoaDon hoaDon = hoaDonRepo.getHoaDonByMa(maHD);
        // Gộp các giá trị thành một chuỗi địa chỉ
             String diaChiGop = diaChi1 + ", " + xa1 + ", " + huyen1 + ", " + thanhPho1;
            hoaDon.setDia_chi(diaChiGop);
            hoaDonRepo.createHoaDon(hoaDon);


        redirectAttributes.addAttribute("maHD", maHD);
        return "redirect:/admin/ban-hang-tai-quay/view-cart/{maHD}";
    }



    @PostMapping("/admin/ban-hang-tai-quay/tao-don-hang/add-voucher")
    public String themVoucherVaoHoaDon(@RequestParam("maHD") String maHD,
                                       @RequestParam("maGGHD") String maGGHD,
                                       RedirectAttributes redirectAttributes) {
        HoaDon hoaDon = hoaDonRepo.getHoaDonByMa(maHD);
        GiamGiaHoaDon giamGiaHoaDon = giamGiaHoaDonRepo.getGiamGiaHoaDonByMa(maGGHD);

        // Kiểm tra hoaDon và giamGiaHoaDon không phải là null
        if (hoaDon != null && giamGiaHoaDon != null && giamGiaHoaDon.getSo_luong() > 0) {
            BigDecimal dieuKien = giamGiaHoaDon.getDieu_kien();
            BigDecimal soTienGiam = giamGiaHoaDon.getSo_tien_giam();

            // Kiểm tra tổng tiền của bảng HoaDon cũng phải lớn hơn điều kiện giảm
            BigDecimal tongTienHoaDon = hoaDon.getTong_tien();
            if (tongTienHoaDon.compareTo(dieuKien) > 0) {
                // Áp dụng giảm giá cho tổng tiền của hóa đơn
                BigDecimal tongTienSauGiam = tongTienHoaDon.subtract(soTienGiam);

                // Cập nhật tổng tiền của hóa đơn
                hoaDon.setTong_tien(tongTienSauGiam);
                hoaDonRepo.createHoaDon(hoaDon);

                // Tạo chi tiết giảm giá trong hóa đơn
                GiamGiaChiTietHoaDon giamGiaChiTietHoaDon = new GiamGiaChiTietHoaDon();
                giamGiaChiTietHoaDon.setHd(hoaDon);
                giamGiaChiTietHoaDon.setGghd(giamGiaHoaDon);
                giamGiaChiTietHoaDon.setTrangthai(1);
                giamGiaChiTietHoaDonRepo.createGGCTHD2(giamGiaChiTietHoaDon);

                // Giảm số lượng voucher còn lại
                giamGiaHoaDon.setSo_luong(giamGiaHoaDon.getSo_luong() - 1);
                giamGiaHoaDonRepo.createGGHD(giamGiaHoaDon);

                redirectAttributes.addAttribute("maHD", maHD);
                return "redirect:/admin/ban-hang-tai-quay/view-cart/{maHD}";
            }
        }

        redirectAttributes.addAttribute("maHD", maHD);
        return "redirect:/admin/ban-hang-tai-quay/view-cart/{maHD}";
    }


    @RequestMapping("/admin/ban-hang-tai-quay/tao-don-hang/delete")
    public String deleteGGCTHD(@RequestParam("maHD") String maHD,
                               @RequestParam("maGGHD") String maGGHD,
                               RedirectAttributes redirectAttributes) {
        HoaDon hoaDon = hoaDonRepo.getHoaDonByMa(maHD);
        GiamGiaHoaDon giamGiaHoaDon = giamGiaHoaDonRepo.getGiamGiaHoaDonByMa(maGGHD);

        // Xóa chi tiết giảm giá
        giamGiaChiTietHoaDonRepo.deleteGGCTHD(hoaDon.getId(), giamGiaHoaDon.getId());

        // Tính lại tổng tiền của hóa đơn từ đầu
        BigDecimal newTotal = calculateTotal(hoaDon);
        hoaDon.setTong_tien(newTotal);

        // Cập nhật hóa đơn trong repository
        hoaDonRepo.createHoaDon(hoaDon);
        redirectAttributes.addAttribute("maHD", maHD);
        return "redirect:/admin/ban-hang-tai-quay/view-cart/{maHD}";
    }

    private BigDecimal calculateTotal(HoaDon hoaDon) {
        List<HoaDonChiTiet> hoaDonChiTietList = hoaDonChiTietRepo.getListHDCTbyMaHD(hoaDon.getMa());
        BigDecimal total = BigDecimal.ZERO;

        if (hoaDonChiTietList != null) {
            for (HoaDonChiTiet chiTiet : hoaDonChiTietList) {
                Integer soLuong = chiTiet.getSo_luong();
                BigDecimal gia = chiTiet.getDon_gia();

                if (soLuong != null) {
                    BigDecimal subtotal = BigDecimal.valueOf(soLuong).multiply(gia);
                    total = total.add(subtotal);
                }
            }
        }

        return total;
    }


}