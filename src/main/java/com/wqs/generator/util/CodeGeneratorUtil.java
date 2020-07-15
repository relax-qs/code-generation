package com.wqs.generator.util;

import com.wqs.generator.freemarker.FreeMarkerTemplateUtils;
import com.wqs.generator.model.ColumnClass;
import com.wqs.generator.model.GeneratorBean;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;

public class CodeGeneratorUtil {
    // 作者
    private String AUTHOR;
    // 创建日期
    private String CURRENT_DATE;
    // 包名
    private String packageName;
    // 生成文件的根目录
    private String diskPath;
    // 默认false  不忽略第一个"_"前的单词
    private boolean cutFistUnderLine;

    public Connection getConnection() throws Exception {
        InputStream ins = CodeGeneratorUtil.class.getResourceAsStream("/jdbc.properties");
        Properties props = new Properties();
        props.load(ins);
        Class.forName(props.getProperty("jdbc.driver"));
        Connection connection = DriverManager.getConnection(props.getProperty("jdbc.url"), props.getProperty("jdbc.username"), props.getProperty("jdbc.password"));
        return connection;
    }

    public void init(String diskPath, String packageName, String author, boolean cutFistUnderLine) {
        this.diskPath = diskPath;
        this.packageName = packageName;
        this.AUTHOR = author;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        this.CURRENT_DATE = sdf.format(new Date());
        this.cutFistUnderLine = cutFistUnderLine;
    }


    /**
     * 获取数据库下的所有表名
     */
    public List<String> getTableNames(Connection conn) throws Exception {
        List<String> tableNames = new ArrayList<String>();
        ResultSet rs = null;
        //获取数据库的元数据
        DatabaseMetaData db = conn.getMetaData();
        //从元数据中获取到所有的表名
        rs = db.getTables(null, null, null, new String[]{"TABLE"});
        while (rs.next()) {
            String tableName = rs.getString(3);
            tableNames.add(tableName);
        }
        return tableNames;
    }

    /**
     * 获取数据库下的所有表名
     */
    public List<ColumnClass> getColumnList(ResultSet columnResultSet) throws Exception {
        List<ColumnClass> columnList = new ArrayList<ColumnClass>();
        ColumnClass columnClass = null;
        while (columnResultSet.next()) {
            columnClass = new ColumnClass();
            // 字段名
            String columnName = columnResultSet.getString("COLUMN_NAME");
            // 数据类型名
            String dataTypeName = columnResultSet.getString("TYPE_NAME");
            // 字段描述
            String comment = columnResultSet.getString("REMARKS");
            columnClass.setColumnName(columnName);
            columnClass.setColumnType(toJdbcType(dataTypeName));
            columnClass.setJavaType(toJavaType(dataTypeName));
            columnClass.setColumnComment(comment);
            columnClass.setChangeColumnName(replaceUnderLineAndUpperCase(columnName, cutFistUnderLine));
            columnList.add(columnClass);
        }
        return columnList;
    }

    public void generate() throws Exception {
        Connection connet = getConnection();
        DatabaseMetaData metaData = connet.getMetaData();
        // 获取schema 下的所有表
        List<String> tableNames = getTableNames(connet);
        GeneratorBean bean = null;
        for (String tableName : tableNames) {
            bean = new GeneratorBean();
            bean.setTabName(tableName);
            bean.setBeanName(replaceUnderLineAndUpperCase(tableName, cutFistUnderLine));
            bean.setPackageName(packageName);
            // 表的列信息
            ResultSet columnResultSet = metaData.getColumns(null, "%", tableName, "%");
            List<ColumnClass> columnClassList = this.getColumnList(columnResultSet);
            // 生成mapper文件
            this.generateMapperFile(bean, columnClassList);
            // 生成dao文件
            this.generateDaoFile(bean, columnClassList);
            // 生成entity文件
            this.generateEntityFile(bean, columnClassList);
            // 生成dto文件
            this.generateDTOFile(bean, columnClassList);
            columnResultSet.close();
        }

    }

    /**
     * 生成dto文件
     *
     * @param bean
     * @param columnClassList
     */
    private void generateDTOFile(GeneratorBean bean, List<ColumnClass> columnClassList) throws IOException, TemplateException {
        final String suffix = "Vo.java";
        final String fullPackage = bean.getPackageName()+".api.vo";
        final String path = diskPath+"src/"+fullPackage.replaceAll("\\.", "/")+"/" + bean.getBeanName() + suffix;
        final String templateName = "VO.ftl";
        File mapperFile = new File(path);
        Map<String,Object> dataMap = new HashMap<String,Object>();
        Map<String,ColumnClass> columnMap = new HashMap<String, ColumnClass>();
        for(ColumnClass column:columnClassList){
            columnMap.put(column.getColumnName(), column);
        }
        dataMap.put("model_column",new ArrayList<ColumnClass>(columnMap.values()));
        bean.setTemplateName(templateName);
        generateFileByTemplate(bean,mapperFile,dataMap);
    }

