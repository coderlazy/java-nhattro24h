/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dcp.extractor.data;

import com.dcp.db.RentalHouse;
import com.dcp.db.Search;
import com.dcp.db.Streets;
import java.io.FileWriter;
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

/**
 *
 * @author LazyCode
 */
public class ExtractorFaceBookPost {

    public static String[] WORD_ECLECTRICTY = {"dien:", "d:", "d", "Dien", "D", "dien", "điện:", "đ:", "đ", "Điện", "D", "điện"};
    public static String[] WORD_WARTER = {"nuoc:", "nc:", "n:", "nuoc", "nc", "n", "nước:", "nc:", "n:", "nước"};
    public static String[] WORD_ROM_PRICE = {"giá", "₫", "giá:", "phong", "gia", "gia:", "₫:", "phòng"};
    public static String[] WORD_LEASE = {"cho thue nha", "cho thue"};
    public static String[] WORD_TENANT = {"can thue nha", "can thue", "can tim", "can thue phong"};
    public static String[] WORD_DUSTY = {"thanh ly", "sim"};
    public static String[] PREFIX_PLACE = {"o", "khu vuc", "tai", "dia chi", "ngo", "đc", "khu", "gan", "duong"};

    public ExtractorFaceBookPost() {
    }

    public List<TaggedWord> processing(VietTokenizer tokenizer, String message) throws IOException {
        String[] listString = tokenizer.tokenize(message);
        Tokenizer a = tokenizer.getTokenizer();
        List<TaggedWord> result = a.getResult();
//        RentalHouse rh = new RentalHouse();
//        RentalHouseObject rho;
//        rho = anlanyticMessage(result, message);
//        if (rho != null) {
//            rh.create(rho);
//        }
        return result;
    }

    public String[] anlanyticMessage(List<TaggedWord> taggedWord, String message) {
//        RentalHouseObject rho =
        String price = "";
        String electricPrice = "";
        String waterPrice = "";
        String location = "";
        for (int i = 1; i < taggedWord.size(); i++) {
            if (this.detectPrice(taggedWord.get(i).getRule().getName(), taggedWord.get(i - 1).getText(), "room")) {
                price = taggedWord.get(i).getText();
            }
            if (this.detectPrice(taggedWord.get(i).getRule().getName(), taggedWord.get(i - 1).getText(), "electricty")) {
                electricPrice = taggedWord.get(i).getText();
            }
            if (this.detectPrice(taggedWord.get(i).getRule().getName(), taggedWord.get(i - 1).getText(), "warter")) {
                waterPrice = taggedWord.get(i).getText();
            }
        }
        String r[] = {price, electricPrice, waterPrice};
        return r;
//        location = getLocation(taggedWord);
//        System.out.print("Message: " + message);
//        RentalHouseObject rho = null;
//        if (message.length() > 0) {
//            rho = new RentalHouseObject(message.substring(0, 5), message, price, price, "updating", location, "Ha Noi", waterPrice, electricPrice, "updating", null, null, true, "none");
//        }
//        return rho;
    }

    public String detectPriceWater(List<TaggedWord> taggedWord) {
        String waterPrice = "";
        for (int i = 1; i < taggedWord.size(); i++) {
            if ("wprice".equals(taggedWord.get(i).getRule().getName())) {
                System.out.println(taggedWord.get(i).getText());
                return taggedWord.get(i).getText();
            }
        }
        return waterPrice;
    }

    public boolean detectPrice(String rule, String aboveValue, String type) {
        String[] arrayString;
        switch (type) {
            case "electricty":
                arrayString = WORD_ECLECTRICTY;
                break;
            case "warter":
                arrayString = WORD_WARTER;
            case "room":
                arrayString = WORD_ROM_PRICE;
                break;
            default:
                arrayString = new String[0];
                break;
        }
        return rule.equals("number2") && inArray(arrayString, aboveValue.toLowerCase());
    }

    public boolean inArray(String[] arrayString, String value) {
        for (int i = 0; i < arrayString.length; i++) {
            if (value.compareTo(arrayString[i]) == 0) {
                return true;
            }
        }
        return false;
    }

    public String getLocation(List<TaggedWord> taggedWord) {
        String value;
        Search search = new Search();
        String streets = "";
        Streets street = new Streets();
        String[] arrayStreet = {""};
        List<String> listStreet = new ArrayList<>();
        HashMap<String, Integer> map = new HashMap();
        for (int i = 0; i < taggedWord.size(); i++) {
            value = filterVNString(taggedWord.get(i).getText());
            if (inArray(PREFIX_PLACE, value)) {
                for (int j = i + 1; j < taggedWord.size(); j++) {
                    value = filterVNString(taggedWord.get(j).getText());
                    LocationData ld = street.findLocation(2, value);
                    value = (ld != null) ? ld.getName() : "";
                    if (!inArray(PREFIX_PLACE, value) && value.length() > 0 && !inArray(arrayStreet, value)) {
                        search.setStreet(value);
                        String tempStreet = search.getStreet();
                        if (tempStreet.length() > 0) {
                            map.put("|" + tempStreet + "|", 1);
                        }
                    }
                }
            }

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

}
