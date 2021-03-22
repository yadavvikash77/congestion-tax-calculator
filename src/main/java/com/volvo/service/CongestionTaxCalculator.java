package com.volvo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.volvo.constants.Constants;
import com.volvo.helper.AllowedVehicles;
import com.volvo.helper.HolidayCalendar;
import com.volvo.helper.TimeSlot;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.*;
import java.util.concurrent.TimeUnit;


public class CongestionTaxCalculator {

    public int getTax(String[] dates, String vehicle)
    {
        int totalFee = 0;
        int nextFee = 0;
        Date date = null;
        try {
            for (int i = 0; i < dates.length ; i++) {
                Date intervalStart = new SimpleDateFormat(Constants.DATE_FORMAT).parse(dates[i]);
                int tempFee = getTollFee(dates[0],vehicle);
                if((i+1) >= dates.length){
                    date = new SimpleDateFormat(Constants.DATE_FORMAT).parse(dates[dates.length-1]);
                    nextFee = getTollFee(dates[dates.length-1],vehicle);
                }else{
                    date = new SimpleDateFormat(Constants.DATE_FORMAT).parse(dates[i+1]);
                    nextFee = getTollFee(dates[i+1],vehicle);
                }
                long diffInMillies = date.getTime() - intervalStart.getTime();
                long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillies);
                if (minutes <= Constants.SIXTY){
                    if (totalFee > 0) totalFee -= tempFee;
                    if (nextFee >= tempFee) tempFee = nextFee;
                    totalFee += tempFee;
                }else{
                    totalFee += nextFee;
                }
            }
            if (totalFee > Constants.SIXTY){
                totalFee = Constants.SIXTY;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return totalFee;
    }


    public int getTollFee(String date, String vehicle){
        int feeAmount = 0;
        File file = new File(Constants.TIME_SLOT_JSON_PATH);
        ObjectMapper objectMapper = new ObjectMapper();
        List<TimeSlot> freeSlot = new ArrayList<>();
        List<TimeSlot> timeSlotList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.TIME_FORMAT);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(Constants.DATE_FORMAT);
        try {
            calendar.setTime(simpleDateFormat1.parse(date));
            if( isAllowedVehicle(vehicle) || isHoliday(calendar)){
                feeAmount = 0;
            }else {
                String[] time = date.split(Constants.SPACE);
                Date inputDate = simpleDateFormat.parse(time[1].trim());
                JsonNode jsonNode = objectMapper.readTree(file);
                if (jsonNode != null && jsonNode.has(Constants.GUTHENBURG)) {
                    JsonNode cityNode = jsonNode.get(Constants.GUTHENBURG);
                    if (cityNode != null && cityNode.has(Constants.FREESLOT)) {
                        freeSlot = objectMapper.readValue(cityNode.get(Constants.FREESLOT).toString(), new TypeReference<List<TimeSlot>>() {});
                    }
                    if (cityNode != null && cityNode.has(Constants.TIMESLOT)) {
                        timeSlotList = objectMapper.readValue(cityNode.get(Constants.TIMESLOT).toString(), new TypeReference<List<TimeSlot>>() {
                        });
                    }
                }
                if(freeSlot.size() > 0) {
                    for(TimeSlot freeTimeSlot : freeSlot) {
                        boolean freeStartTime = inputDate.equals(simpleDateFormat.parse(freeTimeSlot.getStartTime())) || inputDate.after(simpleDateFormat.parse(freeTimeSlot.getStartTime()));
                        boolean freeEndTime = inputDate.equals(simpleDateFormat.parse(freeTimeSlot.getEndTime())) || inputDate.before(simpleDateFormat.parse(freeTimeSlot.getEndTime()));
                        if (freeStartTime && freeEndTime) {
                            feeAmount = Integer.parseInt(freeTimeSlot.getTaxAmount());
                        }
                    }
                }
                if(timeSlotList.size() > 0){
                    for (TimeSlot timeSlot : timeSlotList) {
                        boolean startTime = inputDate.equals(simpleDateFormat.parse(timeSlot.getStartTime())) || inputDate.after(simpleDateFormat.parse(timeSlot.getStartTime()));
                        boolean endTime = inputDate.equals(simpleDateFormat.parse(timeSlot.getEndTime())) || inputDate.before(simpleDateFormat.parse(timeSlot.getEndTime()));
                        if (startTime && endTime) {
                            feeAmount = Integer.parseInt(timeSlot.getTaxAmount());
                        }
                    }
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return feeAmount;
    }

    public boolean isHoliday(Calendar calendar){
        File fileName = new File(Constants.HOLIDAY_CALENDAR_JSON_PATH);
        boolean isHoliday = false;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,true);
            HolidayCalendar holidayCalendar = objectMapper.readValue(fileName, HolidayCalendar.class);
            if(holidayCalendar != null && calendar != null && calendar.get(Calendar.YEAR) == Constants.YEAR){
                switch (calendar.get(Calendar.MONTH)){
                    case 0:
                        if(holidayCalendar.getJanuary() != null && holidayCalendar.getJanuary().length > 0 && Arrays.asList(holidayCalendar.getJanuary()).contains(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)))){
                            isHoliday = true;
                        }
                        break;
                    case 1:
                        if(holidayCalendar.getFebruary() != null && holidayCalendar.getFebruary().length > 0 &&Arrays.asList(holidayCalendar.getFebruary()).contains(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)))){
                            isHoliday = true;
                        }
                        break;
                    case 2:
                        if(holidayCalendar.getMarch() != null && holidayCalendar.getMarch().length > 0 && Arrays.asList(holidayCalendar.getMarch()).contains(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)))){
                            isHoliday = true;
                        }
                        break;
                    case 3:
                        if(holidayCalendar.getApril() != null && holidayCalendar.getApril().length > 0 && Arrays.asList(holidayCalendar.getApril()).contains(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)))){
                            isHoliday = true;
                        }
                        break;
                    case 4:
                        if(holidayCalendar.getMay() != null && holidayCalendar.getMay().length > 0 && Arrays.asList(holidayCalendar.getMay()).contains(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)))){
                            isHoliday = true;
                        }
                        break;
                    case 5:
                        if(holidayCalendar.getJune() != null && holidayCalendar.getJune().length > 0 && Arrays.asList(holidayCalendar.getJune()).contains(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)))){
                            isHoliday = true;
                        }
                        break;
                    case 6:
                            isHoliday = true;
                        break;
                    case 7:
                        if(holidayCalendar.getAugust() != null && holidayCalendar.getAugust().length > 0 && Arrays.asList(holidayCalendar.getAugust()).contains(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)))){
                            isHoliday = true;
                        }
                        break;
                    case 8:
                        if(holidayCalendar.getSeptember() != null && holidayCalendar.getSeptember().length > 0 && Arrays.asList(holidayCalendar.getSeptember()).contains(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)))){
                            isHoliday = true;
                        }
                        break;
                    case 9:
                        if(holidayCalendar.getOctober() != null && holidayCalendar.getOctober().length > 0 && Arrays.asList(holidayCalendar.getOctober()).contains(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)))){
                            isHoliday = true;
                        }
                        break;
                    case 10:
                        if(holidayCalendar.getNovember() != null && holidayCalendar.getNovember().length > 0 && Arrays.asList(holidayCalendar.getNovember()).contains(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)))){
                            isHoliday = true;
                        }
                        break;
                    case 11:
                        if(holidayCalendar.getDecember() != null && holidayCalendar.getDecember().length > 0 && Arrays.asList(holidayCalendar.getDecember()).contains(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)))){
                            isHoliday = true;
                        }
                        break;
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        return isHoliday;
    }


    public boolean isAllowedVehicle(String vehicle){
        List<String> allowedVehiclesAsList = new ArrayList<>();
        File fileName = new File(Constants.ALLOWED_VEHICLES_JSON_PATH);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
                AllowedVehicles allowedVehicles = objectMapper.readValue(fileName, AllowedVehicles.class);
                if(allowedVehicles != null) {
                    allowedVehiclesAsList = Arrays.asList(allowedVehicles.getAllowedVehicles());
                }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return allowedVehiclesAsList.contains(vehicle);
    }
}
