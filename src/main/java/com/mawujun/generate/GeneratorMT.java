package com.mawujun.generate;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.sunland.qogir.common.utils.FK;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

public class GeneratorMT {
	Logger logger = LoggerFactory.getLogger(GeneratorMT.class);
	
	private Class annotationClass=javax.persistence.Entity.class;
	private Class annotationTable=javax.persistence.Table.class;
	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String now_date=format.format(new Date());
	
	private String targetPackage;
	
	//findClassInPackageByFile( String packageName, String filePath, final boolean recursive, List<Class> clazzs)
//	
//	public void generateM(String dirPath,String packageName,String targetMDir,String targetPackage) throws IOException{
//		Assert.notNull(packageName);
//		Assert.notNull(targetMDir);
//		Assert.notNull(targetPackage);
//		this.targetPackage=targetPackage;
//		
//		List<Class> clazzs = new ArrayList<Class>();  
//		findClassInPackageByFile(packageName,dirPath,true,clazzs);
//		generateM(clazzs,targetMDir);
//	}
//	public void generateFK(String dirPath,String packageName,String targetMDir,String targetPackage) throws IOException{
//		Assert.notNull(packageName);
//		Assert.notNull(targetMDir);
//		Assert.notNull(targetPackage);
//		this.targetPackage=targetPackage;
//		
//		List<Class> clazzs = new ArrayList<Class>();  
//		findClassInPackageByFile(packageName,dirPath,true,clazzs);
//		generateFK_sql(clazzs,targetMDir);
//	}
	/**
	 * 搜索某个路径下面，标注了@Entity的类，并生成和android中的R类似的类，M
	 * @author mawujun email:160649888@163.com qq:16064988
	 * @param packageName 从哪些包中进行搜索
	 * @param targetMDir 生成的目标地址 存放在那个路径下:E:\\eclipse\\workspace\\hujibang\\src\\main\\java，最终的目录是targetMDir+targetPackage
	 * @param targetPackage com.mawujun.utils 生成的类的包名
	 * @throws IOException
	 */
	public void generateM(String packageName,String targetMDir,String targetPackage) throws IOException{
		Assert.notNull(packageName);
		Assert.notNull(targetMDir);
		Assert.notNull(targetPackage);
		this.targetPackage=targetPackage;
		
		List<Class> entities=getClasssFromPackage(packageName);
		generateM(entities,targetMDir);
	}
	public void generateFK(String packageName,String targetMDir) throws IOException{
		Assert.notNull(packageName);
		Assert.notNull(targetMDir);
		Assert.notNull(targetPackage);
		//this.targetPackage=targetPackage;
		
		List<Class> entities=getClasssFromPackage(packageName);
		generateFK_sql(entities,targetMDir);
	}
	
	public void generaterCSRD(String packageName,String targetMDir,String basePackage) throws IOException {
		Assert.notNull(packageName);
		Assert.notNull(targetMDir);
		Assert.notNull(basePackage);
		this.targetPackage=basePackage;
		
		List<Class> entities=getClasssFromPackage(packageName);
		generaterCSRD(entities,targetMDir,basePackage);
	}
	/**
	 * 生成源数据表，字段名称解释
	 * @param packageName
	 * @param targetMDir
	 * @param basePackage
	 * @throws IOException
	 */
	public void generaterExcel(String packageName,String filepath) throws IOException {
		Assert.notNull(packageName);
		Assert.notNull(filepath);
		//Assert.notNull(basePackage);
		//this.targetPackage=basePackage;
		HSSFWorkbook workbook = new HSSFWorkbook();
		
		List entities=getClasssFromPackage(packageName);
		//Collections.sort(entities);//entities.sort(c);
		generaterExcel(workbook,entities);
		
		File file=new File(filepath);
		FileOutputStream out=new FileOutputStream(file);
		workbook.write(out);
        workbook.close();
	}
	
