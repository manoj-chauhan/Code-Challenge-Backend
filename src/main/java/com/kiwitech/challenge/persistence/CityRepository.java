package com.kiwitech.challenge.persistence;


import com.kiwitech.challenge.persistence.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    @Query("Select c from City c where c.name like %:initials%")
    List<City> findCitiesStartingWith(@Param("initials") String initials);

}
