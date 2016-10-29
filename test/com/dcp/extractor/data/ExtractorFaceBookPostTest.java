/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dcp.extractor.data;

import java.io.IOException;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import vn.hus.nlp.tokenizer.VietTokenizer;
import vn.hus.nlp.tokenizer.tokens.TaggedWord;

/**
 *
 * @author LazyCode
 */
public class ExtractorFaceBookPostTest {

    static ExtractorFaceBookPost ep;
    static List<TaggedWord> taggedWord;

    public ExtractorFaceBookPostTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        ep = new ExtractorFaceBookPost();
        VietTokenizer tokenize = new VietTokenizer();
        String message = "Hiện tại nhà mình còn 1 phòng trọ ở tầng 2 trong tòa nhà "
                + "4 tầng tại số 4B ngõ 31 Yên Hòa.các thông tin chi tiết như sau :\n"
                + "- Diện tích 15m2 ,sạch sẽ, thoáng mát.\n"
                + "- Một tầng có 2 phòng chung 1 WC,có nóng lạnh.\n"
                + "- Có sân phơi + nấu ăn rộng rãi ở sân thượng.\n"
                + "- Có truyền hình Cáp miễn phí.\n"
                + "- Nhà nằm trong khu dân trí cao, yên tĩnh, giờ giấc thoải mái. An ninh rất tốt.\n"
                + "- Giá điện 4000đ/số, giá nước 75.000đ/người,nét cáp quang FPT 120k/phòng\n"
                + "- Nhà ở riêng chủ, chỉ cho các bạn nữ thuê.\n"
                + "- Nhà ngay gần cầu Cót, gần bến xe bus, siêu thị,"
                + " chợ…gần các trường ĐH Giao thông, Báo chí, Sư phạm…\n"
                + "- Giá 1,6 triệu \n"
                + "Vậy quý anh chị em nào có nhu cầu thì liên hệ với mình qua sdt : 0973501663 hoặc cmt ngay dưới tin đăng này.Xin cám ơn ạ";
        taggedWord = ep.processing(tokenize, message);
//        String regex = "(Điện|điện|đ|đien|dien)\\s*(:)?\\s*((\\d+([\\.,]\\d+)*)(k|nghìn|nghin|ng|d|)?(\\d*))";
//        message = "Điện: 3.500đ/1kwh.";
//        message = ep.filterVNString(message.toLowerCase());
//        System.out.println(message);
//        System.out.println(ep.regex(regex, message));
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    @Test
    public void converPriceTest(){
        assertEquals("70k", ep.converPrice("nuoc 70k"));
    }
    @Test
    public void detectPriceWaterCheck() throws IOException {
        String pWarter = "giá nước 75.000đ";
//        assertEquals(pWarter, ep.detectPriceWater(taggedWord));
        String message2 = "Xin phép Ad cho em đang bài tý ạ, hiện tại nhà mình đang còn 1 phòng khép kín cho thuê diện tích 30m2, có chỗ để xe máy không gian thoải mái.gần các trường đại học ngoại ngữ,quốc gia,sư phạm, báo chí\n"
                + "Đ/c: số 2b,tổ 53,ngõ 175, Xuân Thuỷ,Cầu Giấy\n"
                + "Giá tiền tính như sau :\n"
                + "Tiền nhà 2100k\n"
                + "Tiền điện có công tơ riêng 4,5k / số\n"
                + "Tiền nước 70k / tháng\n"
                + "Tiền mạng 70k/ tháng \n"
                + "Có nhu cầu thuê liên hệ 0963 292 123";
        assertEquals("nuoc 70k", ep.detectPriceWater(message2.toLowerCase()));
        message2 = "Vì anh mình về quê lấy vợ mình ở 1 mình không đủ khoản "
                + "nên mình nhượng phòng như trong hình giá "
                + "3 triệu nguyên tầng 3 2 phòng.... .. "
                + "chung chủ điện chia đều nhau đầu người "
                + "trong nhà ( 2 bác dễ tính thoải mái hay ra ngoài nhiều nên dùng ít điện )"
                + " .. nước 50k 1 người .. wifi "
                + "tính theo máy tính 50k /1 máy tính... "
                + "Giờ Giấc thoải mái có chìa khóa riêng.. "
                + "😊😊😊😊 có thể chuyển vào ngày 8-9 tháng 11 ...."
                + " ở Trần Cung- Cầu Giấy.";
        assertEquals("nuoc 50k", ep.detectPriceWater(message2.toLowerCase()));
        message2 = "cho thuê phòng ĐẸP - đầy đủ đồ - ngõ 95 Chùa Bộc🎶🎶\n"
                + "cón 01 phòng tầng 2 và 1 phòng tầng 7 trong nhà 7 tầng đẹp mới có thang máy, gần các trường Học viện ngân hàng, thủy lợi, công đoàn, đại học y.... gần các tuyến đường trung tâm : ngã tư sở , tây sơn, phạm ngọc thạch, trường chinh..... mặt ngõ to 2 ô tô tránh nhau\n"
                + "khu vực có đầy đủ tiện ích : sân tenis, bể bơi, sân bóng đá, bóng rổ, tenis...\n"
                + "- diện tích 22m2, phòng có cửa sổ thoáng\n"
                + "- nội thất đầy đủ bao gồm : tủ lạnh, giường, tủ, bàn làm việc,điều hòa, nóng lạnh, internet, truyền hình cáp....................... ( chỉ việc dọn đồ đến ở)\n"
                + "- giờ giấc thoải mái, không chung chủ, có khu nấu ăn riêng ( vệ sinh tuần dọn 2 lần)\n"
                + "- để xe FREE rộng rãi thoải mái có camera an ninh 24/24\n"
                + "- điện 3,5k/ 1 số\n"
                + "- nước 100k / 1 người\n"
                + "giá cho thuê 3tr2\n"
                + "liên hệ: 0978688662";
        assertEquals("nuoc 100k", ep.detectPriceWater(message2.toLowerCase()));
        message2 = "Phòng trọ tầng 2, 15m2, vệ sinh riêng biệt, nấu ăn, "
                + "phơi đồ ngoài ban công tầng 3 rộng 15m2, "
                + "có chỗ để xe miễn phí, net 50k, nước 50k, "
                + "điện chia theo hoá đơn, ở từ 25/10\n"
                + "Địa chỉ : Ngõ 128C Đại La (ngã 4 vọng) \n"
                + "Liên hệ : 01687697623";
        assertEquals("nuoc 50k", ep.detectPriceWater(message2.toLowerCase()));
    }

