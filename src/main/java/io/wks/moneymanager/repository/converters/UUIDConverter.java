package io.wks.moneymanager.repository.converters;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import io.wks.moneymanager.Category;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.math.BigDecimal;
import java.util.UUID;

public class UUIDConverter implements DynamoDBTypeConverter<String, UUID> {

    @Override
    public String convert(UUID uuid) {
        return uuid.toString();
    }

    @Override
    public UUID unconvert(String s) {
        return UUID.fromString(s);
    }
}