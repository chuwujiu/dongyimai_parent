package com.offcn.sellergoods.service;

import com.offcn.entity.PageResult;
import com.offcn.pojo.TbSeller;

import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SellerService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSeller> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);


	/**
	 * 增加
	*/
	public void add(TbSeller seller);


	/**
	 * 修改
	 */
	public void update(TbSeller seller);
	public void updateStatus(String sellerId,String status);



	public TbSeller findOne(String sellerId);




	public void delete(String[] sellerIds);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSeller seller, int pageNum, int pageSize);
	
}
