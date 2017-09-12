package com.tms;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class Table {
	private String tableName;
	private String sequence ;
	private String pkName;
	private String pkType;
	private String dateFormatPattern="DateUtil.FMT_DATE_YYYY_MM_DD_HH_MI_SS";
	private List<Map<String,String>> enumFields = new LinkedList<Map<String,String>>();
	private Option entityOption = Option.overwrite;
	private Option entityDaoOption = Option.overwrite;
	private Option entityServiceOption = Option.overwrite;
	private Option entityActionOption = Option.overwrite;
	
	private Option bizDaoOption = Option.notOverwrite;
	private Option bizServiceOption = Option.notOverwrite;
	private Option bizActionOption = Option.notOverwrite;
	
	private Option tableConstOption = Option.notOverwrite;
	
	private boolean entityCache = true;

	public static enum Option{
		notCreate,overwrite,notOverwrite
	}
	
	public Table(String tableName){
		super();
		this.tableName = tableName;
		this.sequence = "SEQ_"+tableName;
		this.pkName = "ID";
	}
	
	public Table(String tableName,String sequence,String pkName){
		super();
		this.tableName = tableName;
		this.sequence = sequence;
		this.pkName = pkName;
	}
	
	public Table(String tableName,String sequence,String pkName,String dateFormatPattern){
		super();
		this.tableName = tableName;
		this.sequence = sequence;
		this.pkName = pkName;
		this.dateFormatPattern = dateFormatPattern;
	}
	
	public Table(String tableName,String sequence ,String pkName, Option entityOption, Option entityDaoOption, Option entityServiceOption, Option entityActionOption) {
		super();
		this.tableName = tableName;
		this.sequence = sequence;
		this.pkName = pkName;
		this.entityOption = entityOption;
		this.entityDaoOption = entityDaoOption;
		this.entityServiceOption = entityServiceOption;
		this.entityActionOption = entityActionOption;
	}

	public String getTableName() {
		return tableName;
	}

	public Table setTableName(String tableName) {
		this.tableName = tableName;
		return this;
	}

	public String getSequence() {
		return sequence;
	}

	public Table setSequence(String sequence) {
		this.sequence = sequence;
		return this;
	}

	public String getPkName() {
		return pkName;
	}

	public Table setPkName(String pkName) {
		this.pkName = pkName;
		return this;
	}
	
	public String getPkType() {
		return pkType;
	}

	public Table setPkType(String pkType) {
		this.pkType = pkType;
		return this;
	}
	
	public String getEntityClassName(){
		return CamelCaseUtils.toCapitalizeCamelCase(this.getTableName());
	}
	
	public String getEntityName(){
		return CamelCaseUtils.toCamelCase(this.getTableName());
	}
	
	public Option getEntityOption() {
		return entityOption;
	}

	public Table setEntityOption(Option entityOption) {
		this.entityOption = entityOption;
		return this;
	}

	public Option getEntityDaoOption() {
		return entityDaoOption;
	}

	public Table setEntityDaoOption(Option entityDaoOption) {
		this.entityDaoOption = entityDaoOption;
		return this;
	}

	public Option getEntityServiceOption() {
		return entityServiceOption;
	}

	public Table setEntityServiceOption(Option entityServiceOption) {
		this.entityServiceOption = entityServiceOption;
		return this;
	}

	public Option getEntityActionOption() {
		return entityActionOption;
	}

	public Table setEntityActionOption(Option entityActionOption) {
		this.entityActionOption = entityActionOption;
		return this;
	}

	public Option getBizDaoOption() {
		return bizDaoOption;
	}

	public Table setBizDaoOption(Option bizDaoOption) {
		this.bizDaoOption = bizDaoOption;
		return this;
	}

	public Option getBizServiceOption() {
		return bizServiceOption;
	}

	public Table setBizServiceOption(Option bizServiceOption) {
		this.bizServiceOption = bizServiceOption;
		return this;
	}

	public Option getBizActionOption() {
		return bizActionOption;
	}

	public Table setBizActionOption(Option bizActionOption) {
		this.bizActionOption = bizActionOption;
		return this;
	}
	
	public Option getTableConstOption() {
		return tableConstOption;
	}

	public Table setTableConstOption(Option tableConstOption) {
		this.tableConstOption = tableConstOption;
		return this;
	}

	public String getEntityDaoClassName(){
		return this.getEntityClassName()+"EntityDao";
	}
	
	public String getEntityDaoName(){
		return this.getEntityName()+"EntityDao";
	}
	
	public String getEntityServiceClassName(){
		return this.getEntityClassName()+"EntityService";
	}
	
	public String getEntityServiceName(){
		return this.getEntityName()+"EntityService";
	}
	
	public String getEntityActionClassName(){
		return this.getEntityClassName()+"EntityAction";
	}
	
	public String getEntityActionName(){
		return this.getEntityName()+"EntityAction";
	}
	
	public String getBizDaoClassName(){
		return this.getEntityClassName()+"Dao";
	}
	
	public String getBizDaoName(){
		return this.getEntityName()+"Dao";
	}
	
	public String getBizServiceClassName(){
		return this.getEntityClassName()+"Service";
	}
	
	public String getBizServiceName(){
		return this.getEntityName()+"Service";
	}
	
	public String getBizActionClassName(){
		return this.getEntityClassName()+"Action";
	}
	
	public String getBizActionName(){
		return this.getEntityName()+"BizAction";
	}
	
	public Map getTableInfo(){
		Map map = new HashMap();
		map.put("tableName", this.getTableName());
		map.put("PK_TYPE", this.getPkType());
		map.put("PK_NAME", this.getPkName());
		map.put("PK_NAME_CAMEL", CamelCaseUtils.toCamelCase(this.getPkName()));
		map.put("entityClass", this.getEntityClassName());
		map.put("entityName", this.getEntityName());
		map.put("entityDaoClass", this.getEntityDaoClassName());
		map.put("entityDaoName", this.getEntityDaoName());
		map.put("entityServiceClass", this.getEntityServiceClassName());
		map.put("entityServiceName", this.getEntityServiceName());
		map.put("entityActionClass", this.getEntityActionClassName());
		map.put("entityActionName", this.getEntityActionName());
		map.put("entityCache", this.isEntityCache());
		
		map.put("enumFields", this.getEnumFields());
		
		map.put("bizDaoClass", this.getBizDaoClassName());
		map.put("bizDaoName", this.getBizDaoName());
		map.put("bizServiceClass", this.getBizServiceClassName());
		map.put("bizServiceName", this.getBizServiceName());
		map.put("bizActionClass", this.getBizActionClassName());
		map.put("bizActionName", this.getBizActionName());
		return map;
	}

	public String getDateFormatPattern() {
		return dateFormatPattern;
	}

	public Table setDateFormatPattern(String dateFormatPattern) {
		this.dateFormatPattern = dateFormatPattern;
		return this;
	}
	public boolean isEntityCache() {
		return entityCache;
	}

	public Table setEntityCache(boolean entityCache) {
		this.entityCache = entityCache;
		return this;
	}

	public List<Map<String,String>> getEnumFields() {
		return enumFields;
	}

	public Table setEnumFields(String... enumFields) {
		for(String enumField:enumFields){
			Map<String,String> map = new HashMap<String,String>();
			map.put("ATTR_NAME", CamelCaseUtils.toCamelCase(enumField));
			map.put("COLUMN_NAME", enumField);
			this.enumFields.add(map);
		}
		return this;
	}
}
