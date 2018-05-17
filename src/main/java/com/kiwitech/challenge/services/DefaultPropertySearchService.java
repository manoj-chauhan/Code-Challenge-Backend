package com.kiwitech.challenge.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kiwitech.challenge.persistence.entities.Property;
import com.kiwitech.challenge.web.dtos.PropertyDto;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultPropertySearchService implements PropertySearchService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Environment env;

//    public static String ELASTICSEARCH_URL = "vpc-challenge-dvrvpwazki45l2vbrpfwydejvy.us-west-2.es.amazonaws.com";


    @Override
    public List<PropertyDto> getFeaturedProperties() {
        return searchDocuments();
    }





    private void getDocument() {
        try {
            RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(
                                    env.getProperty("challenge.elastic.domain"),
                                    Integer.parseInt(env.getProperty("challenge.elastic.port")),
                                    env.getProperty("challenge.elastic.scheme"))));

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
                            new HttpHost(
                                    env.getProperty("challenge.elastic.domain"),
                                    Integer.parseInt(env.getProperty("challenge.elastic.port")),
                                    env.getProperty("challenge.elastic.scheme"))));

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
                            new HttpHost(
                                    env.getProperty("challenge.elastic.domain"),
                                    Integer.parseInt(env.getProperty("challenge.elastic.port")),
                                    env.getProperty("challenge.elastic.scheme"))));

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

    private List<PropertyDto> searchDocuments() {
        try {
            List<PropertyDto> properties = new ArrayList<>();
            RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(
                                    env.getProperty("challenge.elastic.domain"),
                                    Integer.parseInt(env.getProperty("challenge.elastic.port")),
                                    env.getProperty("challenge.elastic.scheme"))));

            SearchRequest searchRequest = new SearchRequest("properties");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
            searchRequest.source(searchSourceBuilder);
            SearchResponse response = client.search(searchRequest);
            SearchHit[] hits = response.getHits().getHits();
            for (SearchHit h: hits) {
                String json = h.getSourceAsString();
                ObjectMapper mapper = new ObjectMapper();
                PropertyDto p = mapper.readValue(json, PropertyDto.class);
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
