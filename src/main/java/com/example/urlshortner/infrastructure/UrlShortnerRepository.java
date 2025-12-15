package com.example.urlshortner.infrastructure;

import com.example.urlshortner.domain.UrlMapping;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@DynamoDbBean
class UrlMappingEntity {
    private String alias;
    private String userId;
    private String originalUrl;
    private Boolean enable;
    private Long createdAt;
    private Long updatedAt;

    @DynamoDbPartitionKey
    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }

    public Boolean getEnable() { return enable; }
    public void setEnable(Boolean enable) { this.enable = enable; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }

    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
}

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

    public UrlMapping findByAlias(String alias) {
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
