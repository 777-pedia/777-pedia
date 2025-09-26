package org.example.pedia_777.common.code;

import org.springframework.http.HttpStatus;

public interface Code {

    HttpStatus getHttpStatus();

    String getMessageTemplate();

    default String getMessage(String... args) {

        String template = getMessageTemplate();

        if (args == null || args.length == 0) {
            return template;
        }

        return String.format(template, (Object[]) args);
    }
}