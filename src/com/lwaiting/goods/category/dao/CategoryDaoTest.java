package com.lwaiting.goods.category.dao;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.junit.Test;

import cn.itcast.jdbc.TxQueryRunner;

public class CategoryDaoTest {
	private QueryRunner qr = new TxQueryRunner();
	@Test
	public void testFindAll() throws SQLException {
		String sql = "select * from t_category where pid is null";
		List<Map<String, Object>> mapList = qr.query(sql, new MapListHandler());
		System.out.println(mapList);
	}

}
