package com.dcp.extractor.data;

import com.dcp.db.Streets;
import com.dcp.db.Search;
import com.dcp.db.Connector;
import com.dcp.db.DistrictsModel;
import com.dcp.db.ProvinceModel;
import com.dcp.db.RentalHouse;
import com.dcp.db.StreetModel;
import com.dcp.db.WardsModel;
import com.mongodb.BasicDBObject;
import static com.mongodb.client.model.Filters.eq;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.*;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Paging;
import facebook4j.Post;
import facebook4j.ResponseList;
import facebook4j.auth.AccessToken;
import facebook4j.auth.OAuthAuthorization;
import facebook4j.auth.OAuthSupport;
import facebook4j.conf.Configuration;
import facebook4j.conf.ConfigurationBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bson.BSON;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
//import org.json.simple.JSONObject;
import vn.hus.nlp.tokenizer.Tokenizer;
import vn.hus.nlp.tokenizer.VietTokenizer;
import vn.hus.nlp.tokenizer.tokens.TaggedWord;

public class App {

    public static int MAX_PAGE = 50;
    public static boolean CRAWL = true;
    public static String[] WORD_ECLECTRICTY = {"dien:", "d:", "d", "Đien", "Đ"};
    public static String[] WORD_WARTER = {"nuoc:", "nc:", "n:", "nuoc", "nc", "n"};
    public static String[] WORD_ROM_PRICE = {"gia", "₫", "gia:", "₫:", "phong"};
    public static String[] WORD_LEASE = {"cho thue nha", "cho thue"};
    public static String[] WORD_TENANT = {"can thue nha", "can thue", "can tim", "can thue phong"};
    public static String[] WORD_DUSTY = {"thanh ly", "sim"};
    public static String[] PREFIX_PLACE = {"o", "khu vuc", "tai", "dia chi", "ngo", "đc", "khu", "gan", "duong"};
    public static int LEASE = 1;
    public static int TENANT = 2;
    public static int DUSTY = 3;
    public int countMessage = 0;

    public App() {

    }

    public static Configuration createConfiguration() {
        ConfigurationBuilder confBuilder = new ConfigurationBuilder();

        confBuilder.setDebugEnabled(true);

        confBuilder.setOAuthAppId("1543380259298935");
        confBuilder.setOAuthPermissions(
                "user_groups,user_events,user_managed_groups,rsvp_event,publish_pages,user_photos,user_posts,user_friends");
        confBuilder.setOAuthAppSecret("8fea96f557e4ea87afc067a9276069e9");
        confBuilder.setUseSSL(true);
        confBuilder.setJSONStoreEnabled(true);
        Configuration configuration = confBuilder.build();
        return configuration;
    }

    public static void main(String[] args) throws Exception {
        App app = new App();
//        app.CrawlData(app);
//        app.seedProvinces();
//        app.seedDistrict();
//        app.seedWards();
//        app.seedStreet();
    }

    public void CrawlData(App app) throws Exception {
        if (CRAWL) {
            app.extractPost();
        } else {
            Scanner scanner = new Scanner(new File("out.txt"));
            String json = "";
            while (scanner.hasNextLine()) {
                json += scanner.nextLine();
            }
//            app.anlanyticPost(json, "data");
        }
        System.out.println("done");
    }

    @SuppressWarnings("empty-statement")
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

    public void typeOfPost(String message) {

    }

    public void extractPost() throws FacebookException, IOException {
        Configuration configuration = createConfiguration();
        FacebookFactory facebookFactory = new FacebookFactory(configuration);
        Facebook facebookClient = facebookFactory.getInstance();
        AccessToken accessToken = null;
        try {
            OAuthSupport oAuthSupport = new OAuthAuthorization(configuration);
            accessToken = oAuthSupport.getOAuthAppAccessToken();
        } catch (FacebookException e) {
            System.err.println("Error while creating access token " + e.getLocalizedMessage());
        }
        facebookClient.setOAuthAccessToken(accessToken);
        System.out.println("Access Token" + accessToken);
        /* Get feeds from facebook group */
        ResponseList<Post> feeds = facebookClient.getFeed("451604054903475");
        VietTokenizer tokenizer = new VietTokenizer("tokenizer.properties");
        FileWriter writter = new FileWriter("out.txt");
        int count = 0;
        for (int countPage = 0; countPage < MAX_PAGE; countPage++) {
            for (int i = 0; i < feeds.size(); i++) {
                Post post = feeds.get(i);
                count++;
                String message = post.getMessage();
                System.out.println(post.getId());
                message = (message == null) ? "" : message;
                System.out.println(message);
                processing(tokenizer, message, writter);
            }
            Paging<Post> paging = feeds.getPaging();
            feeds = facebookClient.fetchNext(paging);
        }
        writter.close();
    }

