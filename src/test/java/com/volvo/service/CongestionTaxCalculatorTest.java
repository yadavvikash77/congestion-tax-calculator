package com.volvo.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class CongestionTaxCalculatorTest {

    private CongestionTaxCalculator congestionTaxCalculator;
    @BeforeEach
    void setUp() {
        congestionTaxCalculator = new CongestionTaxCalculator();
    }

    @AfterEach
    void tearDown() {
        congestionTaxCalculator = null;
    }

    @Test
    void getTax() {
        String [] dates = {"2013-02-08 06:20:00","2013-02-08 06:27:27"};
        assertEquals(8,congestionTaxCalculator.getTax(dates,"Car"));
        assertEquals(0,congestionTaxCalculator.getTax(dates,"Busses"));
        String [] date2 = {"2013-02-08 15:47:00","2013-02-08 16:01:00","2013-02-08 16:48:00","2013-02-08 17:49:00"};
        assertEquals(31,congestionTaxCalculator.getTax(date2,"Car"));
        String [] date3 = {"2013-01-14 21:00:00","2013-01-15 21:00:00","2013-02-07 06:23:27","2013-02-07 15:27:00","2013-02-08 06:27:00"};
        assertEquals(37,congestionTaxCalculator.getTax(date3,"Car"));
    }

    @Test
    @DisplayName("Testing TollFee")
    void getTollFee() {
        assertEquals(8,congestionTaxCalculator.getTollFee("2013-02-08 06:20:27","Car"));
        assertEquals(0,congestionTaxCalculator.getTollFee("2013-02-08 06:20:27","Busses"));
        assertEquals(0,congestionTaxCalculator.getTollFee("2013-12-24 06:20:27","Car"));
        assertEquals(0,congestionTaxCalculator.getTollFee("2013-01-14 21:00:00", "Car"));
        assertEquals(0,congestionTaxCalculator.getTollFee("2013-03-28 14:07:00", "Car"));
        assertEquals(18,congestionTaxCalculator.getTollFee("2013-04-02 16:48:00", "Car"));
        assertEquals(13,congestionTaxCalculator.getTollFee("2013-05-02 17:48:00", "Car"));
        assertEquals(0,congestionTaxCalculator.getTollFee("2013-06-02 18:35:00", "Car"));
        assertEquals(0,congestionTaxCalculator.getTollFee("2013-07-02 15:29:00", "Car"));
        assertEquals(13,congestionTaxCalculator.getTollFee("2013-08-02 15:29:00", "Car"));
        assertEquals(0,congestionTaxCalculator.getTollFee("2013-09-02 18:30:00", "Car"));
        assertEquals(0,congestionTaxCalculator.getTollFee("2013-10-02 05:59:00", "Car"));
        assertEquals(8,congestionTaxCalculator.getTollFee("2013-11-02 18:29:00", "Car"));
        assertEquals(0,congestionTaxCalculator.getTollFee("2013-06-21 15:26:00", "Car"));
    }

    @Test
    @DisplayName("Testing Holiday")
    void isHoliday() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(simpleDateFormat.parse("2013-02-08 06:20:27"));
            assertFalse(congestionTaxCalculator.isHoliday(calendar));
            calendar.setTime(simpleDateFormat.parse("2013-08-02 15:29:00"));
            assertFalse(congestionTaxCalculator.isHoliday(calendar));
            calendar.setTime(simpleDateFormat.parse("2012-02-08 06:20:27"));
            assertFalse(congestionTaxCalculator.isHoliday(calendar));
            calendar.setTime(simpleDateFormat.parse("2014-02-08 06:20:27"));
            assertFalse(congestionTaxCalculator.isHoliday(calendar));
            calendar.setTime(simpleDateFormat.parse("2013-01-01 06:20:27"));
            assertTrue(congestionTaxCalculator.isHoliday(calendar));
            calendar.setTime(simpleDateFormat.parse("2013-02-01 06:20:27"));
            assertFalse(congestionTaxCalculator.isHoliday(calendar));
            calendar.setTime(simpleDateFormat.parse("2013-03-28 06:20:27"));
            assertTrue(congestionTaxCalculator.isHoliday(calendar));
            calendar.setTime(simpleDateFormat.parse("2013-04-01 06:20:27"));
            assertTrue(congestionTaxCalculator.isHoliday(calendar));
            calendar.setTime(simpleDateFormat.parse("2013-04-30 06:20:27"));
            assertTrue(congestionTaxCalculator.isHoliday(calendar));
            calendar.setTime(simpleDateFormat.parse("2013-03-27 06:20:27"));
            assertFalse(congestionTaxCalculator.isHoliday(calendar));
            calendar.setTime(simpleDateFormat.parse("2013-07-27 06:20:27"));
            assertTrue(congestionTaxCalculator.isHoliday(calendar));
            calendar.setTime(simpleDateFormat.parse("2013-10-27 06:20:27"));
            assertFalse(congestionTaxCalculator.isHoliday(calendar));
            calendar.setTime(simpleDateFormat.parse("2013-12-24 06:20:27"));
            assertTrue(congestionTaxCalculator.isHoliday(calendar));
            calendar.setTime(simpleDateFormat.parse("2013-12-25 06:20:27"));
            assertTrue(congestionTaxCalculator.isHoliday(calendar));
            calendar.setTime(simpleDateFormat.parse("2013-12-31 06:20:27"));
            assertTrue(congestionTaxCalculator.isHoliday(calendar));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Testing vehicle allowed or not?")
    void isAllowedVehicle() {
        assertAll(
                ()->{assertTrue(congestionTaxCalculator.isAllowedVehicle("Busses"));},
                ()->{assertFalse(congestionTaxCalculator.isAllowedVehicle(null));},
                ()->{assertFalse(congestionTaxCalculator.isAllowedVehicle(""));},
                ()->{assertFalse(congestionTaxCalculator.isAllowedVehicle("Car"));},
                ()->{assertTrue(congestionTaxCalculator.isAllowedVehicle("Emergency vehicles"));},
                ()->{assertTrue(congestionTaxCalculator.isAllowedVehicle("Busses"));},
                ()->{assertTrue(congestionTaxCalculator.isAllowedVehicle("Diplomat vehicles"));},
                ()->{assertTrue(congestionTaxCalculator.isAllowedVehicle("Motorcycles"));},
                ()->{assertTrue(congestionTaxCalculator.isAllowedVehicle("Military vehicles"));},
                ()->{assertTrue(congestionTaxCalculator.isAllowedVehicle("Foreign vehicles"));},
                ()->{assertFalse(congestionTaxCalculator.isAllowedVehicle("Motorbike"));}
        );
    }
}