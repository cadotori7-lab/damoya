package com.soldesk.mapper;

import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.soldesk.vo.CalendarEvent;

public interface ScheduleMapper {
    List<CalendarEvent> selectMonthEvents(@Param("projectId") long projectId,
                                          @Param("start") Timestamp start,
                                          @Param("end")   Timestamp end);
}
