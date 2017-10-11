package com.lwaiting.goods.category.service;

import java.sql.SQLException;
import java.util.List;

import com.lwaiting.goods.category.dao.CategoryDao;
import com.lwaiting.goods.category.domain.Category;

/**
 * 分类模块业务层
 * @author Administrator
 *
 */
public class CategoryService {
	private CategoryDao categoryDao = new CategoryDao();
	
	/**
	 * 查询所有分类
	 * @return
	 */
	public List<Category> findAll() {
		try {
			return categoryDao.findAll();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
