package io.wks.moneymanager.repository.converters;

import io.wks.moneymanager.Category;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.math.BigDecimal;

@WritingConverter
public enum CategoryToStringConverter implements Converter<Category, String> {
    INSTANCE;

    @Override
    public String convert(Category source) {
        return source.toString();
    }
}