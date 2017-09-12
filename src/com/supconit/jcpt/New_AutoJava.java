
package com.supconit.jcpt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.GenerateHelper;
import com.tms.CamelCaseUtils;
import com.tms.Table;
import com.tms.Table.Option;
import com.tms.TypeMapping;

public class New_AutoJava {
	private String jdbcURL = "jdbc:oracle:thin:@172.19.135.152:1521:orcl";
	private String user="jcpt_dy";
	private String password="jcpt_dy";
	private String projectPath;
	private String currentUserEmail="zhangpeng1@supcon.com";
	private String vcPath;

	public New_AutoJava() throws URISyntaxException {
		this.projectPath=this.getClass().getResource("/").toURI().getPath()+"generator/";
		this.vcPath= this.getClass().getResource("/").toURI().getPath()+"com/supconit/jcpt/velocity.properties";
	}

	private Statement stmt = null;
	private List<Table> tableList = new LinkedList<Table>();
	{


		tableList.add(new Table("yw_tgs_vehicle","seq_yw_tgs_vehicle","ID"));

	}
	
	public static void main(String[] args) throws Exception{
		New_AutoJava aj = new New_AutoJava();
		ResultSet rs = null;
		Connection conn = null;
		Class.forName("oracle.jdbc.driver.OracleDriver");
		conn = DriverManager.getConnection(aj.jdbcURL,aj.user,aj.password);
		aj.stmt = conn.createStatement();
		
		
		Velocity.init(aj.vcPath);
		for(Table table: aj.tableList){
			List<Map<String,String>> propertyList = aj.getPro(table);
			
			String pkType = Long.class.getSimpleName();
			for(Map<String,String> property :propertyList){
				if(table.getPkName()!=null && table.getPkName().equals(property.get("COLUMN_NAME"))){
					pkType = property.get("ATTR_TYPE");
					break;
				}
			}
			table.setPkType(pkType);
			
			aj.autoEntity(table,propertyList);
			aj.autoMapper(table,propertyList);
			aj.autoDao(table,propertyList);
			aj.autoService(table,propertyList);
			aj.autoController(table,propertyList);

		}

		if (rs != null) {
			rs.close();
			rs = null;
		}
		if (aj.stmt != null) {
			aj.stmt.close();
			aj.stmt = null;
		}
		if (conn != null) {
			conn.close();
			conn = null;
		}
	}
	
	public List<Map<String,String>> getPro(Table table)throws Exception{
		
		List<Map<String,String>> enumList = table.getEnumFields();
		Set<String> enumSet = new HashSet<String>();
		for(Map<String,String> map :enumList){
			enumSet.add(map.get("COLUMN_NAME"));
		}
		ResultSet rs = null;
//		Statement stmt = null;
//		Connection conn = null;
		List<Map<String,String>> list = new LinkedList<Map<String,String>>();
//		try {
//			Class.forName("oracle.jdbc.driver.OracleDriver");
//			// new oracle.jdbc.driver.OracleDriver();
//			conn = DriverManager.getConnection(this.jdbcURL,this.user,this.password);
//			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT A.COLUMN_NAME,A.DATA_TYPE,A.DATA_LENGTH,A.DATA_PRECISION,A.NULLABLE,B.COMMENTS AS COL_COMMENTS,C.COMMENTS AS TAB_COMMENTS " 
								+" FROM USER_TAB_COLUMNS A ,USER_COL_COMMENTS B,USER_TAB_COMMENTS C "
								+" WHERE A.TABLE_NAME =B.TABLE_NAME "
								+" AND A.TABLE_NAME=C.TABLE_NAME "
								+" AND A.COLUMN_NAME = B.COLUMN_NAME "
								+" AND A.TABLE_NAME='"+table.getTableName()+"'");
			
