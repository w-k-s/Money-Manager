package io.wks.moneymanager.repository.converters;

import io.wks.moneymanager.Category;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.math.BigDecimal;

@ReadingConverter
public enum StringToCategoryConverter implements Converter<String, Category> {
    INSTANCE;

    @Override
    public Category convert(String source) {
        return Category.valueOf(source);
    }
}