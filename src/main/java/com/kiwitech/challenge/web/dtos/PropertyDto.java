package com.kiwitech.challenge.web.dtos;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class PropertyDto {

    private Long id;
    private String propertyName;
    private String description;
    private double locationLattitude;
    private double getLocationLattitude;
    private String city;
    private int beds;
    private int baths;
    private int kitchens;
    private boolean petsAllowed;
    private String propertyType;
    private String minPrice;
    private String maxPrice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLocationLattitude() {
        return locationLattitude;
    }

    public void setLocationLattitude(double locationLattitude) {
        this.locationLattitude = locationLattitude;
    }

    public double getGetLocationLattitude() {
        return getLocationLattitude;
    }

    public void setGetLocationLattitude(double getLocationLattitude) {
        this.getLocationLattitude = getLocationLattitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getBeds() {
        return beds;
    }

    public void setBeds(int beds) {
        this.beds = beds;
    }

    public int getBaths() {
        return baths;
    }

    public void setBaths(int baths) {
        this.baths = baths;
    }

    public int getKitchens() {
        return kitchens;
    }

    public void setKitchens(int kitchens) {
        this.kitchens = kitchens;
    }

    public boolean isPetsAllowed() {
        return petsAllowed;
    }

    public void setPetsAllowed(boolean petsAllowed) {
        this.petsAllowed = petsAllowed;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }
}
