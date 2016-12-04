/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dcp.db;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 *
 * @author LazyCode
 */
public class ProvinceModel extends Model implements ModelTemplate {

    public int province_id;
    public String name;
    public MongoCollection<Document> collection;

    public ProvinceModel() {
        super("provinces");
        collection = this.createCollection();
    }

    @Override
    public void add() {
        
        Document doc = new Document("name", this.name).append("province_id", this.province_id);
        collection.insertOne(doc);
    }

    @Override
    public void update() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
