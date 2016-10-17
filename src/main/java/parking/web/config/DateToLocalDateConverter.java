package parking.web.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
public class DateToLocalDateConverter implements Converter<Date, LocalDate> {

    @Override
    public LocalDate convert(Date source) {
        return source == null ? null : source.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}