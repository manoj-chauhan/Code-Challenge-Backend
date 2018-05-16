package com.kiwitech.challenge.services;

import com.kiwitech.challenge.persistence.CityRepository;
import com.kiwitech.challenge.persistence.entities.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {

    @Autowired
    CityRepository cityRepository;

    /**
     * Provides the matching cities.
     * @param initials starting characters of the city name.
     * @return list of cities
     */
    List<City> getMatchingCities(String initials) {
        List<City> list = cityRepository.findCitiesStartingWith(initials);
        return list;
    }


}