			while (rs.next()) {
				Map<String,String> map = new LinkedHashMap<String,String>();
				map.put("COLUMN_NAME", rs.getString("COLUMN_NAME"));
				map.put("DATA_TYPE", rs.getString("DATA_TYPE"));
				map.put("DATA_LENGTH", rs.getString("DATA_LENGTH"));
				map.put("DATA_PRECISION", rs.getString("DATA_PRECISION"));
				map.put("NULLABLE", rs.getString("NULLABLE").equals("N")?"false":"true");
				map.put("name", StringUtils.defaultIfBlank(rs.getString("COL_COMMENTS"),CamelCaseUtils.toCamelCase(map.get("COLUMN_NAME"))));
				map.put("TAB_COMMENTS", rs.getString("TAB_COMMENTS"));
//				map.put("name", CamelCaseUtils.toCamelCase(map.get("COLUMN_NAME")));
				map.put("code", CamelCaseUtils.toCamelCase(map.get("COLUMN_NAME")));
				map.put("type",TypeMapping.getJavaType(map.get("DATA_TYPE")));
				map.put("PK_NAME",table.getPkName());
				map.put("ISENUM",""+enumSet.contains(rs.getString("COLUMN_NAME")));
				if(rs.getString("COLUMN_NAME").equals("ID")){
					map.put("indexType", "PRIMARY");
				}
				list.add(map);
				// System.out.println(rs.getInt("deptno"));
			}
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (rs != null) {
//					rs.close();
//					rs = null;
//				}
//				if (stmt != null) {
//					stmt.close();
//					stmt = null;
//				}
//				if (conn != null) {
//					conn.close();
//					conn = null;
//				}
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
		Collections.sort(list, new Comparator<Map<String,String>>() {
			@Override
			public int compare(Map<String,String> o1, Map<String,String> o2) {
				return o1.get("COLUMN_NAME").compareTo(o2.get("COLUMN_NAME"));
			}
		});
		System.out.println(list);
		return list;
	}
	
