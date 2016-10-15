/*
 * To change this license header; choose License Headers in Project Properties.
 * To change this template file; choose Tools | Templates
 * and open the template in the editor.
 */
package com.dcp.extractor.data;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author LazyCode
 */
public class RentalHouseObject {

    private String title;
    private String content;
    private String priceFrom;
    private String priceTo;
    private String area;
    private String location;
    private String city;
    private String waterPrice;
    private String electricPrice;
    private String contact;
    private Date publishedAt;
    private ArrayList<String> pictures;
    private Boolean isActive;
    private String user;

    public RentalHouseObject(String title, String content,
            String priceFrom, String priceTo,
            String area, String location, String city,
            String waterPrice, String electricPrice,
            String contact, Date publishedAt,
            ArrayList<String> pictures, Boolean isActive,
            String user) {
        this.title = title;
        this.content = content;
        this.priceFrom = priceFrom;
        this.priceTo = priceTo;
        this.area = area;
        this.location = location;
        this.waterPrice = waterPrice;
        this.electricPrice = electricPrice;
        this.contact = contact;
        this.publishedAt = publishedAt;
        this.pictures = pictures;
        this.isActive = isActive;
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPriceFrom() {
        return priceFrom;
    }

    public void setPriceFrom(String priceFrom) {
        this.priceFrom = priceFrom;
    }

    public String getPriceTo() {
        return priceTo;
    }

    public void setPriceTo(String priceTo) {
        this.priceTo = priceTo;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWaterPrice() {
        return waterPrice;
    }

    public void setWaterPrice(String waterPrice) {
        this.waterPrice = waterPrice;
    }

    public String getElectricPrice() {
        return electricPrice;
    }

    public void setElectricPrice(String electricPrice) {
        this.electricPrice = electricPrice;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt) {
        this.publishedAt = publishedAt;
    }

    public ArrayList<String> getPictures() {
        return pictures;
    }

    public void setPictures(ArrayList<String> pictures) {
        this.pictures = pictures;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
