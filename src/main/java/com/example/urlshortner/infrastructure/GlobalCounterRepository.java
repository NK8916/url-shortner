package com.example.urlshortner.infrastructure;

import com.example.urlshortner.domain.GlobalCounter;
import jakarta.enterprise.context.ApplicationScoped;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Map;

@ApplicationScoped
public class GlobalCounterRepository implements GlobalCounter{
    private static final String TABLE_NAME = "global_counter";
    private static final String COUNTER_KEY = "url_id_counter";

    private final DynamoDbClient dynamoDb;

    public GlobalCounterRepository(DynamoDbClient dynamoDb) {
        this.dynamoDb = dynamoDb;
    }

    @Override
    public long nextId() {
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

        UpdateItemResponse response = dynamoDb.updateItem(request);
        String valueStr = response.attributes().get("value").n();
        return Long.parseLong(valueStr);
    }

}
