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
//        System.out.println(message);
//        message = ep.filterVNString(message);
        
        taggedWord = ep.processing(tokenize, message);
        
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
    public void detectPriceWaterCheck(){
//        System.out.println(taggedWord);
        String pWarter = "75.000đ";
        assertEquals(pWarter, ep.detectPriceWater(taggedWord));
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
