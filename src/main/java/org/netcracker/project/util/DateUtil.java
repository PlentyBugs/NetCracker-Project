package org.netcracker.project.util;

import org.netcracker.project.util.callback.DateCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateUtil {

    private final String dateFormat;
    private final String DATE_PATTERN;
    private final DateTimeFormatter formatter;
    private final DateTimeFormatter formDateFormatter;

    public DateUtil(@Value("${date.format}") String dateFormat) {
        this.dateFormat = dateFormat;
        DATE_PATTERN = "2[0-9][2-9][0-9]-(1[0-2]|0[0-9])-([0-2][0-9]|3[0-1])(\\s|T)(2[0-4]|[01][0-9]):([0-5][0-9]|60)";
        formatter = DateTimeFormatter.ofPattern(dateFormat);
        formDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");;
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }

    public DateTimeFormatter getFormDateFormatter() {
        return formDateFormatter;
    }

    public String getDATE_PATTERN() {
        return DATE_PATTERN;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public LocalDateTime compileFilter(String filter, String command) {
        return LocalDateTime.parse(
                LocalDateTime.parse(
                        filter.replaceFirst(command, "").replaceFirst("T", " "),
                        getFormDateFormatter()
                ).format(getFormatter()),
                getFormatter()
        );
    }

    public DateCallback parseDateFromForm(String formDate) {
        if ((formDate = formDate.replaceFirst("T", " ")).matches(getDATE_PATTERN())) {
            LocalDateTime parsed = LocalDateTime.parse(formDate, getFormDateFormatter());
            return new DateCallback(parsed, true);
        }
        return new DateCallback(null, false);
    }
}