    public void generaterExcel(HSSFWorkbook workbook,List<Class> entities) throws IOException {
    	HSSFCellStyle cellStyle = workbook.createCellStyle();
    	HSSFFont cellFont = workbook.createFont();
    	cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    	cellStyle.setFont(cellFont);
    	
    	
       
        for(Class clazz:entities) {
        	 int rowint=0;
        	// 生成一个(带标题)表格
        	ApiModel apiModel=(ApiModel)clazz.getAnnotation(ApiModel.class);
    		if(apiModel==null){
    			throw new NullPointerException(clazz.getClass()+"的ApiModel注解没有设置");
    		}
    		System.out.println(apiModel.value()+"==="+clazz.getName());
            HSSFSheet sheet = workbook.createSheet(apiModel.value());
            
        	Table annoation=(Table)clazz.getAnnotation(annotationTable);
    		if(annoation==null){
    			throw new NullPointerException(clazz.getClass()+"的Table注解没有设置");
    		}
    		HSSFRow row0 = sheet.createRow(rowint++);
    		HSSFCell cell00=row0.createCell(0);
    		cell00.setCellStyle(cellStyle);
    		cell00.setCellValue("表名称");
    		HSSFCell cell01=row0.createCell(1);
    		//cell01.setCellStyle(cellStyle);
    		cell01.setCellValue(annoation.name());
    		
    		HSSFRow row1 = sheet.createRow(rowint++);
    		HSSFCell cell10=row1.createCell(0);
    		cell10.setCellStyle(cellStyle);
    		cell10.setCellValue("实体名称");
    		HSSFCell cell11=row1.createCell(1);
    		//cell11.setCellStyle(cellStyle);
    		cell11.setCellValue(clazz.getName());
    		
    		//=============================================
    		
    		HSSFRow row2 = sheet.createRow(rowint++);
    		HSSFCell cell20=row2.createCell(0);
    		cell20.setCellStyle(cellStyle);
    		cell20.setCellValue("中文名称");
    		HSSFCell cell21=row2.createCell(1);
    		//cell21.setCellStyle(cellStyle);
    		cell21.setCellValue(apiModel.value());
    		
    		
    		HSSFRow rowtitle = sheet.createRow(rowint++);
    		int cellint=0;
    		HSSFCell cell=rowtitle.createCell(cellint++);
    		cell.setCellStyle(cellStyle);
    		cell.setCellValue("序号");
    		cell=rowtitle.createCell(cellint++);
    		cell.setCellStyle(cellStyle);
    		cell.setCellValue("列名称");
//    		cell=rowtitle.createCell(cellint++);
//    		cell.setCellStyle(cellStyle);
//    		cell.setCellValue("字段名称");
    		cell=rowtitle.createCell(cellint++);
    		cell.setCellStyle(cellStyle);
    		cell.setCellValue("中文描述");
    		
    		
    		Set<String> existField=new HashSet<String>();
    		 int index=1;
     		List<Field> fields= getClassField(clazz);
              for (Field field : fields) { //完全等同于上面的for循环
            	Transient transientAnnotation=(Transient)field.getAnnotation(Transient.class);
           		if(transientAnnotation!=null) {
           			continue;
           		}
           		
                  //System.out.println(field.getName()+" "+field.getType());
             	 if(!existField.contains(field.getName())){
             		 existField.add(field.getName());
             	 } else {
             		 continue;
             	 }
             	 logger.info(field.getName());
             	 
             	//生成列名
         		HSSFRow rowcolumn = sheet.createRow(rowint++);
         		cellint=0;
         		cell=rowcolumn.createCell(cellint++);
         		cell.setCellValue(index);index++;
         		//cell.setCellStyle(cellStyle);
         		//cell.setCellValue("中文描述");
             	
         		
         		
         		
         		cell=rowcolumn.createCell(cellint++);//列名称
         		 Column columnAnnotation=(Column)field.getAnnotation(Column.class);
     			 if(columnAnnotation==null || (columnAnnotation!=null && columnAnnotation.name().equals(""))){
     				cell.setCellValue(field.getName());
     			 } else {
     				cell.setCellValue(columnAnnotation.name());
     			 }
     			 
     			cell=rowcolumn.createCell(cellint++);//中文名称
     			ApiModelProperty apiModelProperty=(ApiModelProperty)field.getAnnotation(ApiModelProperty.class);
     			if(apiModelProperty==null || (apiModelProperty!=null && apiModelProperty.value().equals(""))){
     				cell.setCellValue(field.getName());
     			 } else {
     				cell.setCellValue(apiModelProperty.value());
     			 }
     			
//             	 Annotation embeddedIdAnnotataion=field.getAnnotation(EmbeddedId.class);
//             	 //是复合主键的情况下
//             	 if(embeddedIdAnnotataion!=null){
////             		 Class<?> fieldClass=field.getType();
////             		 fileWrite.append("	 /**\n");
////                 	 fileWrite.append("	 * 这个是复合主键。里面的是复合组件的组成列的列名\n");
////                 	 fileWrite.append("	 */\n");
////                 	 fileWrite.append("	public static final class "+fieldClass.getSimpleName()+" {\n");
////                 	 //Field[] embeddedIdFields = fieldClass.getDeclaredFields();
////                 	 List<Field> embeddedIdFields= getClassField(fieldClass);
////                 	 for (Field embeddedIdfield : embeddedIdFields) { 
////                 		 Column columnAnnotation=(Column)embeddedIdfield.getAnnotation(Column.class);
////                 		 if(columnAnnotation==null || (columnAnnotation!=null && columnAnnotation.name().equals(""))){
////             				 fileWrite.append("		public static final String "+embeddedIdfield.getName()+"=\""+embeddedIdfield.getName()+"\";\n");
////             			 } else {
////             				 fileWrite.append("		public static final String "+columnAnnotation.name()+"=\""+columnAnnotation.name()+"\";\n");
////             			 }
////                 	 }
////                 	 fileWrite.append("			\n");
////                 	 fileWrite.append("	}\n");
//             	 } else if(isBaseType(field.getType()) || field.getType().isEnum()){
//             			 
//             			 Column columnAnnotation=(Column)field.getAnnotation(Column.class);
//             			 if(columnAnnotation==null || (columnAnnotation!=null && columnAnnotation.name().equals(""))){
//             				cell.setCellValue(field.getName());
//             			 } else {
//             				cell.setCellValue(columnAnnotation.name());
//             			 }
//                     	
//                  } else if(!isOf(field.getType(),Map.class) && !isOf(field.getType(),Collection.class)){ 
////                     	 JoinColumn columnAnnotation=(JoinColumn)field.getAnnotation(Column.class);
////                     	 if(columnAnnotation==null || (columnAnnotation!=null && columnAnnotation.name().equals(""))){
////                     		 fileWrite.append("	/**\n");
////                         	 fileWrite.append("	* 访问外键的列名，用于sql的时候，返回的是"+field.getName()+"_id\n");
////                         	 fileWrite.append("	*/\n");
////                         	 fileWrite.append("	public static final String "+field.getName()+"_id=\""+field.getName()+"_id\";\n");
////                     	 } else {
////                     		 fileWrite.append("	/**\n");
////                         	 fileWrite.append("	* 访问外键的列名，用于sql的时候，返回的是"+columnAnnotation.name()+"_id\n");
////                         	 fileWrite.append("	*/\n");
////                         	 fileWrite.append("	public static final String "+columnAnnotation.name()+"=\""+columnAnnotation.name()+"\";\n");
////                     	 }
//                     	 
//                  }     
//                 
              }
    		
    		
    		
        }
        
    	
    }
	  /**
     * 生成controller，service，resository，dao这几个类
	 * @throws IOException 
     */
	private void generaterCSRD(List<Class> entities,String targetMDir,String basePackage) throws IOException {
		
		for(Class entity:entities) {
			generaterRepository(entity,targetMDir);
			generaterService(entity,targetMDir);
			generaterController(entity,targetMDir);
			//generaterDao(entity,targetMDir);
			//generaterCSRD(entity,targetMDir,basePackage);
		}
		//generaterDao(targetMDir);
    	
    }
	public void generaterCSRD(String targetMDir,String basePackage,Class... entities) throws IOException {
			this.targetPackage=basePackage;
			for(Class entity:entities) {
				generaterRepository(entity,targetMDir);
				generaterService(entity,targetMDir);
				generaterController(entity,targetMDir);
				//generaterDao(entity,targetMDir);
			}
			//generaterDao(targetMDir);
    }
	public void generaterRepository(Class entity,String targetMDir) throws IOException {
		
		//File dir=new File(targetMDir+File.separatorChar+this.targetPackage.replace('.', File.separatorChar)+File.separatorChar);
		File dir=new File(targetMDir+File.separatorChar+"repo"+File.separatorChar);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		File file=new File(dir.getAbsolutePath()+File.separatorChar+entity.getSimpleName()+"Repo.java");
		if(!file.exists()){
    		file.createNewFile();
    	}
    	FileWriter fileWrite=new FileWriter(file);
    	fileWrite.append("package "+this.targetPackage+".repo;\n");
    	fileWrite.append("import org.springframework.data.jpa.repository.JpaRepository;\n");
    	fileWrite.append("import org.springframework.stereotype.Repository;\n");

    	fileWrite.append("import "+entity.getName()+";\n");
    	fileWrite.append("/*******************************************************************************\n");
    	fileWrite.append("					浙江信电技术股份有限公司\n");
    	fileWrite.append("					created at "+now_date+"\n");
    	fileWrite.append("					请按照业务需求自行修改\n");
    	fileWrite.append("********************************************************************************/\n");
    	fileWrite.append("@Repository\n");
    	fileWrite.append("public interface "+entity.getSimpleName()+"Repo extends JpaRepository<"+entity.getSimpleName()+", String>{ \n");
    	fileWrite.append("\n");
    	fileWrite.append("}\n");
    	fileWrite.close();
		
	}
	
