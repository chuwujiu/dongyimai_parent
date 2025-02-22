package com.offcn.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.search.service.ItemSearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ItemSearchController {
    @Reference
    private ItemSearchService itemSearchService;
    @RequestMapping
    public Map<String,Object> search(@RequestBody Map specMap){
        return itemSearchService.search(specMap);
    }
}