	public void autoEntity(final Table table,List<Map<String,String>>propertyList){
		try {
			
			VelocityContext context = new VelocityContext();
			context.put("entity",new HashMap(){{
				this.put("entityConfigBean",  new HashMap(){{
					this.put("extend",  new HashMap(){{
						this.put("extendClassName", "hc.base.domains.LongId");
					}});
				}});
				
				this.put("code", table.getEntityClassName());
				this.put("group",  new HashMap(){{
					this.put("packageName", "com.supconit.ticc."+ table.getEntityName().toLowerCase());
				}});
			}});
			
			context.put("serialVersion", "2");
			context.put("primaryKey", new HashMap(){{
				this.put("code", CamelCaseUtils.toCamelCase(table.getPkName()));
				this.put("type", "Long");
			}});
			context.put("columns", propertyList);
//			context.internalPut("entity.group.packageName", "com.supconit."+table.getEntityName().toLowerCase()+".entities");
			
			String abstractFilePath = projectPath+"/hc/ticc/"+table.getEntityName().toLowerCase()+"/entities/"+table.getEntityClassName()+".java";
			this.autoTable(table, table.getEntityOption(), abstractFilePath, "Entity.java.vm", context);
			String filePath = projectPath+"/com/supconit/ticc/"+table.getEntityName().toLowerCase()+"/entities/"+table.getEntityClassName()+".java";
			this.autoTable(table, table.getEntityOption(), filePath, "UserEntity.java.vm", context);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void autoMapper(final Table table,List<Map<String,String>>propertyList){
		try {
			
			VelocityContext context = new VelocityContext();
			context.put("entity",new HashMap(){{
				this.put("entityConfigBean",  new HashMap(){{
					this.put("extend",  new HashMap(){{
						this.put("extendClassName", "hc.base.domains.LongId");
					}});
				}});
				
				this.put("code", table.getEntityClassName());
				this.put("group",  new HashMap(){{
					this.put("packageName", "com.supconit.ticc."+ table.getEntityName().toLowerCase());
				}});
				this.put("className", "com.supconit.ticc."+ table.getEntityName().toLowerCase()+".entities."+table.getEntityClassName());
				this.put("tableName", table.getTableName());
			}});
			
			context.put("serialVersion", "2");
			context.put("primaryKey", new HashMap(){{
				this.put("code", CamelCaseUtils.toCamelCase(table.getPkName()));
				this.put("type", "Long");
			}});
			context.put("columns", propertyList);
			context.put("seqName",table.getSequence());
			
			
//			context.internalPut("entity.group.packageName", "com.supconit."+table.getEntityName().toLowerCase()+".entities");
			
			String abstractFilePath = projectPath+"/hc/ticc/"+table.getEntityName().toLowerCase()+"/mappers/"+table.getEntityClassName()+"Mapper.xml";
			this.autoTable(table, table.getEntityOption(), abstractFilePath, "Mapper.xml.vm", context);
			String filePath = projectPath+"/com/supconit/ticc/"+table.getEntityName().toLowerCase()+"/mappers/"+table.getEntityClassName()+"Mapper.xml";
			this.autoTable(table, table.getEntityOption(), filePath, "UserMapper.xml.vm", context);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void autoDao(final Table table,List<Map<String,String>>propertyList){
		try {
			
			VelocityContext context = new VelocityContext();
			context.put("entity",new HashMap(){{
				this.put("entityConfigBean",  new HashMap(){{
					this.put("extend",  new HashMap(){{
						this.put("extendClassName", "hc.base.domains.LongId");
					}});
				}});
				
				this.put("code", table.getEntityClassName());
				this.put("group",  new HashMap(){{
					this.put("packageName", "com.supconit.ticc."+ table.getEntityName().toLowerCase());
					this.put("module", new HashMap(){{
						this.put("project", new HashMap(){{
							this.put("code", "ticc");
						}});
						this.put("code", table.getEntityClassName().toLowerCase());
					}});
					this.put("code", table.getEntityClassName().toLowerCase());
				}});
				this.put("className", "com.supconit.ticc."+ table.getEntityName().toLowerCase()+".entities."+table.getEntityClassName());
				this.put("tableName", table.getTableName());
			}});
			context.put("serialVersion", "2");
			context.put("primaryKey", new HashMap(){{
				this.put("code", table.getPkName());
				this.put("type", "Long");
			}});
			context.put("columns", propertyList);
			context.put("seqName",table.getSequence());
			
			
//			context.internalPut("entity.group.packageName", "com.supconit."+table.getEntityName().toLowerCase()+".entities");
			
			String abstractFilePath = projectPath+"/hc/ticc/"+table.getEntityName().toLowerCase()+"/daos/"+table.getEntityClassName()+"Dao.java";
			this.autoTable(table, table.getEntityOption(), abstractFilePath, "Dao.java.vm", context);
			abstractFilePath = projectPath+"/hc/ticc/"+table.getEntityName().toLowerCase()+"/daos/impl/"+table.getEntityClassName()+"DaoImpl.java";
			this.autoTable(table, table.getEntityOption(), abstractFilePath, "DaoImpl.java.vm", context);
			
			String filePath = projectPath+"/com/supconit/ticc/"+table.getEntityName().toLowerCase()+"/daos/"+table.getEntityClassName()+"Dao.java";
			this.autoTable(table, table.getEntityOption(), filePath, "UserDao.java.vm", context);
			filePath = projectPath+"/com/supconit/ticc/"+table.getEntityName().toLowerCase()+"/daos/impl/"+table.getEntityClassName()+"DaoImpl.java";
			this.autoTable(table, table.getEntityOption(), filePath, "UserDaoImpl.java.vm", context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void autoService(final Table table,List<Map<String,String>>propertyList){
		try {
			
			VelocityContext context = new VelocityContext();
			context.put("entity",new HashMap(){{
				this.put("entityConfigBean",  new HashMap(){{
					this.put("extend",  new HashMap(){{
						this.put("extendClassName", "hc.base.domains.LongId");
					}});
				}});
				
				this.put("code", table.getEntityClassName());
				this.put("group",  new HashMap(){{
					this.put("packageName", "com.supconit.ticc."+ table.getEntityName().toLowerCase());
					this.put("module", new HashMap(){{
						this.put("project", new HashMap(){{
							this.put("code", "ticc");
						}});
						this.put("code", table.getEntityClassName().toLowerCase());
					}});
					this.put("code", table.getEntityClassName().toLowerCase());
				}});
				this.put("className", "com.supconit.ticc."+ table.getEntityName().toLowerCase()+".entities."+table.getEntityClassName());
				this.put("tableName", table.getTableName());
			}});
			context.put("serialVersion", "2");
			context.put("primaryKey", new HashMap(){{
				this.put("code", table.getPkName());
				this.put("type", "Long");
			}});
			context.put("columns", propertyList);
			context.put("seqName",table.getSequence());
			
			
//			context.internalPut("entity.group.packageName", "com.supconit."+table.getEntityName().toLowerCase()+".entities");
			
			String abstractFilePath = projectPath+"/hc/ticc/"+table.getEntityName().toLowerCase()+"/services/"+table.getEntityClassName()+"Service.java";
			this.autoTable(table, table.getEntityOption(), abstractFilePath, "Service.java.vm", context);
			abstractFilePath = projectPath+"/hc/ticc/"+table.getEntityName().toLowerCase()+"/services/impl/"+table.getEntityClassName()+"ServiceImpl.java";
			this.autoTable(table, table.getEntityOption(), abstractFilePath, "ServiceImpl.java.vm", context);
			
			String filePath = projectPath+"/com/supconit/ticc/"+table.getEntityName().toLowerCase()+"/services/"+table.getEntityClassName()+"Service.java";
			this.autoTable(table, table.getEntityOption(), filePath, "UserService.java.vm", context);
			filePath = projectPath+"/com/supconit/ticc/"+table.getEntityName().toLowerCase()+"/services/impl/"+table.getEntityClassName()+"ServiceImpl.java";
			this.autoTable(table, table.getEntityOption(), filePath, "UserServiceImpl.java.vm", context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void autoController(final Table table,List<Map<String,String>>propertyList){
		try {
			
			VelocityContext context = new VelocityContext();
			context.put("entity",new HashMap(){{
				this.put("entityConfigBean",  new HashMap(){{
					this.put("extend",  new HashMap(){{
						this.put("extendClassName", "hc.base.domains.LongId");
					}});
				}});
				
				this.put("code", table.getEntityClassName());
				this.put("group",  new HashMap(){{
					this.put("packageName", "com.supconit.ticc."+ table.getEntityName().toLowerCase());
					this.put("module", new HashMap(){{
						this.put("project", new HashMap(){{
							this.put("code", "ticc");
						}});
						this.put("code", table.getEntityClassName().toLowerCase());
					}});
					this.put("code", table.getEntityClassName().toLowerCase());
				}});
				this.put("className", "com.supconit.ticc."+ table.getEntityName().toLowerCase()+".entities."+table.getEntityClassName());
				this.put("tableName", table.getTableName());
			}});
			context.put("serialVersion", "2");
			context.put("primaryKey", new HashMap(){{
				this.put("code", table.getPkName());
				this.put("type", "Long");
			}});
			context.put("columns", propertyList);
			context.put("seqName",table.getSequence());
			context.put("baseUrl","/"+table.getEntityName().toLowerCase());
			
//			context.internalPut("entity.group.packageName", "com.supconit."+table.getEntityName().toLowerCase()+".entities");
			
			String abstractFilePath = projectPath+"/hc/ticc/"+table.getEntityName().toLowerCase()+"/controllers/"+table.getEntityClassName()+"Controller.java";
			this.autoTable(table, table.getEntityOption(), abstractFilePath, "Controller.java.vm", context);
			
			String filePath = projectPath+"/com/supconit/ticc/"+table.getEntityName().toLowerCase()+"/controllers/"+table.getEntityClassName()+"Controller.java";
			this.autoTable(table, table.getEntityOption(), filePath, "UserController.java.vm", context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void autoTable(Table table,Option option,String filePath,String templateName){
		try {
			this.autoTable(table, option, filePath, templateName, null);
		}  catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void autoTable(Table table,Option option,String filePath,String templateName,VelocityContext context){
		try {
			if(option.equals(Table.Option.notCreate)){
				return;
			}
			File file = new File(filePath);
			if(file.exists() && option.equals(Table.Option.notOverwrite)){
				return;
			}
			
			if(!file.exists()){
				File parentFile = file.getParentFile();
				if(!parentFile.exists()){
					parentFile.mkdirs();
				}
			}
			
			context.put("currentUser", new HashMap(){{
				this.put("email", currentUserEmail);
			}});
			context.put("now",new Date());
			context.put("version","2.0");
			context.put("GenerateHelper", new GenerateHelper());
			context.put("j","#");
			context.put("d","$");
			
			
			//Template template = Velocity.getTemplate("produce/entity/templates/"+templateName);
			Template template = Velocity.getTemplate("produce/entity/new_templates/"+templateName);
			StringWriter writer = new StringWriter();
			template.merge(context, writer);
			
			FileOutputStream fos = new FileOutputStream(file); 
			PrintWriter filePrintWriter = new PrintWriter(file,"UTF-8") ;
			filePrintWriter.print(writer.toString());
			filePrintWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
