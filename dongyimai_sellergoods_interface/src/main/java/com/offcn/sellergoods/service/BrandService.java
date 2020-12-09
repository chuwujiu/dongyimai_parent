package com.offcn.sellergoods.service;

import com.offcn.entity.PageResult;
import com.offcn.pojo.TbBrand;

import java.util.List;
import java.util.Map;

public interface BrandService {
    public PageResult findPage(TbBrand brand,int pageNum,int pageSize);

   public void add(TbBrand brand);

   public void delete(Long[] ids);
   public void update(TbBrand brand);
   public TbBrand findOne(Long id);
   public List<Map> selectOptionList();
}
