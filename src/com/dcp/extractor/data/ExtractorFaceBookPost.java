/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dcp.extractor.data;

import com.dcp.db.Search;
import com.dcp.db.StreetModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vn.hus.nlp.tokenizer.Tokenizer;
import vn.hus.nlp.tokenizer.VietTokenizer;
import vn.hus.nlp.tokenizer.tokens.TaggedWord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author LazyCode
 */
public class ExtractorFaceBookPost {

    public static String[] WORD_ECLECTRICTY = {"dien:", "d:", "d", "Dien", "D", "dien", "điện:", "đ:", "đ", "Điện", "D", "điện"};
    public static String[] WORD_WARTER = {"nuoc:", "nc:", "n:", "nuoc", "nc", "n", "nước:", "nc:", "nước"};
    public static String[] WORD_ROM_PRICE = {"giá", "₫", "giá:", "phong", "gia", "gia:", "₫:", "phòng"};
    public static String[] WORD_LEASE = {"cho thue nha", "cho thue"};
    public static String[] WORD_TENANT = {"can thue nha", "can thue", "can tim", "can thue phong"};
    public static String[] WORD_DUSTY = {"thanh ly", "sim"};
    public static String[] PREFIX_PLACE = {"o", "khu vuc", "tai", "dia chi", "ngo", "đc", "khu", "gan", "duong"};
    public static String[] STOP_WORD = {"phong", "gia", "dien", "nuoc", "nc", "gia thue phong", "cho thue", "phong gia", "đ"};

    public ExtractorFaceBookPost() {
    }

    public List<TaggedWord> processing(VietTokenizer tokenizer, String message) throws IOException {
        String[] listString = tokenizer.tokenize(message);
        Tokenizer a = tokenizer.getTokenizer();
        List<TaggedWord> result = a.getResult();
        return result;
    }

