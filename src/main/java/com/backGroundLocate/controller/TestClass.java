package com.backGroundLocate.controller;

import java.sql.*;

public class TestClass {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Connection conn = null;
        Statement stm = null;
        ResultSet rs = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection("jdbc:sqlserver://47.104.179.40:1433;DatabaseName=gserver_202003", "sa", "1qaz_2wsx");
            stm = conn.createStatement();
//            rs = stm.executeQuery(sql);
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


    }
}
