package com.vz.backend.business.dto;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vz.backend.business.domain.AttachmentCalendar;
import com.vz.backend.business.domain.Calendar2;
import com.vz.backend.core.common.BussinessCommon;
import com.vz.backend.util.DateTimeUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CalendarWrapperDto {
	private List<CalendarDto> objList ;
	private Date frDate;
	private Date toDate;
	private int week;
	private AttachmentCalendar attCalWeek;
	
	@JsonIgnore
	private Map<Integer, List<Calendar2>> calendarListByDate = setCalendarDefault();
	
	@JsonIgnore
	private List<CalendarDto> calendarListByAmPm ;
	
	private static Map<Integer, String> dateOfWeek = DateTimeUtils.dayOfWeekMap;
	
	private Map<Integer, List<Calendar2>> setCalendarDefault() {
		calendarListByDate = new LinkedHashMap<>();
		dateOfWeek.forEach((k,v) -> {
			calendarListByDate.put(k, new ArrayList<>());
		});
		return calendarListByDate;
	}
	
	public CalendarWrapperDto(List<Calendar2> all, Date frDate, Date toDate, int week, int year, AttachmentCalendar attCalWeek) {
		this.frDate = frDate;
		this.toDate = toDate;
		this.week = week;
		this.objList = setCalendarListByAmPm(setCalendarListByDate(all), week, year);
		this.attCalWeek = attCalWeek;
	}

	private List<CalendarDto> setCalendarListByAmPm(Map<Integer, List<Calendar2>> mapByDate, int week, int year) {
		calendarListByAmPm = new ArrayList<>();
		mapByDate.forEach((k, v) -> {
			List<Calendar2DetailDto> amList = new ArrayList<>();
			List<Calendar2DetailDto> pmList = new ArrayList<>();
			v.forEach(i -> {
				Calendar2DetailDto dto = new Calendar2DetailDto();
				dto.set(i);
				if (DateTimeUtils.isAmPM(i.getStartTime())) {
					amList.add(dto);
				} else {
					pmList.add(dto);
				}
			});
			calendarListByAmPm.add(new CalendarDto(dateOfWeek.get(k), DateTimeUtils.getDateByDateThAndWeek(k, week, year), amList, pmList));
		});
		return calendarListByAmPm;
	}
	
	private Map<Integer, List<Calendar2>> setCalendarListByDate(List<Calendar2> all) {
		if(BussinessCommon.isEmptyList(all)) return calendarListByDate;
		for (Calendar2 i : all) {
			int key = DateTimeUtils.getDayOfWeek(i.getStartTime());
			if (!calendarListByDate.containsKey(key)) {
				calendarListByDate.put(key, new ArrayList<>());
			}
			calendarListByDate.get(key).add(i);
		}
		return calendarListByDate;
	}
}
