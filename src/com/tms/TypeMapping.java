package com.tms;

import java.sql.Blob;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class TypeMapping {
	private static Map<String,String> mapping = new LinkedHashMap<String,String>();
	
	static{
		mapping.put("VARCHAR", String.class.getSimpleName());
		mapping.put("NVARCHAR2", String.class.getSimpleName());
		mapping.put("CLOB", String.class.getSimpleName());
		mapping.put("VARCHAR2", String.class.getSimpleName());
		mapping.put("CHAR", String.class.getSimpleName());
		mapping.put("NUMERIC", Long.class.getSimpleName());
		mapping.put("NUMBER", Long.class.getSimpleName());
		mapping.put("INTEGER", Long.class.getSimpleName());
//		mapping.put("DATE", Calendar.class.getSimpleName());
//		mapping.put("TIMESTAMP(6)", Calendar.class.getSimpleName());
//		mapping.put("TIMESTAMP(3)", Calendar.class.getSimpleName());
		mapping.put("TIMESTAMP", "DateTime");
		mapping.put("DATE", "DateTime");
		mapping.put("TIMESTAMP(6)", "DateTime");
		mapping.put("TIMESTAMP(3)", "Date");
		mapping.put("BLOB", Blob.class.getSimpleName());

        mapping.put("FLOAT", Float.class.getSimpleName());

	}
	
	public static String getJavaType(String dbType){
		String javaType =mapping.get(dbType);
		if(javaType==null){
			System.out.println("unmatch dbType:"+dbType);
		}
		return javaType;
	}
	
	public static String getJdbcType(String javaType){
		for(Entry<String,String> entry :mapping.entrySet()){
			if(entry.getValue().equals(javaType)){
				return entry.getKey();
			}
		}
		System.out.println("unmatch javaType:"+javaType);
		return "";
	}
}
