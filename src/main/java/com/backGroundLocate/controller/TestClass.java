package com.backGroundLocate.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.backGroundLocate.util.MD5Util;
import com.backGroundLocate.util.RestTemplateUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.xml.transform.Result;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.Date;

@Api(tags = "测试接口")
@RestController
public class TestClass {
    @Autowired
    private RestTemplateUtil restTemplateUtil;

    private String exGpsApiUrl = "http://47.104.179.40:89/gpsonline/GPSAPI";

    private String exAccount = "ydhb";

    private String exPassword = "123456";

    private String uri = "http://47.104.179.40:89/gpsonline/GPSAPI";

    public static void main(String[] args) {
        //点在多边形内
                Point2D.Double point = new Point2D.Double(116.309098,40.070144);
                //点在多边形外
        //		Point2D.Double point = new Point2D.Double(116.404072, 39.916605);

        List<Point2D.Double> polygon = new ArrayList<Point2D.Double>();
        polygon.add(new Point2D.Double(116.301744, 40.070643));
        polygon.add(new Point2D.Double(116.31626,40.069676));
        polygon.add(new Point2D.Double(116.304367,40.062636));
        polygon.add(new Point2D.Double(116.311948,40.074728));
        polygon.add(new Point2D.Double(116.311948,40.074728));
//        polygon.add(new Point2D.Double(116.277266,40.074728));

        if(contains(polygon,point)){
            System.out.println("点在多边形内");
        }else{
            System.out.println("点在多边形外");
        }

    }

    /**
     * 判断点是否在多边形内
     * 步骤：
     * 		①声明一个“画笔”
     * 		②将“画笔”移动到多边形的第一个顶点
     * 		③用“画笔”按顺序将多边形的顶点连接起来
     * 		④用“画笔”将多边形的第一个点连起来，最终形成一个封闭的多边形
     * 		⑤用contains()方法判断点是否在多边形区域内
     * @param polygon	多边形
     * @param point		检测点
     * @return			点在多边形内返回true，否则返回false
     */
    public static boolean contains(List<Point2D.Double> polygon, Point2D.Double point){

        GeneralPath p = new GeneralPath();

        Point2D.Double first = polygon.get(0);
        p.moveTo(first.x, first.y);

        for(Point2D.Double d : polygon){
            p.lineTo(d.x, d.y);
        }

        p.lineTo(first.x, first.y);
        p.closePath();

        return p.contains(point);
    }


    @PostMapping(value = "/test")
    public void restCar(){
        MultiValueMap<String, Object> vehicleMap= new LinkedMultiValueMap<>();
        vehicleMap.add("version","1");
        vehicleMap.add("method","loadHistory");
        vehicleMap.add("vid","13986108");
        vehicleMap.add("vKey","447663dece4ed3ee3075e9ad687a5787");
        vehicleMap.add("bTime","1605068776");
        vehicleMap.add("eTime","1605068776");

        String locationStr = restTemplateUtil.PostFormData(exGpsApiUrl,vehicleMap);
        System.out.println(locationStr);
        JSONObject locationJson = JSONObject.parseObject(locationStr);
        System.out.println(locationJson.toJSONString());
    }
}
