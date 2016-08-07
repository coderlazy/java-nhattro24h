/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dcp.extractor.data;

/**
 *
 * @author LazyCode
 */
public class Wards extends LocationData implements Location{

    public Wards(int id, String name) {
        super(id, name);
    }
    
    @Override
    public LocationData findLocation(int idProvince, String keyWord) {
        Connector cnn = Connector.getInstance();
         String query = "select * from `provinces` left join `districts` on `districts`.`province_id` "
                + "= `provinces`.`id` left join `wards` on `wards`.`district_id` "
                + "= `districts`.`id` where `provinces`.`id` "
                + "= " + idProvince + " and `wards`.`name_search` = " + keyWord;
        return cnn.runQuery(query, "id", "name");
    }
}
