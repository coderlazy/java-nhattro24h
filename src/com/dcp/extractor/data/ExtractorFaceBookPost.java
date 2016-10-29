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
    public static String[] STOP_WORD = {"phong", "gia", "dien", "nuoc", "nc", "gia thue phong", "cho thue", "phong gia"};

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

    public String detectPriceWater(String message) {
        message = this.filterVNString(message);
        String regex = "(nc|nuoc)\\s*(:)?\\s*((\\d+([\\.,]\\d+)*)(k|nghìn|nghin|ng|d)?(\\d*))";
        Matcher matcher = this.regex(regex, message);
        System.out.println(matcher);
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

    public String detectPriceHouse(String message) {
        message = this.filterVNString(message);
        String regex = "(gia thue phong|cho thue|phong gia|gia|phong)(&lt;|&gt;|~)?\\s*(:?)\\s*([0-9]*)?[0-9]+([\\.,]\\d+)*(\\s|tỉ|tỷ|triệu|ngàn|nghìn|trăm|chục|tr|trieu|cu|củ|k\\d+|nghìn|nghin|lít|lit|t\\d+)*(([0-9]*)?[0-9]+([\\.,]\\d+)*)*";
        Matcher matcher = this.regex(regex, message);
        String rs = "";
        if (matcher.find()) {
            rs += matcher.group(0);
            for (int i = 1; i <= matcher.groupCount(); i++) {
                if (matcher.group(i) != null && matcher.group(i) != "") {
                    rs += "-" + matcher.group(i);
                }
            }
        }
        System.out.println("rs: " + matcher.find());
        return rs;
    }

    public boolean detectPrice(String rule, String aboveValue, String type) {
        String[] arrayString;
        switch (type) {
            case "electricty":
                arrayString = WORD_ECLECTRICTY;
                break;
            case "warter":
                arrayString = WORD_WARTER;
                break;
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

    public Matcher regex(String regex, String content) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        return matcher;
    }

    public String converPrice(String price) {
        for (int i = 0; i < STOP_WORD.length; i++) {
            price = price.replaceAll(STOP_WORD[i], "");
        }
        price = price.replaceAll("\\s+", "");
        String UNIT = "(\\w+)";
        Pattern PRICE_TYPE_1 = Pattern.compile("(\\d+)" + UNIT + "(\\d+)");
        Pattern PRICE_TYPE_2 = Pattern.compile("(\\d+)[,.](\\d+)" + UNIT);
        Pattern PRICE_TYPE_3 = Pattern.compile("(\\d+)" + UNIT);

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
        return "0";
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

    public static void main(String[] args) {
        ExtractorFaceBookPost app = new ExtractorFaceBookPost();
        String text[] = {"75k2", "2k", "3 cu", "4,5 cu", "4,5", "3.5cu"};
        for (String x : text) {
            System.out.println(app.converPrice(x));
        }
    }
}
