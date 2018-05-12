package com.kiwitech.challenge.web.utils;

import com.kiwitech.challenge.persistence.entities.Property;
import com.kiwitech.challenge.web.dtos.PropertyDto;

import java.util.ArrayList;
import java.util.List;

public class PropertyUtil {
    public static List<PropertyDto> convert(List<Property> properties) {
        List<PropertyDto> propertyDtoList = new ArrayList<>();
        for (Property p: properties) {
            PropertyDto dto = new PropertyDto();
            dto.setId(p.getId());
            dto.setPropertyName(p.getPropertyName());
            dto.setPrice(p.getPrice());
            dto.setPropertyLocation(p.getPropertyLocation());
            dto.setPropertyType(p.getPropertyType());
            propertyDtoList.add(dto);
        }
        return propertyDtoList;
    }
}
