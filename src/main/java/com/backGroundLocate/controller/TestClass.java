package com.backGroundLocate.controller;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TestClass {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        String s = "1585658191"; //2019-12-19
        String e = "1585658200"; //2020-03-31

        Date sdate = new Date(Long.valueOf(s)*1000);
        Date edate = new Date(Long.valueOf(e)*1000);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");


        List<String> dayList = getTimeList(sdf1.format(sdate),sdf1.format(edate));
        List<String> monthList = getTimeList(sdf.format(sdate),sdf.format(edate));
        for (String month :monthList){
            month = month.replace("-","");
            String dbName = "gserver_"+month;
//            System.out.println(dbName);
            String sqlStart = "select * from (";
            String sqlContent = "";
            for (String day :dayList){
                day = day.replace("-","");
                if(day.contains(month)){
                    String tbName = "gps_"+day;
//                    System.out.println(tbName);

                    sqlContent += "select cast((lng+lng3) as VARCHAR) +','+cast((lat+lat3) as VARCHAR) AS latlon,distance as mileage,DATEDIFF(second, '1970-01-01 08:00:00', recvtime) as timestamp from "+ tbName +" where VehicleID = 14971170 union all ";

                }
            }
            String sqlEnd = ") a order by a.timestamp asc";
            String sql = sqlStart+sqlContent.substring(0,sqlContent.length()-10)+sqlEnd;
            System.out.println(sql);
        }

    }

    public static List<String> getTimeList(String startDate, String endxDate){
        SimpleDateFormat sdf ;
        int calendarType;

        switch (startDate.length()){
            case 10:
                sdf = new SimpleDateFormat("yyyy-MM-dd");
                calendarType = Calendar.DATE;
                break;
            case 7:
                sdf = new SimpleDateFormat("yyyy-MM");
                calendarType = Calendar.MONTH;
                break;
            case 4:
                sdf = new SimpleDateFormat("yyyy");
                calendarType = Calendar.YEAR;
                break;
            default:
                return null;
        }

        List<String> result = new ArrayList<>();
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        try {
            min.setTime(sdf.parse(startDate));
            min.add(calendarType, 0);
            max.setTime(sdf.parse(endxDate));
            max.add(calendarType, 1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar curr = min;
        while (curr.before(max)) {
            result.add(sdf.format(min.getTime()));
            curr.add(calendarType, 1);
        }
        return result;
    }
}
