package com.kiwitech.challenge.services;

import com.kiwitech.challenge.persistence.entities.Property;
import com.kiwitech.challenge.web.dtos.PropertyDto;

import java.util.List;

public interface PropertySearchService {

    public List<PropertyDto> getFeaturedProperties();
}
