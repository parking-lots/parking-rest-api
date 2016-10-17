package parking.web.config;


import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class LocalTimeToString implements Converter<LocalDate, String> {

    @Override
    public String convert(LocalDate source) {
        return source.format(DateTimeFormatter.ISO_DATE);
    }
}
