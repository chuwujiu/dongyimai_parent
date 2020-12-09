package com.offcn.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.common.utils.IdWorker;
import com.offcn.pay.service.AliPayService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference
    private AliPayService aliPayService;

    @RequestMapping("/createNative")
    public Map createNative(){
        IdWorker idWorker = new IdWorker();
        return aliPayService.createNative(idWorker.nextId()+"","10000");
    }
}
