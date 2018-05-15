package com.kiwitech.challenge.web.utils;

import com.kiwitech.challenge.persistence.entities.Property;
import com.kiwitech.challenge.web.dtos.PropertyDto;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
            Random random = new Random();
            int rndInt = Math.abs(random.nextInt()%50) + 1;
            dto.setImage("http://localhost:8000/property/image/img" + rndInt + ".jpeg");
            propertyDtoList.add(dto);
        }
        return propertyDtoList;
    }
}
