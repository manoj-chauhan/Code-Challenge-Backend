package com.kiwitech.challenge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwitech.challenge.web.dtos.PropertyDto;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.ActiveShardCount;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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


    public void searchProperty(double latitude, double longitude, int distance) {
        try {
            RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(
                                    env.getProperty("challenge.elastic.domain"),
                                    Integer.parseInt(env.getProperty("challenge.elastic.port")),
                                    env.getProperty("challenge.elastic.scheme"))));

            GeoDistanceQueryBuilder queryBuilder = QueryBuilders.geoDistanceQuery("location")
                    .point(latitude, longitude)
                    .distance(10, DistanceUnit.KILOMETERS);

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(queryBuilder);

            SearchRequest searchRequest = new SearchRequest("properties");
            searchRequest.types("prop");
            searchRequest.source(searchSourceBuilder);

            SearchResponse response = client.search(searchRequest);
            SearchHits data = response.getHits();


            List<PropertyDto> properties = new ArrayList<>();
            for (SearchHit d: data.getHits()) {
                    String json = d.getSourceAsString();
                    ObjectMapper mapper = new ObjectMapper();
                    PropertyDto p = mapper.readValue(json, PropertyDto.class);
                    properties.add(p);
            }

            for (PropertyDto p: properties) {
                LOGGER.info("Source Data: " + p.getCity());
            }

            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createIndex() {
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
            request.mapping("prop",
                    "{\n" +
                            "    \"prop\": {\n" +
                            "      \"properties\": {\n" +
                            "        \"id\": {\n" +
                            "            \"type\": \"long\"\n" +
                            "        },\n" +
                            "        \"propertyName\": {\n" +
                            "          \"type\": \"text\"\n" +
                            "        },\n" +
                            "        \"description\": {\n" +
                            "          \"type\": \"text\"\n" +
                            "        },\n" +
                            "        \"location\": {\n" +
                            "          \"type\": \"geo_point\"\n" +
                            "        },\n" +
                            "        \"city\": {\n" +
                            "          \"type\": \"text\"\n" +
                            "        },\n" +
                            "        \"beds\": {\n" +
                            "          \"type\": \"integer\"\n" +
                            "        },\n" +
                            "        \"baths\": {\n" +
                            "          \"type\": \"integer\"\n" +
                            "        },\n" +
                            "        \"kitchens\": {\n" +
                            "          \"type\": \"integer\"\n" +
                            "        },\n" +
                            "        \"petsAllowed\": {\n" +
                            "          \"type\": \"boolean\"\n" +
                            "        },\n" +
                            "        \"propertyType\": {\n" +
                            "          \"type\": \"text\"\n" +
                            "        },\n" +
                            "        \"minPrice\": {\n" +
                            "          \"type\": \"integer\"\n" +
                            "        },\n" +
                            "        \"maxPrice\": {\n" +
                            "          \"type\": \"integer\"\n" +
                            "        },\n" +
                            "        \"image\": {\n" +
                            "          \"type\": \"text\"\n" +
                            "        }\n" +
                            "      }\n" +
                            "    }\n" +
                            "  }",
                    XContentType.JSON);
            request.alias(new Alias("properties_alias"));
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
