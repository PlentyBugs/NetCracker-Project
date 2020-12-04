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
        DATE_PATTERN = "2[0-9][0-9][0-9]-(1[0-2]|0[0-9])-([0-2][0-9]|3[0-1])(\\s|T)(2[0-4]|[01][0-9]):([0-5][0-9]|60)";
        formatter = DateTimeFormatter.ofPattern(dateFormat);
        formDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");;
    }

    /**
     * Метод который возвращает преобразователь даты, основанный на формате из date.format в application.properties
     * @return - преобразователь даты
     */
    public DateTimeFormatter getFormatter() {
        return formatter;
    }

    /**
     * Метод который возвращает преобразователь даты, для конвертирования даты, полученной от клиента из формы
     * @return - преобразователь даты из формы
     */
    public DateTimeFormatter getFormDateFormatter() {
        return formDateFormatter;
    }

    /**
     * Метод который возвращает строку содержащую регулярное выражение для проверки даты
     * @return - строка с регулярным выражением
     */
    public String getDATE_PATTERN() {
        return DATE_PATTERN;
    }

    /**
     * Метод, которвый возвращает строку содержащую формат даты из date.format в application.properties
     * @return - строка с форматом даты
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * Метод, который преобразует дату в формате строки, полученную от клиента из формы.
     * @param formDate - строка с датой, полученная от клиента из формы
     * @return - Коллбэк, который может иметь два значения: объект даты, как результат работы метода и булево значение. Дата либо объект, либо null, булево значение: true (если парсинг прошел удачно) или false (в ином раскладе)
     */
    public DateCallback parseDateFromForm(String formDate) {
        if ((formDate = formDate.replaceFirst("T", " ")).matches(getDATE_PATTERN())) {
            LocalDateTime parsed = LocalDateTime.parse(formDate, getFormDateFormatter());
            return new DateCallback(parsed, true);
        }
        return new DateCallback(null, false);
    }
}
