/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dcp.db;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;
import org.apache.commons.lang.WordUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

/**
 *
 * @author LazyCode
 */
public class StreetModel extends Model implements ModelTemplate {

    public String ref;
    public String id_ref;
    public String name;
    public String street_id;
    public String district_id;
    public String province_id;
    public MongoCollection<Document> collection;
    public MongoDatabase db;

    public StreetModel() {
        super("streets");
        collection = this.createCollection();
        db = this.getDatabase();
    }

    @Override
    public void add() {
        collection = this.createCollection();
        Document doc = new Document("name", this.name)
                .append("street_id", street_id)
                .append("distric_id", district_id)
                .append("province_id", province_id);
        collection.insertOne(doc);
    }

    @Override
    public void update() {

    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isStreet(String province_id, String name) {
        name = WordUtils.capitalize(name);
        FindIterable<Document> iterable = db.getCollection("streets").find(
                new Document("province_id", province_id).append("name", name));
        if (iterable.first() != null) {
            return true;
        }
        return false;
    }
}