	public void generaterDao(Class entity,String targetMDir) throws IOException {
		File dir=new File(targetMDir+File.separatorChar+"dao"+File.separatorChar);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		File file=new File(dir.getAbsolutePath()+File.separatorChar+entity.getSimpleName()+"Dao.java");
		if(!file.exists()){
    		file.createNewFile();
    	}
    	FileWriter fileWrite=new FileWriter(file);
    	fileWrite.append("package "+this.targetPackage+".dao;\n");
    	fileWrite.append("import com.sunland.qogir.common.dao.AbstractDao;\n");
    	fileWrite.append("import org.springframework.stereotype.Repository;\n");

    	fileWrite.append("import "+entity.getName()+";\n");
    	fileWrite.append("/*******************************************************************************\n");
    	fileWrite.append("					浙江信电技术股份有限公司\n");
    	fileWrite.append("					created at "+now_date+"\n");
    	fileWrite.append("					请按照业务需求自行修改\n");
    	fileWrite.append("********************************************************************************/\n");
    	fileWrite.append("@Repository\n");
    	fileWrite.append("public class "+entity.getSimpleName()+"Dao extends AbstractDao<"+entity.getSimpleName()+", String>{ \n");
    	fileWrite.append("\n");
    	fileWrite.append("}\n");
    	fileWrite.close();
	}
//	public void generaterDao(String targetMDir) throws IOException {
//		File dir=new File(targetMDir+File.separatorChar+"dao"+File.separatorChar);
//		if(!dir.exists()) {
//			dir.mkdirs();
//		}
//		File file=new File(dir.getAbsolutePath()+File.separatorChar+"Dao.java");
//		if(!file.exists()){
//    		file.createNewFile();
//    	}
//    	FileWriter fileWrite=new FileWriter(file);
//    	fileWrite.append("package "+this.targetPackage+".dao;\n");
//    	fileWrite.append("import javax.persistence.EntityManager;\n");
//    	fileWrite.append("import javax.persistence.PersistenceContext;\n");
//    	fileWrite.append("import org.springframework.stereotype.Repository;\n");
//    	fileWrite.append("import com.sunland.qogir.common.dao.BaseDao;\n");
//
//    	//fileWrite.append("import "+entity.getName()+";\n");
//    	fileWrite.append("/*******************************************************************************\n");
//    	fileWrite.append("					全局的，可以直接使用的DAO\n");
//    	fileWrite.append("					请按照业务需求自行修改\n");
//    	fileWrite.append("********************************************************************************/\n");
//    	fileWrite.append("@Repository\n");
//    	fileWrite.append("public class Dao extends BaseDao{ \n");
//    	fileWrite.append("	@Override\n");
//    	fileWrite.append("	@PersistenceContext \n");
//    	fileWrite.append("	public void setEntityManager(EntityManager entityManager) {\n");
//    	fileWrite.append("		super.entityManager=entityManager;\n");
//    	fileWrite.append("	}\n");
//    	fileWrite.append("\n");
//    	fileWrite.append("}\n");
//    	fileWrite.close();
//	}
	
