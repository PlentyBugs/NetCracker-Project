package org.netcracker.project.util;

import org.netcracker.project.model.Competition;
import org.netcracker.project.model.enums.Result;
import org.netcracker.project.model.interfaces.Statistical;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.Map;

@Component
public class StatisticsUtil {

    public void putStatisticsInModel(Statistical statistical, Model model) {
        int winCount = 0;
        int secondCount = 0;
        int thirdCount = 0;
        int participate = 0;
        int spottedBySponsors = 0;
        Map<Result, Competition> statistics = statistical.getStatistics();
        for(Result result : statistics.keySet()){
            switch(result){
                case WIN: winCount++;break;
                case SECOND: secondCount++;break;
                case THIRD: thirdCount++;break;
                case PARTICIPATE: participate++;break;
                case SPOTTED: spottedBySponsors++;break;
            }
        }
        model.addAttribute("winCount", winCount);
        model.addAttribute("secondCount", secondCount);
        model.addAttribute("thirdCount", thirdCount);
        model.addAttribute("participate", participate);
        model.addAttribute("spotted", spottedBySponsors);
        model.addAttribute(statistical);
    }
}
