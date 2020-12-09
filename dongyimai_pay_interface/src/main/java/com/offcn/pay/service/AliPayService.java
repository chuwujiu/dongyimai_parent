package com.offcn.pay.service;

import java.util.Map;

public interface AliPayService {
    public Map createNative(String out_trade_no, String total_fee);
}