	public void generaterService(Class entity,String targetMDir) throws IOException {
		String simpleName=entity.getSimpleName();
		String simpleName_uncapitalize=StringUtils.uncapitalize(simpleName);
		String repository=simpleName_uncapitalize+"Repo";
		
		//File dir=new File(targetMDir+File.separatorChar+this.targetPackage.replace('.', File.separatorChar)+File.separatorChar);
		File dir=new File(targetMDir+File.separatorChar+"service"+File.separatorChar);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		File file=new File(dir.getAbsolutePath()+File.separatorChar+entity.getSimpleName()+"Service.java");
		if(!file.exists()){
    		file.createNewFile();
    	}
    	FileWriter fileWrite=new FileWriter(file);
    	fileWrite.append("package "+this.targetPackage+".service;\n");
   
    	fileWrite.append("import java.util.List;\n");
    	fileWrite.append("import org.slf4j.Logger;\n");
    	fileWrite.append("import org.slf4j.LoggerFactory;\n");

    	fileWrite.append("import org.springframework.beans.factory.annotation.Autowired;\n");
    	fileWrite.append("import org.springframework.data.domain.Page;\n");
    	fileWrite.append("import org.springframework.data.domain.PageRequest;\n");
    	fileWrite.append("import org.springframework.data.domain.Pageable;\n");
    	fileWrite.append("import org.springframework.stereotype.Service;\n");
    	fileWrite.append("import org.springframework.transaction.annotation.Transactional;\n");
    	fileWrite.append("import org.springframework.data.domain.Example;\n");
    	fileWrite.append("import java.util.Optional;\n");
    	
    	fileWrite.append("import com.sunland.qogir.repo."+simpleName+"Repo;\n");
    	fileWrite.append("\n");
    	fileWrite.append("import "+entity.getName()+";\n");
    	fileWrite.append("/*******************************************************************************\n");
    	fileWrite.append("					浙江信电技术股份有限公司\n");
    	fileWrite.append("					created at "+now_date+"\n");
    	fileWrite.append("					请按照业务需求自行修改\n");
    	fileWrite.append("********************************************************************************/\n");
    	fileWrite.append("@Service\n");
    	fileWrite.append("@Transactional(\"transactionManager\")\n");
    	fileWrite.append("public class "+simpleName+"Service   {");fileWrite.append("\n");
    	fileWrite.append("	private static Logger logger = LoggerFactory.getLogger("+simpleName+"Service.class);\n");
    	fileWrite.append("	@Autowired");fileWrite.append("\n");
    	fileWrite.append("	private "+simpleName+"Repo "+simpleName_uncapitalize+"Repo;");fileWrite.append("\n");
    	fileWrite.append("	public "+simpleName+" get(String entityId) {");fileWrite.append("\n");
    	fileWrite.append("		Optional<"+simpleName+"> option= "+simpleName_uncapitalize+"Repo.findById(entityId);");fileWrite.append("\n");
    	fileWrite.append("		if(option.isPresent()) {");fileWrite.append("\n");
    	fileWrite.append("			return option.get();");fileWrite.append("\n");
    	fileWrite.append("		} else {");fileWrite.append("\n");
    	fileWrite.append("			return null;");fileWrite.append("\n");
    	fileWrite.append("		}");fileWrite.append("\n");
    	fileWrite.append("	}");fileWrite.append("\n");
    	fileWrite.append("	public void create("+simpleName+" entity) {");fileWrite.append("\n");
    	fileWrite.append("		"+simpleName_uncapitalize+"Repo.save(entity);");fileWrite.append("\n");
    	fileWrite.append("	}");fileWrite.append("\n");
    	fileWrite.append("	public void update("+simpleName+" entity) {");fileWrite.append("\n");
    	fileWrite.append("		"+simpleName_uncapitalize+"Repo.save(entity);");fileWrite.append("\n");
    	fileWrite.append("	}");fileWrite.append("\n");

    	fileWrite.append("	public void delete(String entityId) {");fileWrite.append("\n");
    	fileWrite.append("		"+repository+".deleteById(entityId);");fileWrite.append("\n");
    	fileWrite.append("	}");fileWrite.append("\n");
    	fileWrite.append("	public void delete(String... entityIds) {");fileWrite.append("\n");
    	fileWrite.append("		if(entityIds!=null) {");fileWrite.append("\n");
    	fileWrite.append("			for(String id:entityIds) {");fileWrite.append("\n");
    	fileWrite.append("				"+repository+".deleteById(id);");fileWrite.append("\n");
    	fileWrite.append("			}");fileWrite.append("\n");
    	fileWrite.append("		}");fileWrite.append("\n");
    	fileWrite.append("	}");fileWrite.append("\n");
    	
    	fileWrite.append("	public List<"+simpleName+"> list() {");fileWrite.append("\n");
    	fileWrite.append("		return "+repository+".findAll();");fileWrite.append("\n");
    	fileWrite.append("	}");fileWrite.append("\n");
    	fileWrite.append("	public  Page<"+simpleName+"> queryPage("+simpleName+" entity,int pageNumber,int pageSize) {");fileWrite.append("\n");
    	fileWrite.append("		Example<"+simpleName+"> example=Example.of(entity);");fileWrite.append("\n");
    	fileWrite.append("		Pageable pageable=PageRequest.of(pageNumber, pageSize);");fileWrite.append("\n");
    	fileWrite.append("		return "+repository+".findAll(example, pageable);");fileWrite.append("\n");
    	fileWrite.append("	}");fileWrite.append("\n");
    	
    	fileWrite.append("	public boolean existsById(String id) {");fileWrite.append("\n");
    	fileWrite.append("		return "+repository+".existsById(id);");fileWrite.append("\n");
    	fileWrite.append("	}");fileWrite.append("\n");
    	fileWrite.append("	public long count() {");fileWrite.append("\n");
    	fileWrite.append("		return "+repository+".count();");fileWrite.append("\n");
    	fileWrite.append("	}");fileWrite.append("\n");
    	
    	fileWrite.append("}");
//    	fileWrite.append("public interface "+entity.getSimpleName()+"Repository extends JpaRepository<"+entity.getSimpleName()+", String>{ \n");
//    	fileWrite.append("\n");
//    	fileWrite.append("}\n");
    	fileWrite.close();
	}
	
