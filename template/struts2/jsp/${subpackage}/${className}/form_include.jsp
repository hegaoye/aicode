<%@page import="${basepackage}.${subpackage}.model.*" %>
<#include "macro.include"/> 
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/commons/taglibs.jsp" %>

<#list table.columns as column>
<#if column.htmlHidden>
	<s:hidden id="${column.columnNameLower}" name="${column.columnNameLower}" ></s:hidden>
</#if>
</#list>

<#list table.columns as column>
	<#if !column.htmlHidden>
	
	<tr>	
		<th><%=${className}.ALIAS_${column.sqlName?upper_case}%>&nbsp;</th>	
		<td>
	<#if column.isDateTimeColumn>
		<s:textfield key="${column.columnNameLower}String" value="%{model.${column.columnNameLower}String}" onclick="<%="WdatePicker({dateFmt:'"+${className}.FORMAT_${column.sqlName?upper_case}+"'})"%>" cssClass="${column.validateString}" theme="simple"></s:textfield>
	<#else>
		<s:textfield key="${column.columnNameLower}" value="%{model.${column.columnNameLower}}" cssClass="${column.validateString}" theme="simple" ></s:textfield>
	</#if>
		</td>
	</tr>
	</#if>
</#list>