    /**
     * 生成entity文件
     *
     * @param bean
     * @param columnClassList
     */
    private void generateEntityFile(GeneratorBean bean, List<ColumnClass> columnClassList) throws IOException, TemplateException {
        final String suffix = ".java";
        final String fullPackage = bean.getPackageName()+".service.entity";
        final String path = diskPath+"src/"+fullPackage.replaceAll("\\.", "/")+"/" + bean.getBeanName() + suffix;
        final String templateName = "Model.ftl";
        File mapperFile = new File(path);
        Map<String,Object> dataMap = new HashMap<String,Object>();
        Map<String,ColumnClass> columnMap = new HashMap<String, ColumnClass>();
        for(ColumnClass column:columnClassList){
            columnMap.put(column.getColumnName(), column);
        }
        dataMap.put("model_column",new ArrayList<ColumnClass>(columnMap.values()));
        bean.setTemplateName(templateName);
        generateFileByTemplate(bean,mapperFile,dataMap);
    }

    /**
     * 生成dao文件
     *
     * @param bean
     * @param columnClassList
     */
    private void generateDaoFile(GeneratorBean bean, List<ColumnClass> columnClassList) throws IOException, TemplateException {
        final String suffix = "Mapper.java";
        final String fullPackage = bean.getPackageName()+".service.mapper";
        final String path = diskPath+"src/"+fullPackage.replaceAll("\\.", "/")+"/" + bean.getBeanName() + suffix;
        final String templateName = "DAO.ftl";
        File mapperFile = new File(path);
        Map<String,Object> dataMap = new HashMap<String,Object>();
        bean.setTemplateName(templateName);
        this.generateFileByTemplate(bean,mapperFile,dataMap);
    }

    /**
     * 生成mapper文件
     *
     * @param bean
     * @param columnClassList
     */
    private void generateMapperFile(GeneratorBean bean, List<ColumnClass> columnClassList) throws IOException, TemplateException {
        final String suffix = "Mapper-mysql.xml";
        final String path = diskPath + "/resources/sqlMap/" + bean.getBeanName() + suffix;
        final String templateName = "Mapper.ftl";
        File mapperFile = new File(path);
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("columnClassList", columnClassList);
        bean.setTemplateName(templateName);
        // 模版替换
        this.generateFileByTemplate(bean, mapperFile, dataMap);
    }

    /**
     * 模版中的数据替换
     *
     * @param bean
     * @param file
     * @param dataMap
     */
    private void generateFileByTemplate(GeneratorBean bean, File file, Map<String, Object> dataMap) throws IOException, TemplateException {
        Template template = FreeMarkerTemplateUtils.getTemplate(bean.getTemplateName());
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(file);
        dataMap.put("table_name_small", bean.getTabName());
        dataMap.put("table_name", bean.getBeanName());
        dataMap.put("author", AUTHOR);
        dataMap.put("date", CURRENT_DATE);
        dataMap.put("package_name", bean.getPackageName());
        dataMap.put("table_annotation", bean.getTabCommnet());
        Writer out = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"), 10240);
        template.process(dataMap, out);
    }


    public String replaceUnderLineAndUpperCase(String str, boolean cutLine) {
        StringBuffer sb = new StringBuffer();
        if (cutLine) {
            sb.append(str.substring(str.indexOf("_") + 1));
        } else {
            sb.append(str);
        }
        int count = sb.indexOf("_");
        while (count > 0) {
            int num = sb.indexOf("_", count);
            count = num + 1;
            if (num != -1 && count < sb.length()) {
                char ss = sb.charAt(count);
                sb.replace(count, count + 1, Character.toString(ss).toUpperCase());
            }
        }

        String result = sb.toString().replaceAll("_", "");
        if (!Character.isLowerCase(result.charAt(1))) {
            return result;
        }
        return StringUtils.capitalize(result);
    }

    public static String toJavaType(String resultType) {
        if ("varchar".equalsIgnoreCase(resultType)) {
            return "String";
        } else if ("bit".equalsIgnoreCase(resultType)) {
            return "Boolean";
        } else if ("dateTime".equalsIgnoreCase(resultType)) {
            return "Date";
        } else if ("int".equalsIgnoreCase(resultType)) {
            return "Integer";
        } else if ("bigint".equalsIgnoreCase(resultType)) {
            return "Long";
        } else if ("decimal".equalsIgnoreCase(resultType)) {
            return "BigDecimal";
        } else {
            return "String";
        }
    }

    public static String toJdbcType(String resultType) {
        if ("varchar".equalsIgnoreCase(resultType)) {
            return "VARCHAR";
        } else if ("bit".equalsIgnoreCase(resultType)) {
            return "BIT";
        } else if ("dateTime".equalsIgnoreCase(resultType)) {
            return "TIMESTAMP";
        } else if ("int".equalsIgnoreCase(resultType)) {
            return "INTEGER";
        } else if ("bigint".equalsIgnoreCase(resultType)) {
            return "BIGINT";
        } else if ("decimal".equalsIgnoreCase(resultType)) {
            return "DECIMAL";
        } else {
            return "VARCHAR";
        }
    }


    public String getAUTHOR() {
        return AUTHOR;
    }

    public void setAUTHOR(String AUTHOR) {
        this.AUTHOR = AUTHOR;
    }

    public String getCURRENT_DATE() {
        return CURRENT_DATE;
    }

    public void setCURRENT_DATE(String CURRENT_DATE) {
        this.CURRENT_DATE = CURRENT_DATE;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDiskPath() {
        return diskPath;
    }

    public void setDiskPath(String diskPath) {
        this.diskPath = diskPath;
    }

    public boolean isCutFistUnderLine() {
        return cutFistUnderLine;
    }

    public void setCutFistUnderLine(boolean cutFistUnderLine) {
        this.cutFistUnderLine = cutFistUnderLine;
    }
}
