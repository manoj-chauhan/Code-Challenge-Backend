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
            dto.setDescription(p.getDescription());
            dto.setLocationLattitude(p.getLocationLattitude());
            dto.setGetLocationLattitude(p.getGetLocationLattitude());
            dto.setCity(p.getCity());
            dto.setBeds(p.getBeds());
            dto.setBaths(p.getBaths());
            dto.setKitchens(p.getKitchens());
            dto.setPetsAllowed(p.isPetsAllowed());
            dto.setPropertyType(p.getPropertyType());
            dto.setMinPrice(p.getMinPrice());
            dto.setMaxPrice(p.getMaxPrice());
            propertyDtoList.add(dto);
        }
        return propertyDtoList;
    }
}
