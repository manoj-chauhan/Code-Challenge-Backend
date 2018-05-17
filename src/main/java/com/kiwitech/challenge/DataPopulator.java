package com.kiwitech.challenge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwitech.challenge.persistence.entities.Property;
import com.kiwitech.challenge.web.dtos.PropertyDto;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.ActiveShardCount;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;

@Component
public class DataPopulator {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Environment env;

    private int getRandomNumber(int from) {
        Random random = new Random();
        return Math.abs(random.nextInt() % from) + 1;
    }

    public void populateData() {

        Resource resource = new ClassPathResource("locationlist.csv");



        String[] propertyNames = {
                "Lovely Home",
                "Hotel Inn",
                "Taj",
                "Roser Hotel",
                "Yellow Door",
                "Heritage Hotel"
        };

        String[] propertyDescriptions = {
                "Beautiful Place",
                "Holiday Destination",
                "Holiday world",
                "Relax at home",
                "Just like my home",
                "Will go there"
        };

        String[] propertyTypes = {
                "Rented",
                "Owned",
                "Leased",
                "Resale",
                "Auctioned"
        };

        int[][] propertyPrices = {
                {100,200},
                {200,400},
                {400,1000},
                {1000,2000},
                {2000,5000},
                {5000,12000}
        };
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            String location = null;
            while((location = reader.readLine()) != null) {
                String[] locationCoordinate = location.split(",");
                String city = locationCoordinate[0];
                double latitude = Double.parseDouble(locationCoordinate[1]);
                double longitude = Double.parseDouble(locationCoordinate[2]);

                PropertyDto dto = new PropertyDto();
                dto.setId((long) getRandomNumber(10000000));
                dto.setPropertyName(propertyNames[getRandomNumber(propertyNames.length - 1)]);
                dto.setDescription(propertyDescriptions[getRandomNumber(propertyDescriptions.length - 1)]);
                PropertyDto.Location l = new PropertyDto.Location();
                l.setLat(latitude);
                l.setLon(longitude);
                dto.setLocation(l);
                dto.setCity(city);
                dto.setBeds(getRandomNumber(5));
                dto.setBaths(getRandomNumber(4));
                dto.setKitchens(getRandomNumber(3));
                dto.setPetsAllowed(getRandomNumber(2) > 1);
                dto.setPropertyType(propertyTypes[getRandomNumber(propertyTypes.length - 1)]);
                int randomPriceIndex = getRandomNumber(propertyPrices.length - 1);
                dto.setMinPrice(propertyPrices[randomPriceIndex][0]);
                dto.setMaxPrice(propertyPrices[randomPriceIndex][1]);
                Random random = new Random();
                int rndInt = Math.abs(random.nextInt()%50) + 1;
                dto.setImage("http://" + env.getProperty("challenge.domain") + "/property/image/img" + rndInt + ".jpeg");
                createDocument(dto);

            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDocument(PropertyDto property) {
        try {
            RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(
                                    env.getProperty("challenge.elastic.domain"),
                                    Integer.parseInt(env.getProperty("challenge.elastic.port")),
                                    env.getProperty("challenge.elastic.scheme"))));

            IndexRequest request = new IndexRequest(
                    "properties",
                    "prop");

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(property);
            request.source(json, XContentType.JSON);
            IndexResponse indexResponse = client.index(request);
            LOGGER.info("Document Indexed: " + indexResponse.toString());
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createIndex() {
        try {
            RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(
                                    env.getProperty("challenge.elastic.domain"),
                                    Integer.parseInt(env.getProperty("challenge.elastic.port")),
                                    env.getProperty("challenge.elastic.scheme"))));

            CreateIndexRequest request = new CreateIndexRequest("properties");
            request.settings(Settings.builder()
                    .put("index.number_of_shards", 3)
                    .put("index.number_of_replicas", 2)
            );
            request.mapping("test_index",
                    "{" +
                            "    \"prop\": {" +
                            "      \"properties\": {" +
                            "        \"id\": {" +
                            "            \"type\": \"long\"" +
                            "        }," +
                            "        \"propertyName\": {" +
                            "          \"type\": \"text\"" +
                            "        }," +
                            "        \"description\": {" +
                            "          \"type\": \"text\"" +
                            "        }," +
                            "        \"location\": {" +
                            "          \"type\": \"geo_point\"" +
                            "        }," +
                            "        \"city\": {" +
                            "          \"type\": \"text\"" +
                            "        }," +
                            "        \"beds\": {" +
                            "          \"type\": \"integer\"" +
                            "        }," +
                            "        \"baths\": {" +
                            "          \"type\": \"integer\"" +
                            "        }," +
                            "        \"kitchens\": {" +
                            "          \"type\": \"integer\"" +
                            "        }," +
                            "        \"petsAllowed\": {" +
                            "          \"type\": \"boolean\"" +
                            "        }," +
                            "        \"propertyType\": {" +
                            "          \"type\": \"text\"" +
                            "        }," +
                            "        \"minPrice\": {" +
                            "          \"type\": \"integer\"" +
                            "        }," +
                            "        \"maxPrice\": {" +
                            "          \"type\": \"integer\"" +
                            "        }," +
                            "        \"image\": {" +
                            "          \"type\": \"text\"" +
                            "        }" +
                            "      }" +
                            "    }" +
                            "  }",
                    XContentType.JSON);
            request.alias(
                    new Alias("test_index_alias")
            );
            request.alias(new Alias("twitter_alias"));
            request.timeout(TimeValue.timeValueMinutes(2));
            request.timeout("2m");
            request.masterNodeTimeout(TimeValue.timeValueMinutes(1));
            request.masterNodeTimeout("1m");
            request.waitForActiveShards(2);
            request.waitForActiveShards(ActiveShardCount.DEFAULT);
            CreateIndexResponse createIndexResponse = client.indices().create(request);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
