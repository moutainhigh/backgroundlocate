package com.backGroundLocate.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class JdbcUtil {

    public static ResultSet connectionDataBase(String sql,String driveName,String url ,String userName,String password){
        Connection conn = null;
        Statement stm = null;
        ResultSet rs = null;
        try {
            Class.forName(driveName);
            conn = DriverManager.getConnection(url, userName, password);
            stm = conn.createStatement();
            rs = stm.executeQuery(sql);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stm != null) {
                    stm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rs;
    }
}