	public void generaterController(Class entity,String targetMDir) throws IOException {
		String simpleName=entity.getSimpleName();
		String simpleName_uncapitalize=StringUtils.uncapitalize(simpleName);

		
		//File dir=new File(targetMDir+File.separatorChar+this.targetPackage.replace('.', File.separatorChar)+File.separatorChar);
		File dir=new File(targetMDir+File.separatorChar+"controller"+File.separatorChar);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		File file=new File(dir.getAbsolutePath()+File.separatorChar+entity.getSimpleName()+"Controller.java");
		if(!file.exists()){
    		file.createNewFile();
    	}
    	FileWriter fileWrite=new FileWriter(file);
    	fileWrite.append("package "+this.targetPackage+".controller;\n");
    	fileWrite.append("import "+entity.getName()+";\n");
    	fileWrite.append("import java.util.List;\n");
    	fileWrite.append("import javax.validation.Valid;\n");
    	fileWrite.append("import org.springframework.beans.factory.annotation.Autowired;\n");
    	fileWrite.append("import org.springframework.web.bind.annotation.RequestBody;\n");
    	fileWrite.append("import org.springframework.stereotype.Controller;\n");
    	fileWrite.append("import org.springframework.web.bind.annotation.RequestMapping;\n");
    	fileWrite.append("import org.springframework.web.bind.annotation.ResponseBody;\n");
    	fileWrite.append("import com.sunland.qogir.common.utils.ResponseData;\n");
    	
    	fileWrite.append("import io.swagger.annotations.Api;\n");
    	fileWrite.append("import io.swagger.annotations.ApiImplicitParam;\n");
    	fileWrite.append("import io.swagger.annotations.ApiImplicitParams;\n");
    	fileWrite.append("import io.swagger.annotations.ApiOperation;\n");
    	fileWrite.append("import com.sunland.qogir.service."+simpleName+"Service;\n");
    	fileWrite.append("	\n");
    	fileWrite.append("	\n");
    	fileWrite.append("/*******************************************************************************\n");
    	fileWrite.append("					浙江信电技术股份有限公司\n");
    	fileWrite.append("					created at "+now_date+"\n");
    	fileWrite.append("					请按照业务需求自行修改\n");
    	fileWrite.append("********************************************************************************/\n");
    	fileWrite.append("@Api\n");
    	fileWrite.append("@Controller\n");
    	fileWrite.append("@RequestMapping(\"/"+simpleName_uncapitalize+"\")\n");
    	fileWrite.append("public class "+simpleName+"Controller {\n");
    	fileWrite.append("	@Autowired\n");
    	fileWrite.append("	private "+simpleName+"Service "+simpleName_uncapitalize+"Service;\n");
    	fileWrite.append("	\n");
    	fileWrite.append("	\n");
    	fileWrite.append("	@ApiOperation(value = \"获取单个对象\", notes = \"\",httpMethod=\"POST\") \n");
    	fileWrite.append("	@ApiImplicitParams({\n");
    	fileWrite.append("		@ApiImplicitParam(name = \"tid\", value = \"tid\", required = true, dataType = \"String\",paramType=\"query\")\n");
    	fileWrite.append("	})\n");
    	fileWrite.append("	\n");
    	fileWrite.append("	@RequestMapping(value=\"/get\")\n");
    	fileWrite.append("	@ResponseBody\n");
    	fileWrite.append("	public ResponseData get(String tid) {\n");
    	fileWrite.append("		"+simpleName+" entity="+simpleName_uncapitalize+"Service.get(tid);\n");
    	fileWrite.append("		return ResponseData.getInstance().setData(entity);\n");
    	fileWrite.append("	}\n");
    	fileWrite.append("	\n");
    	fileWrite.append("	@ApiOperation(value = \"创建新对象\", notes = \"\",httpMethod=\"POST\")  \n");
    	fileWrite.append("	@RequestMapping(value=\"/create\")\n");
    	fileWrite.append("	@ResponseBody\n");
    	fileWrite.append("	public ResponseData create(@Valid @RequestBody "+simpleName+" entity) {\n");
    	fileWrite.append("		"+simpleName_uncapitalize+"Service.create(entity);\n");
    	fileWrite.append("		return ResponseData.getInstance().setData(entity);\n");
    	fileWrite.append("	}\n");
    	fileWrite.append("	\n");
    	fileWrite.append("	@ApiOperation(value = \"更新对象\", notes = \"\",httpMethod=\"POST\") \n");
    	fileWrite.append("	@RequestMapping(value=\"/update\")\n");
    	fileWrite.append("	@ResponseBody\n");
    	fileWrite.append("	public ResponseData update(@Valid @RequestBody "+simpleName+" entity) {\n");
    	fileWrite.append("		"+simpleName_uncapitalize+"Service.update(entity);\n");
    	fileWrite.append("		return ResponseData.getInstance().setData(entity);\n");
    	fileWrite.append("	}\n");
    	fileWrite.append("	\n");
    	fileWrite.append("	@ApiOperation(value = \"删除对象\", notes = \"\",httpMethod=\"POST\") \n");
    	fileWrite.append("	@ApiImplicitParam(name = \"tid\", value = \"\", required = true, dataType = \"String\",paramType=\"query\")\n");
    	fileWrite.append("	@RequestMapping(value=\"/delete\")\n");
    	fileWrite.append("	@ResponseBody\n");
    	fileWrite.append("	public ResponseData delete(String tid) {\n");
    	fileWrite.append("		"+simpleName_uncapitalize+"Service.delete(tid);\n");
    	fileWrite.append("		return ResponseData.getInstance();\n");
    	fileWrite.append("	}\n");
    	fileWrite.append("	\n");
    	fileWrite.append("	@ApiOperation(value = \"批量删除对象\", notes = \"\",httpMethod=\"POST\") \n");
    	fileWrite.append("	@ApiImplicitParam(name = \"tides\", value = \"tid的数组\", required = true, dataType = \"String\",paramType=\"query\")\n");
    	fileWrite.append("	@RequestMapping(value=\"/deleteByIds\")\n");
    	fileWrite.append("	@ResponseBody\n");
    	fileWrite.append("	public ResponseData deleteByIds(String[] tides) {\n");
    	fileWrite.append("		"+simpleName_uncapitalize+"Service.delete(tides);\n");
    	fileWrite.append("		return ResponseData.getInstance();\n");
    	fileWrite.append("	}\n");
    	fileWrite.append("	\n");
    	fileWrite.append("	@ApiOperation(value = \"查询所有对象\", notes = \"\",httpMethod=\"POST\") \n");
    	fileWrite.append("	@RequestMapping(value=\"/list\")\n");
    	fileWrite.append("	@ResponseBody\n");
    	fileWrite.append("	public ResponseData list() {\n");
    	fileWrite.append("		List<"+simpleName+"> list="+simpleName_uncapitalize+"Service.list();\n");
    	fileWrite.append("		return ResponseData.getInstance().setData(list);\n");
    	fileWrite.append("	}\n");
    	fileWrite.append("	\n");
//    	fileWrite.append("	\n");
//    	fileWrite.append("	\n");
//    	fileWrite.append("	\n");
//    	fileWrite.append("	\n");
//    	fileWrite.append("	\n");
//    	fileWrite.append("	\n");
//    	fileWrite.append("	\n");
//    	fileWrite.append("	\n");
//    	fileWrite.append("	\n");
//    	fileWrite.append("	\n");
//    	fileWrite.append("	\n");
//    	fileWrite.append("	\n");
//    	fileWrite.append("	\n");
//    	fileWrite.append("	\n");
//    	fileWrite.append("	\n");
//    	fileWrite.append("	\n");

		fileWrite.append("}");
		fileWrite.close();
	}
	private void generateFK_sql(List<Class> entities,String targetMDir) throws IOException{
    	//生成M
    	//File file=new File(targetMDir+File.separatorChar+this.targetPackage.replace('.', File.separatorChar)+File.separatorChar+"FK_create.sql");
		File file=new File(targetMDir+File.separatorChar+File.separatorChar+"FK_create.sql");
    	//file.delete();
    	if(!file.exists()){
    		file.createNewFile();
    	}
    	FileWriter fileWrite=new FileWriter(file);

//    	fileWrite.append("package "+this.targetPackage+";\n");
//    	fileWrite.append("public final class M {\n");
    	
    	fileWrite.append("########################################这是添加外键约束\n");
    	for(Class clazz:entities){
    		logger.info("============================================="+clazz.getName());

    		
    		 //Field[]fields = clazz.getDeclaredFields();
    		 List<Field> fields= getClassField(clazz);
    		 
    		 //Set<String> existField=new HashSet<String>();
             for (Field field : fields) { //完全等同于上面的for循环
            	 FK fk=field.getAnnotation(FK.class);
            	 
            	 if(fk!=null){
            		 String pk_tablename="";
                	 String pk_column="";
                	 String fk_tablename=gettablename(clazz);
                	 String fk_column=getcloumnname(field);
	     			if(fk!=null){
	     				if(fk.cls()!=null){
	     					Class pk_class=fk.cls();
	     					pk_tablename=gettablename(pk_class);
	     					
	     					
	     				} else if(fk.table()!=null){
	     					pk_tablename=fk.table();
	     				} else {
	     					logger.error(clazz.toString()+"的"+field.getName()+"的FK定义没有配置cls和table!");
	     				}
	     				if(fk.column()!=null){
	     					pk_column=fk.column();
	     				}
	     			}
	     			 String fk_name="fk_"+fk_tablename+"_"+fk_column;
	            	 fileWrite.append("--alter table "+fk_tablename+" drop constraint "+fk_name+";\n");
	            	 //alter table HR_POSITION add constraint org_id_1 foreign key (ORG_ID) references hr_org (ID);
	            	 fileWrite.append("alter table "+fk_tablename+" add constraint "+fk_name+" foreign key ("+fk_column+") references "+pk_tablename+" ("+pk_column+");\n");
            	 }
            	
             }
             
    	}
    	fileWrite.append("########################################结束\n");
    	fileWrite.close();
	}
	private String getcloumnname(Field field){
		String fk_column="";
		 Column column=field.getAnnotation(Column.class);
			if(column!=null){
				if(column.name()!=null && !"".equals(column.name())){
					fk_column=column.name();
				} else {
					fk_column=field.getName();
				}
			} else {
				fk_column=field.getName();
			}
			return fk_column;
	}
	private String gettablename(Class clazz){
		String pk_tablename="";
		Table tableAnnotation=(Table)clazz.getAnnotation(Table.class);
			if(tableAnnotation!=null){
				pk_tablename=tableAnnotation.name();
			} else {
				Entity entityAnnotation=(Entity)clazz.getAnnotation(Entity.class);
				if(entityAnnotation!=null){
					pk_tablename=entityAnnotation.name();
				} else {
					//throw new RuntimeException("没有在实体类上添加@Entity注解");
					pk_tablename=clazz.getName();
				}
			}
			return pk_tablename;
	}
	

