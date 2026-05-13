package com.gi.riskmonitoring.controller;


import com.gi.riskmonitoring.model.AlertEntity;
import com.gi.riskmonitoring.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AlertController {

    @Autowired
    private  AlertRepository alertRepository;

    @GetMapping("/alerts")
    public List<AlertEntity> getAllAlerts(){
       return alertRepository.findAllByOrderByTimestampDesc();
    }

}
