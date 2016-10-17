package parking.web.config;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;


import java.util.ArrayList;
import java.util.List;

@Configuration
public class Config {

    private final List<Converter<?, ?>> converters = new ArrayList<Converter<?, ?>>();

    @Bean
    public CustomConversions customConversions() {
        converters.add(new DateToLocalDateConverter());
        converters.add(new LocalTimeToString());
        converters.add(new StringToLocalTime());
        converters.add(new LocalDateToDate());

        return new CustomConversions(converters);
    }

    @Bean(name = "OBJECT_MAPPER_BEAN")
    public ObjectMapper jsonObjectMapper() {
        return Jackson2ObjectMapperBuilder.json()
                .serializationInclusion(JsonInclude.Include.NON_NULL) // Don’t include null values
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) //ISODate
                .modules(new JSR310Module())
                .build();
    }


}
