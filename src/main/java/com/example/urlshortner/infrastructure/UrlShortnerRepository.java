package com.example.urlshortner.infrastructure;

import com.example.urlshortner.domain.UrlMapping;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@ApplicationScoped
public class UrlShortnerRepository {
    private static final String TABLE_NAME = "url_shortner";

    private final DynamoDbEnhancedClient enhancedClient;
    private DynamoDbTable<UrlMappingEntity> table;

    public UrlShortnerRepository(DynamoDbClient dynamoDb) {
        this.enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDb)
                .build();
    }

    @PostConstruct
    void init() {
        this.table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(UrlMappingEntity.class));
    }

    public void save(UrlMapping mapping) {
        UrlMappingEntity entity = new UrlMappingEntity();
        entity.setAlias(mapping.getAlias());
        entity.setUserId(mapping.getUserId());
        entity.setOriginalUrl(mapping.getOriginalUrl());
        entity.setEnable(mapping.isEnable());
        entity.setCreatedAt(mapping.getCreatedAt());
        entity.setUpdatedAt(mapping.getUpdatedAt());
        table.putItem(entity);
    }

    public UrlMapping getByAlias(String alias) {
        UrlMappingEntity entity = table.getItem(r -> r.key(k -> k.partitionValue(alias)));
        if (entity == null) return null;
        UrlMapping m = new UrlMapping();
        m.setAlias(entity.getAlias());
        m.setUserId(entity.getUserId());
        m.setOriginalUrl(entity.getOriginalUrl());
        m.setEnable(Boolean.TRUE.equals(entity.getEnable()));
        m.setCreatedAt(entity.getCreatedAt() == null ? 0L : entity.getCreatedAt());
        m.setUpdatedAt(entity.getUpdatedAt() == null ? 0L : entity.getUpdatedAt());
        return m;
    }
}
