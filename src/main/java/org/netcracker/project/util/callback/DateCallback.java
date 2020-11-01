package org.netcracker.project.util.callback;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class DateCallback {
    LocalDateTime localDateTime;
    boolean success;

    public boolean isFailure() {
        return !isSuccess();
    }
}
