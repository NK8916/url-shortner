package com.example.urlshortner.infrastructure;

import com.example.urlshortner.domain.UrlMapping;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class UrlShortnerRepository {

    private static final String TABLE_NAME = "url_mapping";
    private final DynamoDbClient dynamoDb;

    @Inject
    public UrlShortnerRepository(DynamoDbClient dynamoDb) {
        this.dynamoDb = dynamoDb;
    }

    public void save(UrlMapping mapping) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("alias", AttributeValue.builder().s(mapping.getAlias()).build());
        item.put("userId", AttributeValue.builder().s(mapping.getUserId()).build());
        item.put("originalUrl", AttributeValue.builder().s(mapping.getOriginalUrl()).build());
        item.put("enable", AttributeValue.builder().bool(mapping.isEnable()).build());
        item.put("createdAt", AttributeValue.builder().n(Long.toString(mapping.getCreatedAt())).build());
        item.put("updatedAt", AttributeValue.builder().n(Long.toString(mapping.getUpdatedAt())).build());

        dynamoDb.putItem(PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build());
    }

    public UrlMapping getByAlias(String alias) {
        GetItemResponse resp = dynamoDb.getItem(GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("alias", AttributeValue.builder().s(alias).build()))
                .consistentRead(true)
                .build());

        if (!resp.hasItem() || resp.item().isEmpty()) return null;

        Map<String, AttributeValue> item = resp.item();
        UrlMapping m = new UrlMapping();
        m.setAlias(alias);
        m.setUserId(item.get("userId") != null ? item.get("userId").s() : null);
        m.setOriginalUrl(item.get("originalUrl") != null ? item.get("originalUrl").s() : null);
        m.setEnable(item.get("enable") != null && Boolean.TRUE.equals(item.get("enable").bool()));
        m.setCreatedAt(item.get("createdAt") != null ? Long.parseLong(item.get("createdAt").n()) : 0L);
        m.setUpdatedAt(item.get("updatedAt") != null ? Long.parseLong(item.get("updatedAt").n()) : 0L);
        return m;
    }
}
