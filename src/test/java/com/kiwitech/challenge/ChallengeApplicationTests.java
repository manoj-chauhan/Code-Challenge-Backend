package com.kiwitech.challenge;

import com.kiwitech.challenge.services.PropertySearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChallengeApplicationTests {

	@Autowired
	DataPopulator dataPopulator;

	@Test
	public void contextLoads() {
		dataPopulator.populateData();
	}

	@Test
	public void SearchByLocationTest() {
		dataPopulator.searchProperty( 28.465092,77.034414,1);
	}
}
