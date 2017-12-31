package com.yan.project.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Table;

import com.yan.project.core.util.EntityUtils;
/**
 * wrjsystem
 *
 * @author Tony
 *
 * 
 */
public class Finder {

	// 将字符串都赋值为 null 是否合理？赋值为空字符串会如何？
	// private StringBuilder sql = new StringBuilder();
	
	// 如果使用统计/聚集函数，当表中无数据时，只有 COUNT()返回0，其他均返回null(Mysql)
//	private List<String> paramList = new ArrayList<String>();
	
	// 为什么会全部都设置为 空字符串 ，因为在 toString()函数里，进行了字符串拼接
	// (为什么解决，因为这会使得堆上的对象会需要空字符串的磁盘空间，总之潜意识中觉得可能会有坑，所以埋了。)
	// 如何解决，在构造函数中赋值为空字符串，（不得不说，构造函数有很多事情可做。）
	private String selectSQL = "";
	
	private String tableName = "";
	
	// 不带 where
	private String whereSQL = "";
	
	private Map paramMap = new HashMap();
	
	// ORDER BY xxx ASC|DESC （可以多字段）
	private boolean isSort = true;
	private String orderSQL = "";
		
	// LIMIT 10, 5		从第11条记录开始查询5条记录返回
	private String pageSQL = "";
	
	// GROUP BY xxx		（可以多字段）
	private String groupbySQL = "";
	// 实现对分组进行条件限制，不能通过 WHERE 完成
	private String havingSQL = "";
	
	private boolean isDistinct = false;
	
	// sql语句是否完整，包括 paramList 表名  whereSQL paramMap 这四项是否齐全
	private boolean isFull = false;
	
	// 此外还有多表的查询 UNION  CARTESIAN PRODUCT		INNER JOIN 等内连接外连接
	
	public Finder(){
	}

	public Finder(Object entity){
		// sql.append(" SELECT ");
		Table table = (Table) entity.getClass().getAnnotation(Table.class);
		String name = table.name();
		
		if(name == null){
			return;
		} else {
			tableName = name;
			try {
				paramMap = EntityUtils.getParamMapByEntity(entity);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(selectSQL.trim() != ""&&tableName.trim() != ""&& whereSQL.trim() != ""){
			setFull();
		}
	}


	public String toString(){
		if(isFull){
			StringBuilder sql = new StringBuilder(" SELECT ");
			
			sql.append(selectSQL);
			
			sql.append(" FROM ").append(tableName);
			
			//查询条件的属性值，所以这个地方只需要非空的值
			sql.append(" WHERE ").append(whereSQL);
			// 是否要将 whereSQL 整个放进去。应该。即便是有 paramMap 仍然需要这么做来得到 whereSQL
			if(isSort){
				sql.append(orderSQL);
			}
			
			sql.append(groupbySQL);
			sql.append(havingSQL);
			
			sql.append(pageSQL);

			return sql.toString();
		} else {
			return null;
		}
	}

	public boolean isFull() {
		return isFull;
	}
	// 不应该有 isFull 的  setter 方法
	private void setFull() {
		this.isFull = true;
	}

	public String getSelectSQL() {
		return selectSQL;
	}

	public void setSelectSQL(String selectSQL) {
		this.selectSQL = selectSQL;
		if(selectSQL.trim() != ""&&tableName.trim() != ""&& whereSQL.trim() != ""){
			setFull();
		}
	}
	public void setSelectSQL(List<String> paramList) {
		StringBuilder sql = new StringBuilder();
		for(int i = 0; i < paramList.size(); i++){
			
			if((i+1)==paramList.size()){
				sql.append(paramList.get(i));
				break;
			}
			sql.append(paramList.get(i)).append(" , ");
		}
		this.selectSQL = sql.toString();
		if(selectSQL.trim() != ""&&tableName.trim() != ""&& whereSQL.trim() != ""){
			setFull();
		}
	}

	public String getOrderSQL() {
		return orderSQL;
	}

	public void setOrderSQL(String orderSQL) {
		this.orderSQL = orderSQL;
	}

	public boolean isDistinct() {
		return isDistinct;
	}

	public void setDistinct(boolean isDistinct) {
		this.isDistinct = isDistinct;
	}

	public String getTableName() {
		return tableName;
	}


	public void setTableName(String tableName) {
		this.tableName = tableName;
		if(selectSQL.trim() != ""&&tableName.trim() != ""&& whereSQL.trim() != ""){
			setFull();
		}
	}
	public void setTableName(Class<?> clazz) throws Exception {
		String tableName = EntityUtils.getTableNameByClass(clazz);
		this.tableName = tableName;
		if(selectSQL.trim() != ""&&tableName.trim() != ""&& whereSQL.trim() != ""){
			setFull();
		}
	}


	public String getWhereSQL() {
		return whereSQL;
	}


	public void setWhereSQL(String whereSQL) {
		this.whereSQL = whereSQL;
		if(selectSQL.trim() != ""&&tableName.trim() != ""&& whereSQL.trim() != ""){
			setFull();
		}
	}
	public void setWhereSQL(Map paramMap) {
		StringBuilder sql = new StringBuilder();
		Set<String> paramSet = paramMap.keySet();
		for(String param : paramSet){
			sql.append(param).append("=:").append(param).append(" AND ");
		}
		sql.append(" 1=1 ");
		
		this.whereSQL = sql.toString();
		if(selectSQL.trim() != ""&&tableName.trim() != ""&& whereSQL.trim() != ""){
			setFull();
		}
	}

	public Map getParamMap() {
		return paramMap;
	}


	public void setParamMap(Map paramMap) {
		this.paramMap = paramMap;
	}


	public boolean isSort() {
		return isSort;
	}


	public void setSort(boolean isSort) {
		this.isSort = isSort;
	}


	public String getHavingSQL() {
		return havingSQL;
	}


	public void setHavingSQL(String havingSQL) {
		this.havingSQL = havingSQL;
	}


	public String getPageSQL() {
		return pageSQL;
	}


	public void setPageSQL(String pageSQL) {
		this.pageSQL = pageSQL;
	}


	public String getGroupbySQL() {
		return groupbySQL;
	}


	public void setGroupbySQL(String groupbySQL) {
		this.groupbySQL = groupbySQL;
	}


	


}