    public String detectPhoneNumber(String message) {
        message = this.filterVNString(message);
        String regex = "\\d{3,5}(\\w|\\W?)\\d{3,4}\\1\\d{3,4}";
        Matcher matcher = this.regex(regex, message);
        System.out.println(matcher);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    public String detectPriceWater(String message) {
        message = this.filterVNString(message);
        String regex = "(nc|nuoc)\\s*(:)?\\s*((\\d+([\\.,]\\d+)*)(k|nghìn|nghin|ng|d)?(\\d*))";
        Matcher matcher = this.regex(regex, message);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    public String detectPriceElectric(String message) {
        message = this.filterVNString(message);
        String regex = "(d|dien)\\s*(:)?\\s*((\\d+([\\.,]\\d+)*)(k|nghìn|nghin|ng|d|)?(\\d*))";
        Matcher matcher = this.regex(regex, message);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }


    public ArrayList<Float> detectPriceHouse(String message) {
        String regex1 = "(((\\d+)([\\.,]?)(\\d+)*\\s*(tỉ|tỷ|triệu|tr|cu|củ|k|lít|lit|t)[\\s|$|\\d+])|(\\d+([\\.,]\\d+)+)[đ|d])";

        Pattern pattern = Pattern.compile(regex1, Pattern.MULTILINE);

        Matcher matcher = pattern.matcher(message);
        ArrayList<Float> rs = new ArrayList<Float>();
        float price;
        while (matcher.find()) {
            price = Float.parseFloat(this.convertPrice(this.filterVNString(matcher.group(0))));
            if (price >= 1_000_000) {
                rs.add(price);
            }
        }
        return rs;
    }

    public boolean inArray(String[] arrayString, String value) {
        for (int i = 0; i < arrayString.length; i++) {
            if (value.compareTo(arrayString[i]) == 0) {
                return true;
            }
        }
        return false;
    }

    public String filterVNString(String string) {
        string = string.replaceAll("[̣̀̃̉́]", "");
        string = string.replaceAll("̣₫", "đ");
        string = string.replaceAll("đ", "d");
        string = string.replaceAll("[iìỉĩíịj]", "i");
        string = string.replaceAll("[eèẻẽéẹêềểễếệ]", "e");
        string = string.replaceAll("[aàảãáạăằẳẵắặâầẩẫấậ]", "a");
        string = string.replaceAll("[uùủũúụưừửữứự]", "u");
        string = string.replaceAll("[oòỏõóọôồổỗốộơờởỡớợ]", "o");
        string = string.replaceAll("[yỳỷỹýỵ]", "y");
        return string;
    }

    public Matcher regex(String regex, String content) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        return matcher;
    }

    public String convertPrice(String price) {
        for (int i = 0; i < STOP_WORD.length; i++) {
            price = price.replaceAll(STOP_WORD[i], "");
        }
        price = price.replaceAll("\\s+", "");

        price = price.replaceAll(",", ".");
        price = price.replaceAll("\\.", "");
        if (price.length() > 0 && price.charAt(0) == '0') {
            return "0";
        }
        String UNIT = "([a-zA-Z]+)";
        Pattern PRICE_TYPE_3 = Pattern.compile("(\\d+)" + UNIT);
        Pattern PRICE_TYPE_2 = Pattern.compile("(\\d+)[,.](\\d+)" + UNIT);
        Pattern PRICE_TYPE_1 = Pattern.compile("(\\d+)" + UNIT + "(\\d+)");
        Pattern PRICE_TYPE_4 = Pattern.compile("(\\d+)");
        Matcher matcher = PRICE_TYPE_1.matcher(price);
        String array[] = {"0", "k", "0"};
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                array[i - 1] = matcher.group(i);
            }
            double x = Double.parseDouble("0." + array[2]);
            return "" + (Double.parseDouble(array[0]) + x) * this.getUnit(array[1]);
        } else {
            matcher = PRICE_TYPE_2.matcher(price);
            if (matcher.find()) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    array[i - 1] = matcher.group(i);
                }
                double x = Double.parseDouble("0." + array[1]);
                return "" + (Double.parseDouble(array[0]) + x) * this.getUnit(array[2]);
            } else {
                matcher = PRICE_TYPE_3.matcher(price);
                if (matcher.find()) {
                    for (int i = 1; i <= matcher.groupCount(); i++) {
                        array[i - 1] = matcher.group(i);
                    }
                    return "" + Integer.parseInt(array[0]) * this.getUnit(array[1]);
                }

            }
        }
        return price;
    }

    public int getUnit(String unit) {
        unit = unit.trim();
        switch (unit) {
            case "k":
            case "nghin":
                return 1000;
            case "cu":
            case "tr":
            case "trieu":
                return 1000_000;

            default:
                return 1;
        }
    }

    public String getLocation(List<TaggedWord> taggedWord) {
        String value;
        Search search = new Search();
        String streets = "";
        StreetModel street = new StreetModel();
        String[] arrayStreet = {""};
        List<String> listStreet = new ArrayList<>();
        HashMap<String, Integer> map = new HashMap();
        for (int i = 0; i < taggedWord.size(); i++) {
            value = filterVNString(taggedWord.get(i).getText());
//            if (inArray(PREFIX_PLACE, value)) {
            for (int j = i + 1; j < taggedWord.size(); j++) {
                value = filterVNString(taggedWord.get(j).getText());
                boolean isStreet = street.isStreet("2", value);
                value = isStreet ? value : "";
                if (!inArray(PREFIX_PLACE, value) && value.length() > 0 && !inArray(arrayStreet, value)) {
                    search.setStreet(value);
                    String tempStreet = search.getStreet();
                    if (tempStreet.length() > 0) {
                        map.put("|" + tempStreet + "|", 1);
                    }
                }
            }
//            }

        }
        Set set = map.entrySet();
        Iterator i = set.iterator();
//         Display elements
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            if (me.getKey() != null) {
                streets += me.getKey();
            }
        }
        return streets;
    }

    public void dd(String message) {
        System.out.println(message);
    }

    public static void main(String[] args) throws IOException {
        ExtractorFaceBookPost app = new ExtractorFaceBookPost();
        String message2 = "cho thuê phòng ĐẸP - đầy đủ đồ - ngõ 95 Chùa Bộc\n"
                + "cón 01 phòng tầng 2 và 1 phòng tầng 7 trong nhà 7 tầng đẹp mới có thang máy, gần các trường Học viện ngân hàng, thủy lợi, công đoàn, đại học y.... gần các tuyến đường trung tâm : ngã tư sở , tây sơn, phạm ngọc thạch, trường chinh..... mặt ngõ to 2 ô tô tránh nhau\n"
                + "khu vực có đầy đủ tiện ích : sân tenis, bể bơi, sân bóng đá, bóng rổ, tenis...\n"
                + "- diện tích 22m2, phòng có cửa sổ thoáng\n"
                + "- nội thất đầy đủ bao gồm : tủ lạnh, giường, tủ, bàn làm việc,điều hòa, nóng lạnh, internet, truyền hình cáp....................... ( chỉ việc dọn đồ đến ở)\n"
                + "- giờ giấc thoải mái, không chung chủ, có khu nấu ăn riêng ( vệ sinh tuần dọn 2 lần)\n"
                + "- để xe FREE rộng rãi thoải mái có camera an ninh 24/24\n"
                + "- điện 3,5k/ 1 số\n"
                + "- nước 70k/ 1 người\n"
                + "giá cho thuê 3tr2 \n"
                + "liên hệ: 0978688662";
    }
}
