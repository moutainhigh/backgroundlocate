package com.backGroundLocate.controller;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

public class TestClass {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        String s = "1581236440"; //2020-02-09
        String e = "1585840162"; //2020-04-02

        Date sdate = new Date(Long.valueOf(s)*1000);
        Date edate = new Date(Long.valueOf(e)*1000);

        LocalDate startDate = sdate.toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDate();
        System.out.println("startDate : " + startDate);
        LocalDate endDate = edate.toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDate();
        System.out.println("endDate : " + endDate);

        Period p = Period.between(startDate, endDate);
        System.out.printf("年龄 : %d 年 %d 个月 %d 天", p.getYears(), p.getMonths(), p.getDays());


        String[] yearsArray = new String[p.getYears()];
        String[] monthsArray = new String[p.getMonths()];
        String[] daysArray = new String[p.getDays()];
        if(yearsArray.length>0){

        }else if(monthsArray.length>0){
            LocalDate kssj = endDate.with(TemporalAdjusters.firstDayOfMonth());
            System.out.println(kssj.toString());
            for(int i=0;i<monthsArray.length;i++){

            }
        }else if(daysArray.length>0){
            LocalDate onTheDay = endDate.plusDays(-1);
        }else{

        }

    }
}
