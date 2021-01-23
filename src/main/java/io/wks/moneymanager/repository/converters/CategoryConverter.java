package io.wks.moneymanager.repository.converters;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import io.wks.moneymanager.Category;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.math.BigDecimal;

public class CategoryConverter implements DynamoDBTypeConverter<String, Category> {

    @Override
    public String convert(Category category) {
        return category.toString();
    }

    @Override
    public Category unconvert(String s) {
        return Category.valueOf(s);
    }
}