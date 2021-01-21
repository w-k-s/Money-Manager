package io.wks.moneymanager.config;

import io.wks.moneymanager.repository.converters.CategoryToStringConverter;
import io.wks.moneymanager.repository.converters.StringToCategoryConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.Arrays;

@Configuration
public class MongodbConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(
                Arrays.asList(
                        CategoryToStringConverter.INSTANCE,
                        StringToCategoryConverter.INSTANCE
                ));
    }
}
