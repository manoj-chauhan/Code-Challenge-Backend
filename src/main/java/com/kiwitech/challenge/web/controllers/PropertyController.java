package com.kiwitech.challenge.web.controllers;

import com.kiwitech.challenge.persistence.PropertyRepository;
import com.kiwitech.challenge.persistence.entities.Property;
import com.kiwitech.challenge.services.PropertyService;
import com.kiwitech.challenge.web.dtos.PropertyDto;
import com.kiwitech.challenge.web.utils.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class PropertyController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PropertyService propertyService;

    @RequestMapping(value = "property",method = RequestMethod.GET)
    public List<PropertyDto> getProperties() {
        List<Property> props = propertyService.getProperties();
        return PropertyUtil.convert(props);
    }


}
