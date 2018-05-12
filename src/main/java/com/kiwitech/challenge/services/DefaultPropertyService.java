package com.kiwitech.challenge.services;

import com.kiwitech.challenge.persistence.entities.Property;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultPropertyService implements PropertyService {

    @Override
    public List<Property> getProperties() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));
        try {

            client.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
