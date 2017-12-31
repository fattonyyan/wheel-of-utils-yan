package com.yan.project.core.util;

import java.beans.PropertyDescriptor;
import java.beans.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * wrjsystem
 *
 * @author Tony
 * 
 * 1. 通过类名找表名  找属性列
 * 2. 通过实体对象 找表名 找属性列  找键值对
 * 
 */
public class EntityUtils {

	private String className = null;
	
	private String tableName = null;
	
	private List<String> fieldNameList = new ArrayList();
	
	private Map<String, String> paramMap = new HashMap();
	
	
	public static String getTableNameByClass(Class<?> clazz) throws Exception {
		Table table = (Table) clazz.getAnnotation(Table.class);
		
		String name = table.name();
		
		if(name == null){
			return null;
			// 注意报错
		} else {
			return name;
		}
	}
	
	public static String getPkName(Class<?> clazz) throws Exception {
		List<String> fieldNameList = getAllFieldNameListByClass(clazz);
		for(String fieldName:fieldNameList) {
			PropertyDescriptor pd = new PropertyDescriptor(fieldName, clazz);
			Method method = pd.getReadMethod();
			if(method.isAnnotationPresent(Id.class)){
				return fieldName;
			}
		}
//		getMethod.isAnnotationPresent(annotationClass);
		return null;
	}
	
	public static Object getFieldValue(String fieldName, Object entity) throws Exception {
		
		PropertyDescriptor pd = new PropertyDescriptor(fieldName, entity.getClass());
		Method readMethod = pd.getReadMethod();
		
		return readMethod.invoke(entity);
	}
	
	public static List<String> getAllFieldNameListByClass(Class<?> clazz) throws Exception {
		Field[] fieldNames = clazz.getDeclaredFields();
		Set<String> fieldNameSet = new HashSet();//使用集合来去掉重复的属性，这是为了考虑父类的情况，将父类的属性也放入。当然这里情况简单就没有使用
		List<String> fieldNameList = new ArrayList();
		
		// 其实还有个循环，就是对于 类，需要不断地找类的父类，来将所有的字段，包括继承来的字段，都找到
		
		for(int i =0 ; i < fieldNames.length; i++){
			// pk 主键是自动生成的（generatedKeyHolder()）
			Field fd = fieldNames[i];
			
			// 不要把 serialVersionUID 放进去，后面的取键值对都是用到了本方法，所以只要一次过滤就好
			// 可以使用 @Transient 如果 getter有这个注解就不添加进去
			// 使用这个 getMethod.isAnnotationPresent(annotationClass)判断是否会被添加到数据库
			if(fd.getName().trim() == "serialVersionUID"){
				continue;
			}
			System.out.println(fd.getAnnotation(Transient.class));
			if(fd.getDeclaredAnnotation(Transient.class) != null){
				continue;
			}
			fieldNameSet.add(fd.getName());//所以目前没有用到，可以使用 for(String x:fdSet){.add}来转换为 List
			fieldNameList.add(fd.getName());
		}
		
		return fieldNameList;
	}
	
	public static Map<String, Object> getParamMapByEntity(Object entity) throws Exception {
		HashMap<String,Object> paramMap = new HashMap();
		List<String> fieldNameList = getAllFieldNameListByClass(entity.getClass());
		
		for(String fieldName:fieldNameList){
//		for(int j = 0; j < fieldNameList.size(); j++){
//			String fieldName = fieldNameList.get(j);
			
			//要把主键剔除，因为ID 都用generatedKeyHolder()自动生成生成
			//我想到了一个办法（馊主意），就是通过字段包不包含“id”来判断是否是主键，实际上一个添加操作大部分只要一个主键
//			UUID.randomUUID().toString().replace("-", "");
//			原框架使用的不是自增主键，而是通过 UUID 来自动生成的。
			PropertyDescriptor pd = new PropertyDescriptor(fieldName,entity.getClass());
			Method getMethod = pd.getReadMethod();//获取属性的getter方法，为空null会抛异常
			
			// 注意 ID 的值要单独赋值进去
			
			// 如果属性值为空，则不应该加到键值对中
			if(getMethod.invoke(entity) == null|| "".equals(getMethod.invoke(entity))){
				continue;
			} else {
				paramMap.put(fieldName, getMethod.invoke(entity));
			}		
		}
		return paramMap;
	}
	
	public static Map<String, Object> getAllParamMapByEntity(Object entity) throws Exception {
		// 为什么声明这个方法，是由于在 save/update 的时候，filedNameList 是全部都有，所以才出现某个属性，没有对应的键值对的错误。
		// 事实上，仅 save/update 有非空值的数据是更节约时间的。（已经出现两个地方可以节约时间了。）
		
		HashMap<String,Object> paramMap = new HashMap();
		List<String> fieldNameList = getAllFieldNameListByClass(entity.getClass());
		
		for(int j = 0; j < fieldNameList.size(); j++){
			String fieldName = fieldNameList.get(j);
			
			PropertyDescriptor pd = new PropertyDescriptor(fieldName,entity.getClass());
			Method getMethod = pd.getReadMethod();//获取属性的getter方法，为空null会抛异常
			
			
			// 这个方法与上面的区别是，把所有字段都返回了，而不是非空字段。
			paramMap.put(fieldName, getMethod.invoke(entity));		
		}
		return paramMap;
	}
	
	
	

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<String> getFieldNameList() {
		return fieldNameList;
	}

	public void setFieldNameList(List<String> fieldNameList) {
		this.fieldNameList = fieldNameList;
	}

	public Map<String, String> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}
	
	
	
	
	
	
	
	
}
