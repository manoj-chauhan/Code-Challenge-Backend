package com.kiwitech.challenge.web.controllers;

import com.kiwitech.challenge.persistence.CityRepository;
import com.kiwitech.challenge.persistence.entities.City;
import com.kiwitech.challenge.persistence.entities.Property;
import com.kiwitech.challenge.services.PropertyDataProvider;
import com.kiwitech.challenge.services.PropertySearchService;
import com.kiwitech.challenge.web.dtos.PropertyDto;
import com.kiwitech.challenge.web.utils.PropertyUtil;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
public class PropertyController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PropertySearchService propertyService;

    @Autowired
    PropertyDataProvider propertyDataProvider;

    @Autowired
    CityRepository cityRepository;

    @Autowired
    PropertyUtil propertyUtil;

    @RequestMapping(value = "property",method = RequestMethod.GET)
    public List<PropertyDto> getProperties() {
        List<Property> properties = propertyService.getFeaturedProperties();
        return propertyUtil.convert(properties);
    }

    @RequestMapping(value = "property/image/{imageId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> downloadUserAvatarImage(@PathVariable(value="imageId") String imageId) throws IOException {
        Resource resource = new ClassPathResource("images/" + imageId);
        InputStream io = resource.getInputStream();

        return ResponseEntity.ok()
                .contentLength(io.available())
                .contentType(MediaType.IMAGE_JPEG)
                .body(new InputStreamResource(io));
    }

    @RequestMapping(value = "city/suggestions",method = RequestMethod.GET)
    public List<City> getSuggestedCities(@RequestParam("initials") @Length(min = 3)String initials) {
        return cityRepository.findCitiesStartingWith(initials);
    }


}
