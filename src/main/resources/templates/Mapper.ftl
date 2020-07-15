<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${package_name}.service.mapper.${table_name}Mapper">
	
    <resultMap type="${package_name}.service.entity.${table_name}" id="restMap">
		<#if columnClassList?exists>
			<#list columnClassList as model>
			 	<#if model.columnName == "id">
		<id property="${model.columnName}" column="${model.changeColumnName?uncap_first}" jdbcType="${model.columnType}"/>
				<#else>
		<result column="${model.columnName}" property="${model.changeColumnName?uncap_first}" jdbcType="${model.columnType}"/>						 	
			 	</#if>
			</#list>
		</#if>
	</resultMap>
	

  <sql id="columnList" >
	<#if columnClassList?exists>
		<#list columnClassList as model>
			<#if model_index==0>
		a.${model.columnName}
			<#else>
		,a.${model.columnName}
			</#if>
		</#list>
	</#if>  
  </sql>
</mapper>