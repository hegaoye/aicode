<%@page import="${basepackage}.${subpackage}.model.*" %>
<#include "macro.include"/> 
<#include "custom.include"/> 
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first> 
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/commons/taglibs.jsp" %>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<html>

<head>
	<%@ include file="/commons/meta.jsp" %>
	<base href="<%=basePath%>">
	<title><%=${className}.TABLE_ALIAS%>信息</title>
</head>

<body>
<%@ include file="/commons/messages.jsp" %>

<s:form action="${strutsActionBasePath}/edit.action" method="get" theme="simple">
	<input type="submit" value="编辑"/>
	<input type="button" value="返回列表" onclick="window.location='<@jspEl 'ctx'/>${strutsActionBasePath}/list.action'"/>

<#list table.columns as column>
<#if column.pk>
	<s:hidden name="${column.columnNameLower}" id="${column.columnNameLower}" value="%{model.${column.columnNameLower}}"/>
</#if>
</#list>

	<table class="formTable">
	<#list table.columns as column>
	<#if !column.htmlHidden>
		<tr>	
			<th><%=${className}.ALIAS_${column.sqlName?upper_case}%>&nbsp;</th>	
			<td><#rt>
			<#compress>
			<#if column.isDateTimeColumn>
			<s:label value="%{model.${column.columnNameLower}String}" theme="simple"></s:label>
			<#else>
			<s:label value="%{model.${column.columnNameLower}}" theme="simple"></s:label>
			</#if>
			</#compress>
			<#lt></td>
		</tr>
	</#if>
	</#list>
	</table>
</s:form>

</body>

</html>