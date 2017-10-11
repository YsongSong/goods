package com.lwaiting.goods.category.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

import com.lwaiting.goods.category.domain.Category;

/**
 * 分类模块持久层
 * @author Administrator
 *
 */
public class CategoryDao {
	private QueryRunner qr = new TxQueryRunner();
	
	/**
	 * 把一个Map中的数据映射到Category中
	 * @param map
	 * @return
	 */
	private Category toCategory(Map<String, Object> map) {
		/*
		 * map{cid:xx, cname:xx, pid:xx, desc:xx, orderBy:xx}
		 * Catecory{cid:xx, cname:xx, praent:xx, desc:xx}
		 */
		Category category = CommonUtils.toBean(map, Category.class); 
		String pid = (String) map.get("pid");
		if(pid != null) {//如果父分类ID不为空
			/*
			 * 使用一个父分类对象来装载pid
			 * 再把父分类设置给category
			 */
			Category parent = new Category();
			parent.setCid(pid);
			category.setParent(parent);
		}
		return category;
	}
	
	/**
	 * 把多个Map(List<Map>)中的数据映射到Category(List<Category>)中
	 * @param map
	 * @return
	 */
	private List<Category> toCategoryList(List<Map<String, Object>> mapList) {
		List<Category> categoryList = new ArrayList<Category>();
		for(Map<String, Object> map : mapList) {
			Category c = toCategory(map);
			categoryList.add(c);
		}
		return categoryList;
	}
	
	/**
	 * 返回所有分类
	 * @return
	 * @throws SQLException 
	 */
	public List<Category> findAll() throws SQLException {
		/*
		 * 1. 查询出所有的一级分类
		 */
		String sql = "select * from t_category where pid is null";
		List<Map<String, Object>> mapList = qr.query(sql, new MapListHandler());
		
		List<Category> parents = toCategoryList(mapList);
		/*
		 * 2. 循环遍历所有的一级分类，为每一个一级分类加载它的二级分类
		 */
		for(Category parent : parents) {
			// 查询当前父分类的子分类
			List<Category> children = findByParent(parent.getCid());
			// 设置给父分类
			parent.setChildren(children);
		}
		return parents;
	}
	
	/**
	 * 通过父分类查询子分类
	 * @param pid
	 * @return
	 * @throws SQLException 
	 */
	public List<Category> findByParent(String pid) throws SQLException {
		String sql= "select * from t_category where pid=?";
		List<Map<String, Object>> mapList = qr.query(sql, new MapListHandler(), pid);
		return toCategoryList(mapList);
	}
}
