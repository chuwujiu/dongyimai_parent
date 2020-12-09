package com.offcn.solrutil;

import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    public void importItemData(){
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> itemList = itemMapper.selectByExample(example);
        for (TbItem item : itemList) {
            System.out.println(item.getTitle());
            Map<String,String> specMap= JSON.parseObject(item.getSpec(), Map.class);
            HashMap<String,String> newMap = new HashMap<>();
            for (String key : specMap.keySet()) {
                newMap.put(Pinyin.toPinyin(key,"").toLowerCase(),specMap.get(key));
            }
            item.setSpecMap(newMap);
        }
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();

    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");
        SolrUtil solrUtil=(SolrUtil)context.getBean("solrUtil");
        solrUtil.importItemData();
    }

}
