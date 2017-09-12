package com;

import java.util.Date;

import com.tms.CamelCaseUtils;
import com.tms.TypeMapping;

public class GenerateHelper {
	public String toSetter(String s){
		return "set"+CamelCaseUtils.toCapitalize(s);
	}
	
	public String toJavaType(String s){
		if(s.equals("DateTime")||s.equals("Date")||s.equals("Time")){
			return Date.class.getName();
		}
		return s;
	}
	
	public String toGetter(String s){
		return "get"+CamelCaseUtils.toCapitalize(s);
	}
	
	public String toColumnName(String s){
		return CamelCaseUtils.toUnderlineName(s).toUpperCase();
	}
	
	public String toJdbcType(String s){
		return TypeMapping.getJdbcType(s);
	}
	public String toPropertyName(String s){
		return s.toLowerCase();
	}
	
}
