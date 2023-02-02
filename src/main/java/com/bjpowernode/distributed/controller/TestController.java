package com.bjpowernode.distributed.controller;

import com.bjpowernode.distributed.model.RedPacketRecord;
import com.bjpowernode.distributed.service.RedPacketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class TestController {
    private final Logger logger = LoggerFactory.getLogger(TestController.class);
    @Autowired
    private RedPacketService service;
    @PostMapping("/addhong")
    public  int add(){

            logger.info("领红包");
        RedPacketRecord redPacketRecord = new RedPacketRecord();
        redPacketRecord.setRedId(20);
        redPacketRecord.setUserId(2);
        redPacketRecord.setCreateTime(new Date());
        return service.addRedPacket(redPacketRecord);

    }

}
