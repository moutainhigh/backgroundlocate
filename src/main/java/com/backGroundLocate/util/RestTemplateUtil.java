package com.backGroundLocate.util;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Component
public class RestTemplateUtil {
    private RestTemplate restTemplate;
    /**
     * 发送GET请求
     * @param url
     * @param param
     * @return
     */
    public String GetData(String url, Map<String,Object> param){
        restTemplate=new RestTemplate();
        // 请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return restTemplate.getForEntity(url,String.class,param).getBody();
    }

    /**
     * 发送POST 表单请求
     * @param url
     * @param param
     * @return
     */
    public String PostFormData(String url,MultiValueMap<String,Object> param){
        restTemplate=new RestTemplate();
        // 请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return restTemplate.postForEntity(url,param,String.class).getBody();
    }
}
