package org.netcracker.project.util;

import org.netcracker.project.model.embeddable.Statistics;
import org.netcracker.project.model.interfaces.Statistical;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.Set;

@Component
public class StatisticsUtil {

    /**
     * Метод, который используется для подсчета статистики объекта, который
     * реализует интерфейс Statistical и кладет в Model результаты подсчета
     * @param statistical - Объект, реализующий интерфейс Statistical, чья статистика будет подсчитываться
     * @param model - Объект Model, в который будут положены переменные, необходимые для генерации страницы
     */
    public void putStatisticsInModel(Statistical statistical, Model model) {
        int winCount = 0;
        int secondCount = 0;
        int thirdCount = 0;
        int participate = 0;
        int spottedBySponsors = 0;
        Set<Statistics> statistics = statistical.getStatistics();
        for(Statistics s : statistics){
            switch(s.getResult()){
                case WIN: winCount++; break;
                case SECOND: secondCount++; break;
                case THIRD: thirdCount++; break;
                case PARTICIPATE: participate++; break;
                case SPOTTED: spottedBySponsors++; break;
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
