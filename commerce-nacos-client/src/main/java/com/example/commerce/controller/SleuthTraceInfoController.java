package com.example.commerce.controller;

import com.example.commerce.service.SleuthTraceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 输出跟踪信息
 */
@Slf4j
@RestController
@RequestMapping("/sleuth")
public class SleuthTraceInfoController {

    @Autowired
    private final SleuthTraceInfoService service;

    public SleuthTraceInfoController(SleuthTraceInfoService service) {
        this.service = service;
    }

    @GetMapping("/trace-info")
    public void logCurrentTraceInfo() {
        service.logCurrentTraceInfo();
    }
}
