package org.netcracker.project.filter;

import java.time.LocalDateTime;

public class CompetitionFilter {
    boolean enableBefore;
    boolean enableAfter;
//    boolean imIn; // Добавим этот фильтр потом, когда будет понятно, как определяется состав участников (когда эта часть будет готова)
//    boolean imNotIn; // Пока будем определять только по дате и по названию с описанием
    LocalDateTime before;
    LocalDateTime after;
    String string;
}
