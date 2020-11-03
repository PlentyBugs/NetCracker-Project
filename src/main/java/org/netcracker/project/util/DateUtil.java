package org.netcracker.project.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Component
public class DateUtil {

    private final String dateFormat;
    private final String DATE_PATTERN_SAFE;
    private final DateTimeFormatter formatter;
    private final DateTimeFormatter formDateFormatter;

    public DateUtil(@Value("${date.format}") String dateFormat) {
        this.dateFormat = dateFormat;
        DATE_PATTERN_SAFE = Pattern.quote(dateFormat);
        formatter = DateTimeFormatter.ofPattern(dateFormat);
        formDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");;
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }

    public DateTimeFormatter getFormDateFormatter() {
        return formDateFormatter;
    }

    public String getDATE_PATTERN_SAFE() {
        return DATE_PATTERN_SAFE;
    }

    public String getDateFormat() {
        return dateFormat;
    }
}
