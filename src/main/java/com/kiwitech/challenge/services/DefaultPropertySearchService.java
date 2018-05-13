package com.kiwitech.challenge.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kiwitech.challenge.persistence.entities.Property;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.ActiveShardCount;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultPropertySearchService implements PropertySearchService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<Property> getFeaturedProperties() {
//        for (int i = 0; i < 10; i++) {
//            Property p = new Property();
//            p.setCity("New York");
//            p.setLocationLattitude(938493285);
//            p.setMinPrice("7");
//            p.setMaxPrice("17");
//            p.setBaths(2);
//            p.setBeds(10);
//            p.setLocationLattitude(3478975);
//            p.setDescription("Near River bank");
//            p.setKitchens(1);
//            p.setPropertyName("Studio");
//            p.setPropertyType("Owned");
//
//            createDocument(p);
//        }
        return searchDocuments();
    }


    private void createDocument(Property propety) {
        try {
            RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost("localhost", 9200, "http")));

            IndexRequest request = new IndexRequest(
                    "properties",
                    "doc");

            request.source(propety, XContentType.JSON);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(propety);
            request.source(json, XContentType.JSON);

            IndexResponse indexResponse = client.index(request);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createIndex() {
        try {
            RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost("localhost", 9200, "http"),
                            new HttpHost("localhost", 9201, "http")));

            CreateIndexRequest request = new CreateIndexRequest("properties");
            request.settings(Settings.builder()
                    .put("index.number_of_shards", 3)
                    .put("index.number_of_replicas", 2)
            );
//            request.mapping("property",
//                    createIndexJson(0),
//                    XContentType.JSON);
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


    private void getDocument() {
        try {
            RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost("localhost", 9200, "http"),
                            new HttpHost("localhost", 9201, "http")));

            GetRequest request = new GetRequest("properties", "doc", "1");
            GetResponse response = client.get(request);
            String data = response.getSourceAsString();
            LOGGER.info("Source Data: " + data);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void getMultipleDocument() {
        try {
            RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost("localhost", 9200, "http"),
                            new HttpHost("localhost", 9201, "http")));

            MultiGetRequest multiGetRequest = new MultiGetRequest();

            Header header = new BasicHeader("","");
            MultiGetResponse multiGetItemResponses = client.multiGet(multiGetRequest, header);

            for (MultiGetItemResponse itemResponse : multiGetItemResponses) {
                GetResponse response = itemResponse.getResponse();
                if (response.isExists()) {
                    String json = response.getSourceAsString();
                LOGGER.info("Source Data: " + json);
                }
            }
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getMultipleDocuments() {
        try {
            RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost("localhost", 9200, "http"),
                            new HttpHost("localhost", 9201, "http")));

            MultiGetRequest multiGetRequest = new MultiGetRequest();

            Header header = new BasicHeader("","");
            MultiGetResponse multiGetItemResponses = client.multiGet(multiGetRequest, header);

            for (MultiGetItemResponse itemResponse : multiGetItemResponses) {
                GetResponse response = itemResponse.getResponse();
                if (response.isExists()) {
                    String json = response.getSourceAsString();
                    LOGGER.info("Source Data: " + json);
                }
            }
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private List<Property> searchDocuments() {
        try {
            List<Property> properties = new ArrayList<>();
            RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost("localhost", 9200, "http"),
                            new HttpHost("localhost", 9201, "http")));

            SearchRequest searchRequest = new SearchRequest("properties");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
            searchRequest.source(searchSourceBuilder);
            SearchResponse response = client.search(searchRequest);
            SearchHit[] hits = response.getHits().getHits();
            for (SearchHit h: hits) {
                String json = h.getSourceAsString();
                ObjectMapper mapper = new ObjectMapper();
                Property p = mapper.readValue(json, Property.class);
                properties.add(p);
            }
            client.close();
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String createIndexJson(int i) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode property = mapper.createObjectNode();
        property.put("propertyName", "property" + i);
        property.put("price", "100" + i);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(property);
    }
}
