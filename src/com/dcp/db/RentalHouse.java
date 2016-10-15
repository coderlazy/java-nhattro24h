package com.dcp.db;

import com.dcp.extractor.data.RentalHouseObject;
import com.mongodb.client.MongoCollection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.bson.Document;

/**
 *
 * @author LazyCode
 */
public class RentalHouse extends Model implements ModelTemplate {

    MongoCollection<Document> collection;

    public RentalHouse() {
        super("rentalhouses");
        collection = createCollecton();
    }

//    public RentalHouse(){
//        super(collectionName);
//    }
    public void create(RentalHouseObject rh) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        Document doc = new Document(
                "created", dateFormat.format(date))
                .append("title", rh.getTitle())
                .append("content", rh.getContent())
                .append("priceFrom", rh.getPriceFrom())
                .append("priceTo", rh.getPriceTo())
                .append("area", rh.getArea())
                .append("location", rh.getLocation())
                .append("city", rh.getCity())
                .append("waterPrice", rh.getWaterPrice())
                .append("electricPrice", rh.getElectricPrice())
                .append("contact", rh.getContact())
                .append("publishedAt", rh.getPublishedAt())
                .append("pictures", rh.getPictures())
                .append("isActive", rh.getIsActive())
                .append("user", rh.getUser());
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

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
