package io.wks.moneymanager;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import io.wks.moneymanager.repository.converters.LocalDateConverter;
import io.wks.moneymanager.repository.converters.UUIDConverter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public class TransactionId implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID uuid;
    private LocalDate date;

    @DynamoDBHashKey
    @DynamoDBTypeConverted(converter = UUIDConverter.class)
    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @DynamoDBRangeKey
    @DynamoDBTypeConverted(converter = LocalDateConverter.class)
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