    @Test
    public void detectPriceElectricCheck() throws IOException {
        VietTokenizer tokenize2 = new VietTokenizer();
        List<TaggedWord> taggedWordElectric;
        String message2;
        message2 = "cho thuê phòng ĐẸP - đầy đủ đồ - ngõ 95 Chùa Bộc🎶🎶\n"
                + "cón 01 phòng tầng 2 và 1 phòng tầng 7 trong nhà 7 tầng đẹp mới có thang máy, gần các trường Học viện ngân hàng, thủy lợi, công đoàn, đại học y.... gần các tuyến đường trung tâm : ngã tư sở , tây sơn, phạm ngọc thạch, trường chinh..... mặt ngõ to 2 ô tô tránh nhau\n"
                + "khu vực có đầy đủ tiện ích : sân tenis, bể bơi, sân bóng đá, bóng rổ, tenis...\n"
                + "- diện tích 22m2, phòng có cửa sổ thoáng\n"
                + "- nội thất đầy đủ bao gồm : tủ lạnh, giường, tủ, bàn làm việc,điều hòa, nóng lạnh, internet, truyền hình cáp....................... ( chỉ việc dọn đồ đến ở)\n"
                + "- giờ giấc thoải mái, không chung chủ, có khu nấu ăn riêng ( vệ sinh tuần dọn 2 lần)\n"
                + "- để xe FREE rộng rãi thoải mái có camera an ninh 24/24\n"
                + "- điện 3,5k/ 1 số\n"
                + "- nước 100k / 1 người\n"
                + "giá cho thuê 3tr2\n"
                + "liên hệ: 0978688662";
        assertEquals("dien 3,5k", ep.detectPriceElectric(message2.toLowerCase()));
        assertEquals("nuoc 100k", ep.detectPriceWater(message2.toLowerCase()));
        assertEquals("cho thue 3tr2", ep.detectPriceHouse(message2.toLowerCase()));
        message2 = "Hiện tại nhà mình còn 1 phòng trọ ở tầng 2 trong tòa nhà "
                + "4 tầng tại số 4B ngõ 31 Yên Hòa.các thông tin chi tiết như sau :\n"
                + "- Diện tích 15m2 ,sạch sẽ, thoáng mát.\n"
                + "- Một tầng có 2 phòng chung 1 WC,có nóng lạnh.\n"
                + "- Có sân phơi + nấu ăn rộng rãi ở sân thượng.\n"
                + "- Có truyền hình Cáp miễn phí.\n"
                + "- Nhà nằm trong khu dân trí cao, yên tĩnh, giờ giấc thoải mái. An ninh rất tốt.\n"
                + "- Giá điện 4000đ/số, giá nước 75.000đ/người,nét cáp quang FPT 120k/phòng\n"
                + "- Nhà ở riêng chủ, chỉ cho các bạn nữ thuê.\n"
                + "- Nhà ngay gần cầu Cót, gần bến xe bus, siêu thị,"
                + " chợ…gần các trường ĐH Giao thông, Báo chí, Sư phạm…\n"
                + "- Giá 1,6 triệu"
                + "Vậy quý anh chị em nào có nhu cầu thì liên hệ với mình qua sdt : 0973501663 hoặc cmt ngay dưới tin đăng này.Xin cám ơn ạ";
        assertEquals("dien 4000d", ep.detectPriceElectric(message2.toLowerCase()));
        assertEquals("nuoc 75.000d", ep.detectPriceWater(message2.toLowerCase()));
        assertEquals("gia 1,6 tr", ep.detectPriceHouse(message2.toLowerCase()));
        message2 = "Đầu tháng 10/2016, Bên mình có 1 phòng trống tại địa chỉ: B10, lô 8, Khu ĐT Định Công.\n"
                + "- Số phòng: 402.\n"
                + "- Vị trí: Tầng 4(trong nhà 4 tầng).\n"
                + "- Độc lập, giờ giấc tự do, chỗ để xe miễn phí tầng 1, camera an ninh 24/24. Phòng nằm ở hướng đông nam, có 2 cửa sổ view nhìn xa, thoáng mát.\n"
                + "- Phòng đã được trang bị giường, tủ quần áo, bệ bếp, chậu rửa, bình nóng lạnh,...\n"
                + "Dịch vụ:\n"
                + "- Dịch vụ chăm sóc khách hàng chuyên nghiệp, nhiệt tình\n"
                + "- Kiểm tra hệ thống kỹ thuật theo quý ( 3 tháng/1 lần)\n"
                + "- Dịch vụ vệ sinh hàng tuần \n"
                + "- Hỗ trợ làm thẻ tạm trú\n"
                + "- Xử lý sự cố: điện, nước, internet, cáp... trong vòng 12h.\n"
                + "- Gía thuê phòng: 2.600.000đ/tháng. Thanh toán 3 tháng/lần, đặt cọc 1 tháng.\n"
                + "- Gía tiện ích (thanh toán 1 tháng/ lần)\n"
                + "Điện: 3.500đ/1kwh.\n"
                + "Nước: 100.000đ/người.\n"
                + "Internet và truyền hìnhcáp:100.000đ/phòng.\n"
                + "Phí vệ sinh: 30.000đ/người (bao gồm chi phí thuê người dọn vệ sinh hàng tuần, và phí vệ sinh đóng vào nhà nước)\n"
                + "Cam kết: Hình ảnh thực tế, thông tin đầy đủ, chính xác 100%\n"
                + "Chính chủ, miễn trung gian.\n"
                + "Liên hệ: Ms Thêu - 0167 635 8366";
        assertEquals("dien: 3.500d", ep.detectPriceElectric(message2.toLowerCase()));
        assertEquals("nuoc: 100.000d", ep.detectPriceWater(message2.toLowerCase()));
        assertEquals("gia thue phong: 2.600.000d", ep.detectPriceHouse(message2.toLowerCase()));
        message2 = "Phòng trọ hot mới hoàn thiện nhé\n"
                + "*Các bạn chú ý đọc kỹ bài viết và đến xem sớm nhất có thể nhé tránh trường hợp hết phòng\n"
                + "- 5 phòng khép kín tầng 4 giá 1tr3 nền đá hoa rộng 15m2 (1 dãy)\n"
                + "- 2 phòng không khép kín tầng 5 giá 800k nền đá hoa rộng 10m2\n"
                + "* Vị trí: Gần Đh Thương Mại, Bến xe Mỹ Đình, ĐH Quốc gia HN\n"
                + "* Giờ giấc thoải mái\n"
                + "* Nước 70n/ người/ tháng\n"
                + "* Điện 4,5n/ số\n"
                + "* Wifi free\n"
                + "* An ninh đảm bảo \n"
                + "* Có camera theo dõi tới tất cả các phòng\n"
                + "* Có tạp vụ dọn vệ sinh hàng ngày\n"
                + "* Đối với phòng không khép kín nhà vệ sinh tầng nào cũng có và thiết kế khu vệ sinh chung rộng rãi thoải mái không bao giờ có tình trạng đợi chờ nhé\n"
                + "* Không thông qua môi giới, xem phòng không mất phí\n"
                + "Liên hệ : Mr. Nam 0988216480 để check phòng và xem phòng\n"
                + "Địa chỉ: Số 1 đường mỹ đình (Cạnh cổng ra xe khách bến xe mỹ đình) như trên hình vè";
        assertEquals("dien 4,5", ep.detectPriceElectric(message2.toLowerCase()));
        assertEquals("nuoc 70", ep.detectPriceWater(message2.toLowerCase()));
        assertEquals("gia 800k", ep.detectPriceHouse(message2.toLowerCase()));
    }

