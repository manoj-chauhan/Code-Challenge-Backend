package com.kiwitech.challenge.web.controllers;

import com.kiwitech.challenge.web.dtos.PropertyDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class PropertyController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "property",method = RequestMethod.GET)
    public PropertyDto getProperty() {
        return new PropertyDto();
    }


}
