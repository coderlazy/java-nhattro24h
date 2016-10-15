/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dcp.db;

/**
 *
 * @author LazyCode
 */
public class Search {

    private String street;
    private String prefix;
    public Search() {
        
    }

    public String getStreet() {
        if (this.street.length() > 0) {
            boolean condition = (this.street.length() < 3 || isNumeric(this.street)) && "duong".equals(this.prefix);
            if((this.street.length() > 3) || condition){
                return this.street;
            }
        }
        return "";
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public boolean isNumeric(String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }
}
