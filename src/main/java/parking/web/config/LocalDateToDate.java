package parking.web.config;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class LocalDateToDate implements Converter<LocalDate, Date> {

    @Override
    public Date convert(LocalDate source) {
        return Date.from(source.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
