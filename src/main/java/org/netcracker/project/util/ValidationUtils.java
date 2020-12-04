package org.netcracker.project.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ValidationUtils {

    /**
     * Метод BindingResult с ошибками и преобразует их в Map,
     * где ключ - название поля с Error на конце, а значение - текст ошибки
     * @param bindingResult - объект с ошибками
     * @return - Map, где ключ - имя ошибки, значение - ее текст
     */
    public static Map<String, String> getErrors(BindingResult bindingResult) {
        Collector<FieldError, ?, Map<String, String>> collector = Collectors.toMap(
                fieldError -> fieldError.getField() + "Error",
                FieldError::getDefaultMessage
        );
        return bindingResult.getFieldErrors().stream().collect(collector);
    }
}
