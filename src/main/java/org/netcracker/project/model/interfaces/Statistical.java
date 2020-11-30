package org.netcracker.project.model.interfaces;

import org.netcracker.project.model.Competition;
import org.netcracker.project.model.enums.Result;

import java.util.Map;

public interface Statistical {
    Map<Result, Competition> getStatistics();
}