    public boolean detectPrice(String rule, String aboveValue, String type) {
        System.out.print(aboveValue);
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
            if (value.equals(arrayString[i])) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkType(String message, String[] check) {
        for (String x : check) {
            if (message.contains(x)) {
                return true;
            }
        }
        return false;
    }

    private void processing(VietTokenizer tokenizer, String message, FileWriter writter) throws IOException {
        String[] listString = tokenizer.tokenize(message);
        Tokenizer a = tokenizer.getTokenizer();
        List<TaggedWord> result = a.getResult();
        System.out.println(result);
        RentalHouse rh = new RentalHouse();
        RentalHouseObject rho;
        rho = anlanyticMessage(result, message);
        if (rho != null) {
            rh.create(rho);
        }
    }

    public RentalHouseObject anlanyticMessage(List<TaggedWord> taggedWord, String message) {
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
        location = getLocation(taggedWord);
        System.out.print("Message: " + message);
        RentalHouseObject rho = null;
        if (message.length() > 0) {
            rho = new RentalHouseObject(message.substring(0, 5), message, price, price, "updating", location, "Ha Noi", waterPrice, electricPrice, "updating", null, null, true, "none");
        }
        return rho;
    }

    //Phung's function
    private static void process(Scanner sc, String message, FileWriter writter1, FileWriter writter2, FileWriter writter3, FileWriter writter4, FileWriter writter5) throws IOException {
        System.out.println(message);
        System.out.println("_____________________");
        //  System.out.println("Select ? 1: thue 2: cho thue 3 : o ghep 4: tim o ghep #: spam");
        String[] thuePhong = {"can phong", "muon thue phong", "tim phong", "con trong"};
        String[] choThue = {"co nha", "cho thue", "nhuong lai", "co phong", "chinh chu", "dien tich"};
        String[] oGhep = {"ghep"};
        //String[] timOGhep = {""};
        String temp = message.toLowerCase();
        temp = temp.replaceAll("[̣̀̃̉́]", "");
        temp = temp.replaceAll("̣₫", "đ");
        temp = temp.replaceAll("[iìỉĩíịj]", "i");
        temp = temp.replaceAll("[eèẻẽéẹêềểễếệ]", "e");
        temp = temp.replaceAll("[aàảãáạăằẳẵắặâầẩẫấậ]", "a");
        temp = temp.replaceAll("[uùủũúụưừửữứự]", "u");
        temp = temp.replaceAll("[oòỏõóọôồổỗốộơờởỡớợ]", "o");
        temp = temp.replaceAll("[yỳỷỹýỵ]", "y");
//        System.out.println(temp);
//        System.exit(0);
        int n = 0;
        if (checkType(temp, thuePhong)) {
            n = 1;
        } else if (checkType(temp, choThue)) {
            n = 2;
        } else if (checkType(temp, oGhep)) {
            n = 3;
        } else {
            //n = 0;
        }

        switch (n) {
            case 1:
                writter1.write(temp + "\r\n ===================================== \r\n");

                break;
            case 2:
                writter2.write(temp + "\r\n ===================================== \r\n");
                break;
            case 3:
                writter3.write(temp + "\r\n ===================================== \r\n");
                break;
            case 4:
                writter4.write(temp + "\r\n ===================================== \r\n");
                break;
            default:
                writter5.write(temp + "\r\n ===================================== \r\n");
        }
    }

    public String filterVNString(String string) {
        string = string.replaceAll("[̣̀̃̉́]", "");
        string = string.replaceAll("̣₫", "đ");
        string = string.replaceAll("[iìỉĩíịj]", "i");
        string = string.replaceAll("[eèẻẽéẹêềểễếệ]", "e");
        string = string.replaceAll("[aàảãáạăằẳẵắặâầẩẫấậ]", "a");
        string = string.replaceAll("[uùủũúụưừửữứự]", "u");
        string = string.replaceAll("[oòỏõóọôồổỗốộơờởỡớợ]", "o");
        string = string.replaceAll("[yỳỷỹýỵ]", "y");
        return string;
    }

    public void seedStreet() {
        Connector cnn = Connector.getInstance();
        String query = "select * from streets join districts on streets.district_id = districts.id";
        ResultSet rs = null;
        DistrictsModel dm = new DistrictsModel();
        Document doc;
        try {
            rs = cnn.runQuery(query);
            while (rs.next()) {
                StreetModel sm = new StreetModel();
                doc = dm.collection.find(eq("district_id", rs.getInt("district_id") + "")).first();
                sm.name = rs.getString("name");
                sm.street_id = rs.getString("id");
                sm.district_id = rs.getString("district_id");
                sm.province_id = rs.getString("province_id");
                sm.add();
            }
            rs.close();
        } // Now do something with the ResultSet .... // Now do something with the ResultSet ....
        catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public void seedWards() {

        Connector cnn = Connector.getInstance();
        String query = "select * from wards";
        ResultSet rs = null;
        DistrictsModel dm = new DistrictsModel();
        Document doc;
        try {
            rs = cnn.runQuery(query);
            while (rs.next()) {
                WardsModel wm = new WardsModel();
                doc = dm.collection.find(eq("district_id", rs.getInt("district_id") + "")).first();
                wm.name = rs.getString("name");
                wm.prefix = rs.getString("pre");
                wm.ward_id = rs.getString("id");
                wm.id_ref = doc.get("_id") + "";
                wm.ref = "districts";
                wm.add();
            }
            rs.close();
        } // Now do something with the ResultSet .... // Now do something with the ResultSet ....
        catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public void seedDistrict() {
        Connector cnn = Connector.getInstance();
        String query = "select * from districts";
        ResultSet rs = null;
        ProvinceModel pm = new ProvinceModel();
        Document doc;
        try {
            rs = cnn.runQuery(query);
            while (rs.next()) {
                DistrictsModel dm = new DistrictsModel();
                doc = pm.collection.find(eq("province_id", rs.getInt("province_id"))).first();
                dm.name = rs.getString("name");
                dm.id_ref = doc.get("_id") + "";
                dm.ref = "provinces";
                dm.district_id = rs.getString("id");
                dm.add();
            }
            rs.close();
        } // Now do something with the ResultSet .... // Now do something with the ResultSet ....
        catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public void seedProvinces() throws SQLException {
        Connector cnn = Connector.getInstance();
        String query = "select * from provinces";
        ResultSet rs = null;
        try {
            rs = cnn.runQuery(query);
            while (rs.next()) {
                ProvinceModel pm = new ProvinceModel();
                pm.name = rs.getString("name");
                pm.province_id = rs.getInt("id");
                pm.add();
            }
            rs.close();
        } // Now do something with the ResultSet ....
        catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

}
