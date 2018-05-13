package com.kiwitech.challenge.services;

import com.kiwitech.challenge.persistence.entities.Property;

import java.util.List;

public interface PropertySearchService {

    public List<Property> getFeaturedProperties();
}
