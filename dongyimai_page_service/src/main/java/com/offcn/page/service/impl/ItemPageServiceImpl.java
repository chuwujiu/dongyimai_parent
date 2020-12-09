package com.offcn.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.mapper.TbGoodsDescMapper;
import com.offcn.mapper.TbGoodsMapper;
import com.offcn.mapper.TbItemCatMapper;
import com.offcn.mapper.TbItemMapper;
import com.offcn.page.service.ItemPageService;
import com.offcn.pojo.TbGoods;
import com.offcn.pojo.TbGoodsDesc;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.zookeeper.ZooDefs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;

import static org.apache.zookeeper.ZooDefs.OpCode.delete;

@Service
public class ItemPageServiceImpl implements ItemPageService {
    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    @Autowired
    private TbGoodsMapper tbGoodsMapper;
    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbItemMapper itemMapper;



    @Override
    public boolean getItemHtml(Long goodsId) {

        try {
            Configuration configuration = freeMarkerConfig.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            HashMap map = new HashMap();

            TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(goodsId);
            map.put("goods",tbGoods);

            String itemCat1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
            map.put("itemCat1",itemCat1);

            String itemCat2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
            map.put("itemCat2",itemCat2);

            String itemCat3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();
            map.put("itemCat3",itemCat3);

            TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(goodsId);
            map.put("goodsDesc",tbGoodsDesc);

            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");
            criteria.andGoodsIdEqualTo(goodsId);
            example.setOrderByClause("is_default desc");
            List<TbItem> itemList = itemMapper.selectByExample(example);
            map.put("itemList",itemList);
            Writer out = new FileWriter(pagedir + goodsId + ".html");
            template.process(map,out);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deleteHtml(Long[] goodsIds) {
        try {
            for (Long goodsId : goodsIds) {
                System.out.println(pagedir + goodsId + ".html");
//                boolean exists = new File(pagedir + goodsId + ".html").exists();
//                System.out.println(exists);
                boolean delete = new File(pagedir + goodsId + ".html").delete();
                System.out.println(delete);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }
}
