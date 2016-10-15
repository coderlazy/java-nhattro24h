/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dcp.db;

import com.dcp.db.Connector;
import com.dcp.extractor.data.Location;
import com.dcp.extractor.data.LocationData;

/**
 *
 * @author LazyCode
 */
public class Streets extends LocationData implements Location {
    
    public Streets() {
        
    }
    

    @Override
    public LocationData findLocation(int idProvince, String keyWord) {
        Connector cnn = Connector.getInstance();
        String query = "select streets.id, streets.name from `provinces` left join `districts` on `districts`.`province_id` "
                + "= `provinces`.`id` left join `streets` on `streets`.`district_id` "
                + "= `districts`.`id` where `provinces`.`id` "
                + "= " + idProvince + " and `streets`.`name_search` = \"" + keyWord+"\"";
        return cnn.runQuery(query, "id", "name");
    }

}
