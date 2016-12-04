/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dcp.db;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 *
 * @author LazyCode
 */
abstract class Model {

    public String collectionName;

    public Model(String collectionName) {
        this.collectionName = collectionName;
    }

    public MongoCollection<Document> createCollection() {
        MongoConector mConnector = new MongoConector();
        MongoCollection<Document> collection = mConnector.getDatabase().getCollection(collectionName);
        return collection;
    }

    public MongoCollection<Document> getCollection() {
        MongoConector mConnector = new MongoConector();
        MongoCollection<Document> collection = mConnector.getDatabase().getCollection(collectionName);
        return collection;
    }

    public MongoDatabase getDatabase() {
        MongoConector mConnector = new MongoConector();
        MongoDatabase db = mConnector.getDatabase();//.getCollection(collectionName);
        return db;
    }
}
