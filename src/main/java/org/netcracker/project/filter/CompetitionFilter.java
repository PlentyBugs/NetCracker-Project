package org.netcracker.project.filter;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CompetitionFilter {
    boolean enableBeforeStart;
    boolean enableEqualsStart;
    boolean enableAfterStart;
    boolean enableBeforeEnd;
    boolean enableEqualsEnd;
    boolean enableAfterEnd;
    LocalDateTime beforeStart;
    LocalDateTime equalsStart;
    LocalDateTime afterStart;
    LocalDateTime beforeEnd;
    LocalDateTime equalsEnd;
    LocalDateTime afterEnd;
    String string;
//    boolean imIn; // Добавим этот фильтр потом, когда будет понятно, как определяется состав участников (когда эта часть будет готова)
//    boolean imNotIn; // Пока будем определять только по дате и по названию с описанием

    public boolean isBoundsOn() {
        return enableBeforeStart || enableAfterStart || enableBeforeEnd || enableAfterEnd;
    }

    public boolean isEqualsBoundsOn() {
        return enableEqualsStart || enableEqualsEnd;
    }

    public String getFormattedString() {
        return "%" + string + "%";
    }
}