    @Test
    public void anlanyticMessageCheck() {
//        System.out.println(taggedWord);
//        assertArrayEquals(new String[]{"1,6 triệu", "4000", "75.000"}, ep.anlanyticMessage(taggedWord, "a"));
    }

    @Test
    public void detectPriceCheck() {
        //detect electric
        assertTrue(ep.detectPrice("number2", "d", "electricty"));
        assertTrue(ep.detectPrice("number2", "d:", "electricty"));
        assertTrue(ep.detectPrice("number2", "dien", "electricty"));
        assertTrue(ep.detectPrice("number2", "dien:", "electricty"));
        assertTrue(ep.detectPrice("number2", "Dien", "electricty"));
        assertTrue(ep.detectPrice("number2", "Dien:", "electricty"));
        assertTrue(ep.detectPrice("number2", "D:", "electricty"));
        assertTrue(ep.detectPrice("number2", "D", "electricty"));
        //detect warter"nuoc:", "nc:", "n:", "nuoc", "nc", "n"
        assertTrue(ep.detectPrice("number2", "nuoc:", "warter"));
        assertTrue(ep.detectPrice("number2", "nc:", "warter"));
        assertTrue(ep.detectPrice("number2", "n:", "warter"));
        assertTrue(ep.detectPrice("number2", "nuoc", "warter"));
        assertTrue(ep.detectPrice("number2", "nc", "warter"));
        assertTrue(ep.detectPrice("number2", "n", "warter"));
        //room{"gia", "₫", "gia:", "₫:", "phong"}
        assertTrue(ep.detectPrice("number2", "gia", "room"));
        assertTrue(ep.detectPrice("number2", "₫", "room"));
        assertTrue(ep.detectPrice("number2", "gia:", "room"));
        assertTrue(ep.detectPrice("number2", "₫:", "room"));
        assertTrue(ep.detectPrice("number2", "phong", "room"));

    }

    @Test
    public void inArrayCheck() {
        assertTrue(ep.inArray(new String[]{"dien:", "d:", "d", "Dien", "D"}, "d:"));
        assertTrue(ep.inArray(new String[]{"dien:", "d:", "d", "Dien", "D"}, "d"));
        assertTrue(ep.inArray(new String[]{"dien:", "d:", "d", "Dien", "D", "dien"}, "dien"));
        assertFalse(ep.inArray(new String[]{"dien:", "d:", "d", "Dien", "D"}, "acc"));
        assertFalse(ep.inArray(new String[]{"dien:", "d:", "d", "Dien", "D"}, "12121"));
    }

    @Test
    public void filterVNStringCheck() {
        assertEquals("e", ep.filterVNString("ê"));
        assertEquals("dien", ep.filterVNString("điện"));
        assertEquals("dien", ep.filterVNString("diên"));
        assertEquals("dien", ep.filterVNString("điến"));
        assertEquals("dien", ep.filterVNString("điển"));
        assertEquals("dien", ep.filterVNString("điền"));
        assertEquals("dien", ep.filterVNString("điễn"));
        assertEquals("1,6 trieu", ep.filterVNString("1,6 triệu"));
    }
}
