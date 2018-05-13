package com.kiwitech.challenge.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kiwitech.challenge.persistence.entities.Property;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
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
        getDocument();
        return null;
    }


    private void createDocument() {
        try {
            RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost("localhost", 9200, "http")));

            IndexRequest request = new IndexRequest(
                    "posts",
                    "doc",
                    "1");
            String jsonString = "{" +
                    "\"user\":\"kimchy\"," +
                    "\"postDate\":\"2013-01-30\"," +
                    "\"message\":\"trying out Elasticsearch\"" +
                    "}";
            request.source(jsonString, XContentType.JSON);
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

            GetRequest request = new GetRequest("posts", "doc", "1");
            GetResponse response = client.get(request);
            String data = response.getSourceAsString();
            LOGGER.info("Source Data: " + data);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void searchDocuments() {
        try {
            RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost("localhost", 9200, "http"),
                            new HttpHost("localhost", 9201, "http")));

            SearchRequest searchRequest = new SearchRequest("posts");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
            searchRequest.source(searchSourceBuilder);
            SearchResponse response = client.search(searchRequest);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createIndexJson(int i) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode property = mapper.createObjectNode();
        property.put("propertyName", "property" + i);
        property.put("price", "100" + i);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(property);
    }
}
