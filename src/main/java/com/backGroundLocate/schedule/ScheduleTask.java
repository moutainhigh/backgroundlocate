package com.backGroundLocate.schedule;

import com.backGroundLocate.util.LocationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Baiyutong
 * @date 2020/4/20
 */
@Configuration
@EnableScheduling
public class ScheduleTask {

    @Autowired
    private LocationUtil locationUtil;
}
