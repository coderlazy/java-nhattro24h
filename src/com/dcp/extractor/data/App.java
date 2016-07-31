package com.dcp.extractor.data;

import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.*;

import com.google.gson.*;
import org.json.simple.parser.*;
import org.json.simple.JSONArray;

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
import org.json.simple.JSONObject;
import vn.hus.nlp.tokenizer.Tokenizer;
import vn.hus.nlp.tokenizer.VietTokenizer;
import vn.hus.nlp.tokenizer.tokens.TaggedWord;

public class App {

    public static int MAX_PAGE = 10;
    public static boolean CRAWL = true;
    public static String[] WORD_ECLECTRICTY = {"điện:", "đ:", "điện", "đ", "Điện", "Đ", "Điện"};
    public static String[] WORD_WARTER = {"nước:", "nc:", "n:", "nước", "nc", "n"};
    public static String[] WORD_ROM_PRICE = {"giá", "₫", "giá:", "₫:", "phòng"};
    public static String[] WORD_LEASE = {"cho thuê nhà", "cho thuê"};
    public static String[] WORD_TENANT = {"cần thuê nhà", "cần thuê", "cần tìm", "cần thuê phong"};
    public static String[] WORD_DUSTY = {"thanh lý", "sim", "bán"};
    public static int LEASE = 1;
    public static int TENANT = 2;
    public static int DUSTY = 3;
    public int countMessage = 0;

    public static Configuration createConfiguration() {
        ConfigurationBuilder confBuilder = new ConfigurationBuilder();

        confBuilder.setDebugEnabled(true);

        // String url = "https://graph.facebook.com/v2.2/";
        // System.out.println(url);
        // confBuilder.setRestBaseURL(url);
        confBuilder.setOAuthAppId("1543380259298935");
        confBuilder.setOAuthPermissions(
                "user_groups,user_events,user_managed_groups,rsvp_event,publish_pages,user_photos,user_posts,user_friends");
        confBuilder.setOAuthAppSecret("8fea96f557e4ea87afc067a9276069e9");
        confBuilder.setUseSSL(true);
        confBuilder.setJSONStoreEnabled(true);
        Configuration configuration = confBuilder.build();
        return configuration;
    }

    public static void main(String[] args) throws IOException, ParseException, FacebookException {
        App app = new App();
        if (CRAWL) {
            app.extractPost();
        } else {
            Scanner scanner = new Scanner(new File("out.txt"));
            String json = "";
            while (scanner.hasNextLine()) {
                json += scanner.nextLine();
            }
            app.anlanyticPost(json, "data", app);
        }
//        
    }

    public void anlanyticPost(String json, String key, App app) throws ParseException {
        JSONParser jp = new JSONParser();
        try {
            Object obj = jp.parse(json);
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray listOfStates = (JSONArray) jsonObject.get(key);
            Iterator<Object> iterator = listOfStates.iterator();
            String message = "";
            while (iterator.hasNext()) {
                message = iterator.next().toString();
                app.anlanyticPost(message, "message", app);
                if (key.equals("message")) {
                    app.getDataMessage(message, "a", app);
                    System.out.println("=================");
                }
            }
        } catch (Exception e) {
        }
    }

    public void getDataMessage(String message, String key, App app) throws ParseException {
        JSONParser jp = new JSONParser();
        try {
            Object obj = jp.parse(message);
            JSONArray jsonAray = (JSONArray) obj;
            List<DataMessage> listData = new ArrayList<>();
            DataMessage dm;
            for (int i = 0; i < jsonAray.size(); i++) {
                JSONParser jp2 = new JSONParser();
                Object obj2 = jp.parse(jsonAray.get(i).toString());
                JSONObject jsonObject = (JSONObject) obj2;
                dm = new DataMessage();
                dm.setRuleName(jsonObject.get("ruleName").toString());
                dm.setValue(jsonObject.get("value").toString());
                listData.add(dm);
            }
            app.anlanyticMessage(listData, app);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void anlanyticMessage(List<DataMessage> listDataMessage, App app) {
        countMessage++;
        System.out.println("Message " + countMessage);
        for (int i = 1; i < listDataMessage.size(); i++) {
            if (app.detectPrice(listDataMessage.get(i).getRuleName(), listDataMessage.get(i - 1).getValue(), "room")) {
                System.out.println("Giá : " + listDataMessage.get(i).getValue());
            }
            if (app.detectPrice(listDataMessage.get(i).getRuleName(), listDataMessage.get(i - 1).getValue(), "electricty")) {
                System.out.println("Điện : " + listDataMessage.get(i).getValue());
            }
            if (app.detectPrice(listDataMessage.get(i).getRuleName(), listDataMessage.get(i - 1).getValue(), "warter")) {
                System.out.println("Nước : " + listDataMessage.get(i).getValue());
            }
        }
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
//        FileWriter writter1 = new FileWriter("thue.txt");
//        FileWriter writter2 = new FileWriter("chothue.txt");
//        FileWriter writter3 = new FileWriter("oghep.txt");
//        FileWriter writter4 = new FileWriter("timoghep.txt");
//        FileWriter writter5 = new FileWriter("spam.txt");
//        Scanner sc = new Scanner(System.in);
        /* Processing post by post */
        int count = 0;
        writter.write("[");
        for (int countPage = 0; countPage < MAX_PAGE; countPage++) {
            for (int i = 0; i < feeds.size(); i++) {
                Post post = feeds.get(i);
                count++;
                String message = post.getMessage();
//                message = (message == null) ? "" : message.replaceAll("\\s+", " ");
                message = (message == null) ? "" : message;
                //#DucMV 160730 Test
                //  String [] s = message.split(" ");
//                String message1 = "";
//                for(String x: s){
//                    if(x.equals("")){
//                        continue;
//                    }
//                    x=x.toLowerCase();
//                    x=x.substring(0,1).toUpperCase() + x.substring(1).toLowerCase();                     
//                    message1 += x + " ";
//                    
//                }
//                System.out.println("________________________________________________________");
                processing(tokenizer, message, writter);
//                writter.write("\r\n\r\n\r\n__________________________________________\r\n");
//                process(sc, message, writter1, writter2, writter3, writter4, writter5);

            }
            Paging<Post> paging = feeds.getPaging();
            feeds = facebookClient.fetchNext(paging);
        }
        writter.write("]");
        writter.close();
//        writter1.close();
//        writter2.close();
//        writter3.close();
//        writter4.close();
//        writter5.close();
//        sc.close();
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
        if (rule.equals("number2") && inArray(arrayString, aboveValue.toLowerCase())) {
            return true;
        }
        return false;
    }

    public static boolean inArray(String[] arrayString, String value) {
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

    private static void processing(VietTokenizer tokenizer, String message, FileWriter writter) throws IOException {
        String[] listString = tokenizer.tokenize(message);
        Tokenizer a = tokenizer.getTokenizer();
        List<TaggedWord> result = a.getResult();
        List<DataMessage> listData = new ArrayList<DataMessage>();
        DataMessage data;
        for (TaggedWord d : result) {
            data = new DataMessage();
            data.setRuleName(d.getRule().getName());
            data.setValue(d.getText());
            listData.add(data);
        }
        String json = "{\"message\":[";
        json += new Gson().toJson(listData);
        json += "]}, ";
        writter.write(json);
    }
}
