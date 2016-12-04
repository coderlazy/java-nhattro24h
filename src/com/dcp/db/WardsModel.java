/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dcp.db;

import com.mongodb.DBRef;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 *
 * @author LazyCode
 */
public class WardsModel extends Model implements ModelTemplate {

    public String ref;
    public String id_ref;
    public String name;
    public String ward_id;
    public String prefix;
    public MongoCollection<Document> collection;

    public WardsModel() {
        super("wards");
        collection = this.createCollection();
    }

    @Override
    public void add() {

        Document doc = new Document("name", this.name).append("ward_id", ward_id).append("prefix", prefix);
        DBRef dr = new DBRef(this.ref, this.id_ref);
        doc.append(ref, dr);
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
