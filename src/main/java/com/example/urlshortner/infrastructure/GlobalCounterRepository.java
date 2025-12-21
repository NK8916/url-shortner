package com.example.urlshortner.infrastructure;

import com.example.urlshortner.domain.GlobalCounter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Map;
import org.jboss.logging.Logger;

@ApplicationScoped
public class GlobalCounterRepository implements GlobalCounter{
    private static final Logger logger = Logger.getLogger(GlobalCounterRepository.class);
    private static final String TABLE_NAME = "global_counter";
    private static final String COUNTER_KEY = "url_id_counter";

    private final DynamoDbClient dynamoDb;

    @Inject
    public GlobalCounterRepository(DynamoDbClient dynamoDb) {
        this.dynamoDb = dynamoDb;
    }

    @Override
    public long nextId() {
        logger.infov("Incrementing global counter for next ID");
        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("name", AttributeValue.builder().s(COUNTER_KEY).build()))
                .updateExpression("SET #v = if_not_exists(#v, :zero) + :inc")
                .expressionAttributeNames(Map.of("#v", "value"))
                .expressionAttributeValues(Map.of(
                        ":zero", AttributeValue.builder().n("0").build(),
                        ":inc",  AttributeValue.builder().n("1").build()
                ))
                .returnValues(ReturnValue.UPDATED_NEW)
                .build();
        logger.infov("Sending UpdateItemRequest to DynamoDB: " + request);
        var cfg =this.dynamoDb.serviceClientConfiguration();
        logger.infov("Dynamo endpointOverride = " + cfg.endpointOverride().orElse(null) + ", region = " + cfg.region());
        UpdateItemResponse response = dynamoDb.updateItem(request);
        String valueStr = response.attributes().get("value").n();
        logger.infov("New global counter value: " + valueStr);
        return Long.parseLong(valueStr);
    }

}
