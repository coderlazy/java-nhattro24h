package com.dcp.db;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
import java.util.Arrays;
import java.util.List;
import org.bson.Document;

/**
 *
 * @author LazyCode
 */
public class MongoConector {

    private static final String host = "127.0.0.1";
    private static final int port = 69;
    private static final String dbName = "nhatro";
    public static MongoClient connector;
    public static MongoCollection<Document> collection;

    //mongodb://heroku_s33jfbhv:7jgp00f8t55g4pnec8eu5kgj7j@ds153785.mlab.com:53785/heroku_s33jfbhv
    public MongoConector() {
        getInstance();
    }

    public static MongoClient connectMongonDB() {
        connector = null;
        try {
            connector = new MongoClient(host, port);
//            connector = new MongoClient(
//                    new MongoClientURI("mongodb://heroku_s33jfbhv:7jgp00f8t55g4pnec8eu5kgj7j@ds153785.mlab.com:53785/heroku_s33jfbhv")
//            );
        } catch (Exception e) {
            System.out.println("MongoException: " + e.getMessage());
        }
        return connector;
    }

    public static MongoClient getInstance() {
        try {
            if (connector == null) {
                connector = connectMongonDB();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return connector;
    }

    public MongoDatabase getDatabase() {
        MongoDatabase database = null;
        database = connector.getDatabase(dbName);
        return database;
    }

//    public void insertRentalhouse(String collectionName) {
//        collection = database.getCollection(collectionName);
//        Document doc = new Document("name", "MongoDB")
//                .append("type", "database")
//                .append("count", 1)
//                .append("versions", Arrays.asList("v3.2", "v3.0", "v2.6"))
//                .append("info", new Document("x", 203).append("y", 102));
//        collection.insertOne(doc);
//    }
//
//    public void showDocument() {
//        Document myDoc;
//        myDoc = collection.find(eq("count", 1)).first();
//        collection.updateOne(eq("count", 1), new Document("$set", new Document("count", 110)));
//        System.out.println(myDoc);
//    }
}
