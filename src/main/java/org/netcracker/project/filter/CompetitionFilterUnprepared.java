package org.netcracker.project.filter;

import lombok.Data;
import org.netcracker.project.util.DateUtil;
import org.netcracker.project.util.callback.DateCallback;

@Data
public class CompetitionFilterUnprepared {
    Boolean enableBeforeStart;
    Boolean enableEqualsStart;
    Boolean enableAfterStart;
    Boolean enableBeforeEnd;
    Boolean enableEqualsEnd;
    Boolean enableAfterEnd;
    String beforeStart;
    String equalsStart;
    String afterStart;
    String beforeEnd;
    String equalsEnd;
    String afterEnd;
    String searchString;

    public CompetitionFilter prepare(DateUtil dateUtil) {
        DateCallback beforeStartCallback = dateUtil.parseDateFromForm(beforeStart == null ? "" : beforeStart);
        DateCallback equalsStartCallback = dateUtil.parseDateFromForm(equalsStart == null ? "" : equalsStart);
        DateCallback afterStartCallback = dateUtil.parseDateFromForm(afterStart == null ? "" : afterStart);
        DateCallback beforeEndCallback = dateUtil.parseDateFromForm(beforeEnd == null ? "" : beforeEnd);
        DateCallback equalsEndCallback = dateUtil.parseDateFromForm(equalsEnd == null ? "" : equalsEnd);
        DateCallback afterEndCallback = dateUtil.parseDateFromForm(afterEnd == null ? "" : afterEnd);
        if (searchString == null) searchString = "";

        enableBeforeStart = enableBeforeStart != null && enableBeforeStart;
        enableEqualsStart = enableEqualsStart != null && enableEqualsStart;
        enableAfterStart = enableAfterStart != null && enableAfterStart;
        enableBeforeEnd = enableBeforeEnd != null && enableBeforeEnd;
        enableEqualsEnd = enableEqualsEnd != null && enableEqualsEnd;
        enableAfterEnd = enableAfterEnd != null && enableAfterEnd;

        return new CompetitionFilter(
                enableBeforeStart, enableEqualsStart, enableAfterStart, enableBeforeEnd, enableEqualsEnd, enableAfterEnd,
                beforeStartCallback.getLocalDateTime(), equalsStartCallback.getLocalDateTime(), afterStartCallback.getLocalDateTime(),
                beforeEndCallback.getLocalDateTime(), equalsEndCallback.getLocalDateTime(), afterEndCallback.getLocalDateTime(),
                searchString
        );
    }
}
