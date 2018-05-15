package com.kiwitech.challenge.persistence.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//| city       | text
//| city_ascii | text
//| lat        | double
//| lng        | double
//| pop        | int(11)
//| country    | text
//| iso2       | text
//| iso3       | text
//| province   | text

}
