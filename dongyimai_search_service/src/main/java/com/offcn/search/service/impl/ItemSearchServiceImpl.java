package com.offcn.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map map = new HashMap<>();
        String keywords = (String)searchMap.get("keywords");
        searchMap.put("keywords",keywords.replace(" ",""));
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(highlightOptions);
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
       query.addCriteria(criteria);
        searchByCategory(query,searchMap);
        searchByBrand(query,searchMap);
        searchBySpec(query,searchMap);
        searchByPrice(query,searchMap);
        searchByPage(query,searchMap);
        searchBySort(query,searchMap);
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        List<HighlightEntry<TbItem>> highlighted = page.getHighlighted();
        for (HighlightEntry<TbItem> highlightEntry : highlighted) {
            TbItem tbItem = highlightEntry.getEntity();
            if (highlightEntry.getHighlights().size()>0&&highlightEntry.getHighlights().get(0).getSnipplets().size()>0){
                List<String> snipplets = highlightEntry.getHighlights().get(0).getSnipplets();
                tbItem.setTitle(snipplets.get(0));
            }
        }
        List categoryList = searchCategoryList(searchMap);
        String categoryName=(String)searchMap.get("category");
        if (!"".equals(categoryName)){
            map.putAll(searchBrandAndSpecList(categoryName));
        }else {
            if (categoryList.size()>0){
                map.putAll(searchBrandAndSpecList((String) categoryList.get(0)));
            }
        }
        map.put("rows",page.getContent());
        map.put("categoryList",categoryList);
        map.put("totalPages",page.getTotalPages());
        map.put("total",page.getTotalElements());
        return map;

    }


    @Override
    public List searchCategoryList(Map searchMap) {
        List<String> list = new ArrayList<>();
        Query query=new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : content) {
            list.add(entry.getGroupValue());
        }
        return list;
    }

    @Override
    public void importList(List<TbItem> list) {
        for (TbItem item : list) {
            Map specMap = JSON.parseObject(item.getSpec(), Map.class);
            Map map=new HashMap();
            for (Object key :  specMap.keySet()) {
                map.put("item_spec_"+Pinyin.toPinyin((String)key,"").toLowerCase(),specMap.get(key));
            }
            item.setSpecMap(map);
        }
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List goodsList) {
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(goodsList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    private Map searchBrandAndSpecList(String category){
        Map map = new HashMap<>();
        Long typeId= (Long)redisTemplate.boundHashOps("itemCat").get(category);
        if(typeId!=null){
            List brandList = (List)redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList",brandList);
            List specList=(List)redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList",specList);
        }
        return map;
    }

    public void searchByCategory(Query query,Map searchMap){
        if (!"".equals(searchMap.get("category"))){
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(simpleFilterQuery);
        }
    }
    public void searchByBrand(Query query,Map searchMap){
        if (!"".equals(searchMap.get("brand"))){
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(simpleFilterQuery);
        }
    }
    public void searchBySpec(Query query,Map searchMap){
        if (searchMap.get("spec")!=null){
            Map<String,String> specMap=(Map)searchMap.get("spec");
            for (String key : specMap.keySet()) {
                String pinyinKey = Pinyin.toPinyin(key,"").toLowerCase();

                Criteria filterCriteria = new Criteria("item_spec_" + pinyinKey).is(specMap.get(key));
                System.out.println(pinyinKey);
                System.out.println(specMap.get(key));
                FilterQuery simpleFilterQuery = new SimpleFilterQuery();
                simpleFilterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(simpleFilterQuery);
            }

        }
    }
    public void searchByPrice(Query query,Map searchMap){
        if(!"".equals(searchMap.get("price"))){
            String[] prices = ((String) searchMap.get("price")).split("-");
            if (!prices[0].equals("0")){
                Criteria criteria = new Criteria("item_price").greaterThanEqual(prices[0]);
                SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(criteria);
                query.addFilterQuery(simpleFilterQuery);
            }
            if (!prices[1].equals("*")){
                Criteria criteria = new Criteria("item_price").lessThanEqual(prices[1]);
                SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(criteria);
                query.addFilterQuery(simpleFilterQuery);
            }
        }
    }
    public void searchBySort(Query query,Map searchMap){
        String sort=(String)searchMap.get("sort");
        String sortField=(String)searchMap.get("sortField");
        if (sort!=null&&!sort.equals("")){
            if (sort.equalsIgnoreCase("ASC")){
                Sort sort1 = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(sort1);
            }
            if (sort.equalsIgnoreCase("DESC")){
                Sort sort1 = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort1);
            }
        }
    }

    public void searchByPage(Query query,Map searchMap){
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo==null){
            pageNo=1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize==null){
            pageSize=20;
        }
        query.setOffset((pageNo-1)*pageSize);
        query.setRows(pageSize);
    }
//    @Override
//    public Map<String, Object> search(Map searchMap) {
//        HashMap<String, Object> map = new HashMap<>();
//        Query query = new SimpleQuery();
//        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
//        query.addCriteria(criteria);
//        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
//        map.put("rows",page.getContent());
//        return map;
//    }
}
