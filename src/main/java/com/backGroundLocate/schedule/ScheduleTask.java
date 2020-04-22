package com.backGroundLocate.schedule;

import com.backGroundLocate.util.LocationUtil;
import jdk.nashorn.internal.scripts.JD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Baiyutong
 * @date 2020/4/20
 */
@Configuration
@EnableScheduling
public class ScheduleTask {

    @Autowired
    private LocationUtil locationUtil;

    @Autowired
    @Qualifier("mainJdbcTemplate")
    private JdbcTemplate mainJdbcTemplate;

    @Autowired
    @Qualifier("exLiveJdbcTemplate")
    private JdbcTemplate exLiveJdbcTemplate;

    @Scheduled(cron = "* * * 0/30 * ?")
    private void createMonth() throws Exception {
        System.out.println("执行定位信息分表创建时间: " + LocalDateTime.now());
        ResultSet userRs = null;
        ResultSet vehicleRs = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
            Calendar ct=Calendar.getInstance();
            ct.setTime(new Date());
            ct.add(Calendar.MONTH, +1);
            String tableDate = sdf.format(ct.getTime());
            String userTableName = "t_bns_user_locate_"+tableDate;
            String vehicleTableName = "t_bns_vehicle_locate_"+tableDate;

            DatabaseMetaData dbMetaData = mainJdbcTemplate.getDataSource().getConnection().getMetaData();
            String[] types = {"TABLE"};
            userRs = dbMetaData.getTables(null,null,userTableName,types);
            if(!userRs.next()){
                System.out.println(userTableName+"未创建");
                String createUserTableSql = "CREATE TABLE [dbo].["+userTableName+"] (\n" +
                        "  [id] int  IDENTITY(0,1) NOT NULL,\n" +
                        "  [user_id] int  NULL,\n" +
                        "  [address] varchar(255) COLLATE Chinese_PRC_CI_AS  NULL,\n" +
                        "  [longitude] varchar(255) COLLATE Chinese_PRC_CI_AS  NULL,\n" +
                        "  [latitude] varchar(255) COLLATE Chinese_PRC_CI_AS  NULL,\n" +
                        "  [lon_lat] varchar(255) COLLATE Chinese_PRC_CI_AS  NULL,\n" +
                        "  [upload_time] varchar(255) COLLATE Chinese_PRC_CI_AS  NULL,\n" +
                        "  [timestamp] int  NULL,\n" +
                        "  PRIMARY KEY CLUSTERED ([id])\n" +
                        "WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)\n" +
                        ")";
                mainJdbcTemplate.update(createUserTableSql);
                System.out.println(vehicleTableName+"创建成功");
            }else{
                System.out.println(userTableName+"已创建");
            }


            vehicleRs = dbMetaData.getTables(null,null,vehicleTableName,types);
            if(!vehicleRs.next()){
                System.out.println(vehicleTableName+"未创建");
                String createVehicleTableSql = "CREATE TABLE [dbo].["+vehicleTableName+"] (\n" +
                        "  [id] int  IDENTITY(0,1) NOT NULL,\n" +
                        "  [vehicle_id] int  NULL,\n" +
                        "  [vehicle_name] varchar(255) COLLATE Chinese_PRC_CI_AS  NULL,\n" +
                        "  [lng] varchar(255) COLLATE Chinese_PRC_CI_AS  NULL,\n" +
                        "  [lat] varchar(255) COLLATE Chinese_PRC_CI_AS  NULL,\n" +
                        "  [lng_lat] varchar(255) COLLATE Chinese_PRC_CI_AS  NULL,\n" +
                        "  [direct] int  NULL,\n" +
                        "  [speed] float(53)  NULL,\n" +
                        "  [state] int  NULL,\n" +
                        "  [address] varchar(255) COLLATE Chinese_PRC_CI_AS  NULL,\n" +
                        "  [mileage] float(53)  NULL,\n" +
                        "  [data_day] varchar(1) COLLATE Chinese_PRC_CI_AS  NULL,\n" +
                        "  [recv_time] datetime  NULL,\n" +
                        "  [timestamp] bigint  NULL,\n" +
                        "  PRIMARY KEY CLUSTERED ([id])\n" +
                        "WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)\n" +
                        ")";
                mainJdbcTemplate.update(createVehicleTableSql);
                System.out.println(vehicleTableName+"创建成功");
            }else{
                System.out.println(vehicleTableName+"已创建");
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally{
            userRs.close();
            vehicleRs.close();
        }

    }

}