    /** 
     * 获得包下面的所有的class 
     *  
     * @param pack 
     *            package完整名称 
     * @return List包含所有class的实例 
     */  
    private List<Class> getClasssFromPackage(String pack) {  
        List<Class> clazzs = new ArrayList<Class>();  
      
        // 是否循环搜索子包  
        boolean recursive = true;  
      
        // 包名字  
        String packageName = pack;  
        // 包名对应的路径名称  
        String packageDirName = packageName.replace('.','/');  
      
        Enumeration<URL> dirs;  
      
        try {  
            dirs = GeneratorMT.class.getClassLoader().getResources(packageDirName);  
            while (dirs.hasMoreElements()) {  
                URL url = dirs.nextElement();  
      
                String protocol = url.getProtocol();  
      
                if ("file".equals(protocol)) {  
                	logger.info("file类型的扫描");  
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");  
                    findClassInPackageByFile(packageName, filePath, recursive, clazzs);  
                } else if ("jar".equals(protocol)) {  
                	logger.info("jar类型的扫描");  
                }  
            }  
      
        } catch (Exception e) {  
            e.printStackTrace();  
            logger.info(e.getMessage()); 
        }  
      
        return clazzs;  
    } 
    
    
    /** 
     * 在package对应的路径下找到所有的class 
     *  
     * @param packageName 
     *            package名称 
     * @param filePath 
     *            package对应的路径 
     * @param recursive 
     *            是否查找子package 
     * @param clazzs 
     *            找到class以后存放的集合 
     */  
    private void findClassInPackageByFile( String packageName, String filePath, final boolean recursive, List<Class> clazzs) {  

        File dir = new File(filePath);  
        if (!dir.exists() || !dir.isDirectory()) {  
            return;  
        }  
        // 在给定的目录下找到所有的文件，并且进行条件过滤  
        File[] dirFiles = dir.listFiles(new FileFilter() {  
            public boolean accept(File file) {  
                boolean acceptDir = recursive && file.isDirectory();// 接受dir目录  
                boolean acceptClass = file.getName().endsWith("class");// 接受class文件  
                return acceptDir || acceptClass;  
            }  
        });  
      
        for (File file : dirFiles) {  
            if (file.isDirectory()) {  
                findClassInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, clazzs);  
            } else {  
                String className = file.getName().substring(0, file.getName().length() - 6);  
                try {  
                	Class clazz=GeneratorMT.class.getClassLoader().loadClass(packageName + "." + className);
                	Annotation annoation=clazz.getAnnotation(annotationClass);
    				if(annoation!=null){
    					logger.info("============================找到实体类:"+clazz.getName());
    					clazzs.add(clazz); 
    				}		
                     
                   
                } catch (Exception e) {  
                    e.printStackTrace();  
                    logger.error("异常",e);  
                    
                }  catch (NoClassDefFoundError e) {  
                    //e.printStackTrace();  
                    logger.error("异常",e);  
                    return;
                }  
                logger.info(packageName + "." + className);  
            }  
        }  
    }  
    /**
     * 产生领域模型的类
     * @author mawujun email:160649888@163.com qq:16064988
     * @param entities
     * @throws IOException
     */
    private void generateM(List<Class> entities,String targetMDir) throws IOException{
    	//生成M
    	File file=new File(targetMDir+File.separatorChar+this.targetPackage.replace('.', File.separatorChar)+File.separatorChar+"M.java");
    	//file.delete();
    	if(!file.exists()){
    		file.createNewFile();
    	}
    	FileWriter fileWrite=new FileWriter(file);
    	
    	    	
    	//StringBuilder builder=new StringBuilder();
    	fileWrite.append("package "+this.targetPackage+";\n");
    	fileWrite.append("public final class M {\n");
    	
    	
    	for(Class clazz:entities){
    		logger.info("============================================="+clazz.getName());

    		fileWrite.append("public static final class "+clazz.getSimpleName()+" {\n");
    		 //Field[]fields = clazz.getDeclaredFields();
    		 List<Field> fields= getClassField(clazz);
    		 
    		 Set<String> existField=new HashSet<String>();
             for (Field field : fields) { //完全等同于上面的for循环
            	 if(!existField.contains(field.getName())){
            		 existField.add(field.getName());
            	 } else {
            		 continue;
            	 }
            	 logger.info(field.getName());
                 //System.out.println(field.getName()+" "+field.getType());
                 //fileWrite.append("public static final "+field.getType().getName()+" "+field.getName()+"=\""+field.getName()+"\";\n");
                 if(isBaseType(field.getType()) || field.getType().isEnum()){
                	 fileWrite.append("	public static final String "+field.getName()+"=\""+field.getName()+"\";\n");
                 } else if(!isOf(field.getType(),Map.class) && !isOf(field.getType(),Collection.class)){
                	 Class<?> fieldClass=field.getType();
                	 Annotation embeddedIdAnnotataion=field.getAnnotation(EmbeddedId.class);
                	 //是复合主键的情况下
                	 if(embeddedIdAnnotataion!=null){
                		 fileWrite.append("	 /**\n");
                    	 fileWrite.append("	 * 返回复合主键的组成，，以对象关联的方式:"+field.getName()+"\n");
                    	 fileWrite.append("	 */\n");
                    	 fileWrite.append("	public static final class "+field.getName()+" {\n");
                    	 //Field[] embeddedIdFields = fieldClass.getDeclaredFields();
                    	 List<Field> embeddedIdFields= getClassField(fieldClass);
                    	 for (Field embeddedIdfield : embeddedIdFields) { 
                    		 fileWrite.append("		public static final String "+embeddedIdfield.getName()+"=\""+field.getName()+"."+embeddedIdfield.getName()+"\";\n");
                    	 }
                    	 fileWrite.append("			\n");
                    	 
                     	 fileWrite.append("	    /**\n");
	                	 fileWrite.append("	    * 返回的是复合主键的属性名称，主要用于属性过滤或以id来查询的时候\n");
	                	 fileWrite.append("	    */\n");
	                	 fileWrite.append("	    public static String name(){ \n");
	                	 fileWrite.append("		    return \""+field.getName()+"\";\n");
	                	 fileWrite.append("	    }\n");
	                	 
                    	 fileWrite.append("	}\n");
                    	 
                    	 
//                    	 fileWrite.append("	/**\n");
//                    	 fileWrite.append("	* 这是一个复合主键，返回的是该复合主键的属性名称，在hql中使用:"+field.getName()+"\n");
//                    	 fileWrite.append("	*/\n");
//                    	 fileWrite.append("	public static final String "+field.getName()+"=\""+field.getName()+"\";\n");
                	 } else {

	                	 //返回关联类的属性，以对象关联的方式
	                	 fileWrite.append("	 /**\n");
                    	 fileWrite.append("	 * 返回关联对象的属性，，以对象关联的方式(a.b这种形式)，只有一些基本属性，层级不再往下了\n");
                    	 fileWrite.append("	 */\n");
                    	 fileWrite.append("	public static final class "+field.getName()+" {\n");
                    	 //Field[] embeddedIdFields = fieldClass.getDeclaredFields();
                    	 List<Field> embeddedIdFields= getClassField(fieldClass);
                    	 for (Field embeddedIdfield : embeddedIdFields) { 
                    		 if(isBaseType(embeddedIdfield.getType()) || embeddedIdfield.getType().isEnum()) {
                    			 fileWrite.append("		public static final String "+embeddedIdfield.getName()+"=\""+field.getName()+"."+embeddedIdfield.getName()+"\";\n");
                    		 }
                    	 }
                    	 //返回该属性的名称
                    	 fileWrite.append("			\n");
                     	 fileWrite.append("	    /**\n");
	                	 fileWrite.append("	    * 返回的是关联类的属性名称，主要用于属性过滤的时候\n");
	                	 fileWrite.append("	    */\n");
	                	 fileWrite.append("	    public static String name(){ \n");
	                	 fileWrite.append("		    return \""+field.getName()+"\";\n");
	                	 fileWrite.append("	    }\n");
	                	 
	                	 
                    	 fileWrite.append("	}\n");
                    	 
	   
                    	        	 
//	                	 fileWrite.append("	/**\n");
//	                	 fileWrite.append("	* 访问关联类的id，用于hql的时候，返回的是"+field.getName()+".id\n");
//	                	 fileWrite.append("	*/\n");
//	                	 fileWrite.append("	public static final String "+field.getName()+"_id=\""+field.getName()+".id\";\n");
//	                	 fileWrite.append("	/**\n");
//	                	 fileWrite.append("	* 返回的是关联类的属性名称，主要用于属性过滤的时候\n");
//	                	 fileWrite.append("	*/\n");
//	                	 fileWrite.append("	public static final String "+field.getName()+"=\""+field.getName()+"\";\n");
                	 }
                 } else {
                	 //其他关联类，例如集合等
                	 fileWrite.append("	/**\n");
                	 fileWrite.append("	* 这里一般是集合属性，返回的是"+field.getName()+"\n");
                	 fileWrite.append("	*/\n");
                	 fileWrite.append("	public static final String "+field.getName()+"=\""+field.getName()+"\";\n");
                 }
                
             }
             fileWrite.append("}\n");
    	}
    	fileWrite.append("}\n");
    	fileWrite.close();
    }
    
    
    /**
     * 产生表的字段名
     * @author mawujun email:160649888@163.com qq:16064988
     * @param entities
     * @throws IOException
     */
    public void generateT(List<Class> entities,String targetMDir) throws IOException{
    	//生成T
    	File file=new File(targetMDir+File.separatorChar+this.targetPackage.replace('.', File.separatorChar)+File.separatorChar+"M.java");
    	//file.delete();
    	if(!file.exists()){
    		file.createNewFile();
    	}
    	FileWriter fileWrite=new FileWriter(file);
    	
    	    	
    	//StringBuilder builder=new StringBuilder();
    	fileWrite.append("package "+this.targetPackage+";\n");
    	fileWrite.append("public final class T {\n");
    	
    	
    	for(Class clazz:entities){
    		

    		Table annoation=(Table)clazz.getAnnotation(annotationTable);
    		if(annoation==null){
    			throw new NullPointerException(clazz.getClass()+"的Table注解没有设置");
    		}
    		logger.info("============================================="+annoation.name());
    		
    		//fileWrite.append("public static final class "+clazz.getSimpleName()+" {\n");
    		fileWrite.append("public static final class "+annoation.name()+" {\n");
    		 //Field[]fields = clazz.getDeclaredFields();
    		 Set<String> existField=new HashSet<String>();
    		 
    		List<Field> fields= getClassField(clazz);
             for (Field field : fields) { //完全等同于上面的for循环
                 //System.out.println(field.getName()+" "+field.getType());
            	 if(!existField.contains(field.getName())){
            		 existField.add(field.getName());
            	 } else {
            		 continue;
            	 }
            	 logger.info(field.getName());
            	
            	 Annotation embeddedIdAnnotataion=field.getAnnotation(EmbeddedId.class);
            	 //是复合主键的情况下
            	 if(embeddedIdAnnotataion!=null){
            		 Class<?> fieldClass=field.getType();
            		 fileWrite.append("	 /**\n");
                	 fileWrite.append("	 * 这个是复合主键。里面的是复合组件的组成列的列名\n");
                	 fileWrite.append("	 */\n");
                	 fileWrite.append("	public static final class "+fieldClass.getSimpleName()+" {\n");
                	 //Field[] embeddedIdFields = fieldClass.getDeclaredFields();
                	 List<Field> embeddedIdFields= getClassField(fieldClass);
                	 for (Field embeddedIdfield : embeddedIdFields) { 
                		 Column columnAnnotation=(Column)embeddedIdfield.getAnnotation(Column.class);
                		 if(columnAnnotation==null || (columnAnnotation!=null && columnAnnotation.name().equals(""))){
            				 fileWrite.append("		public static final String "+embeddedIdfield.getName()+"=\""+embeddedIdfield.getName()+"\";\n");
            			 } else {
            				 fileWrite.append("		public static final String "+columnAnnotation.name()+"=\""+columnAnnotation.name()+"\";\n");
            			 }
                	 }
                	 fileWrite.append("			\n");
                	 fileWrite.append("	}\n");
            	 } else if(isBaseType(field.getType()) || field.getType().isEnum()){
            			 
            			 Column columnAnnotation=(Column)field.getAnnotation(Column.class);
            			 if(columnAnnotation==null || (columnAnnotation!=null && columnAnnotation.name().equals(""))){
            				 fileWrite.append("	public static final String "+field.getName()+"=\""+field.getName()+"\";\n");
            			 } else {
            				 fileWrite.append("	public static final String "+columnAnnotation.name()+"=\""+columnAnnotation.name()+"\";\n");
            			 }
                    	
                 } else if(!isOf(field.getType(),Map.class) && !isOf(field.getType(),Collection.class)){ 
                    	 JoinColumn columnAnnotation=(JoinColumn)field.getAnnotation(Column.class);
                    	 if(columnAnnotation==null || (columnAnnotation!=null && columnAnnotation.name().equals(""))){
                    		 fileWrite.append("	/**\n");
                        	 fileWrite.append("	* 访问外键的列名，用于sql的时候，返回的是"+field.getName()+"_id\n");
                        	 fileWrite.append("	*/\n");
                        	 fileWrite.append("	public static final String "+field.getName()+"_id=\""+field.getName()+"_id\";\n");
                    	 } else {
                    		 fileWrite.append("	/**\n");
                        	 fileWrite.append("	* 访问外键的列名，用于sql的时候，返回的是"+columnAnnotation.name()+"_id\n");
                        	 fileWrite.append("	*/\n");
                        	 fileWrite.append("	public static final String "+columnAnnotation.name()+"=\""+columnAnnotation.name()+"\";\n");
                    	 }
                    	 
                 }     
                
             }
             fileWrite.append("}\n");
    	}
    	fileWrite.append("}\n");
    	fileWrite.close();
    }
    
    /** 
     * 这个方法，是最重要的，关键的实现在这里面 
     *  
     * @param aClazz 
     * @param aFieldName 
     * @return 
     */  
    private List<Field> getClassField(Class aClazz) {  
	    Field[] declaredFields = aClazz.getDeclaredFields();  
	    List<Field> fields=new ArrayList<Field>();
	    
	    for (Field field : declaredFields) {  
	    	if("serialVersionUID".equals(field.getName())){
	    		continue;
	    	}
	    	fields.add(field);
	    }  
	  
	    Class superclass = aClazz.getSuperclass();  
	    
	    if (superclass != null) {// 简单的递归一下  
	       
	        fields.addAll( getClassField(superclass));
	    }  
	    return fields;  
	} 
    public boolean isBaseType(Class clz){
		//如果是基本类型就返回true
		if(clz == UUID.class || clz == URL.class || clz == String.class || clz==Date.class || clz==java.sql.Date.class || clz==java.sql.Timestamp.class || clz.isPrimitive() || isWrapClass(clz)){
			return true;
		}
		return false;
	}
    public boolean isOf(Class<?> orginType,Class<?> type){
    	return type.isAssignableFrom(orginType);
    }
    
    public boolean isWrapClass(Class clz) {
		try {
			return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
		} catch (Exception e) {
			return false;
		}
	}
  
}
