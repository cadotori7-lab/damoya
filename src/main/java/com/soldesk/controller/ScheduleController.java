package com.soldesk.controller;

import com.soldesk.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/workspace/{project_id}/schedule")
public class ScheduleController{

    private static final DateTimeFormatter YM = DateTimeFormatter.ofPattern("yyyy-MM");

    @Autowired private CalendarService calendarService;

    @GetMapping
    public String schedule(@PathVariable("project_id") long projectId,
                           @RequestParam(value = "ym", required = false) String ymParam,
                           Model model) {

        YearMonth ym;
        try {
            ym = (ymParam == null || ymParam.isEmpty())
                    ? YearMonth.now()
                    : YearMonth.parse(ymParam, YM);
        } catch (Exception e) {
            ym = YearMonth.now();
        }

        model.addAttribute("weeks", calendarService.buildMonth(projectId, ym));
        model.addAttribute("year",  ym.getYear());
        model.addAttribute("month", ym.getMonthValue());

        // 이전/다음 달 링크용
        model.addAttribute("prevYm", ym.minusMonths(1).format(YM));
        model.addAttribute("nextYm", ym.plusMonths(1).format(YM));
        model.addAttribute("thisYm", YearMonth.now().format(YM));

        return "workspace/schedule";
    }
}
