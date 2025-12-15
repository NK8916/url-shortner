package com.example.urlshortner.infrastructure;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class UrlMappingEntity {
    private String alias;
    private String userId;
    private String originalUrl;
    private Boolean enable;
    private Long createdAt;
    private Long updatedAt;

    public UrlMappingEntity() {
    }

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
