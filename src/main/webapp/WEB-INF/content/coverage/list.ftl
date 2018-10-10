<!DOCTYPE html PUBLIC 
    "-//W3C//DTD XHTML 1.1 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <title>Admin</title>
    <#include "/includes/header.ftl"/>
    <#import "/macros/body.ftl" as body />
    </head>
    
    <@body.body>
<h1>Mapped Temporal Ranges</h1>
<table class="table">
<thead>
<tr>
<th>id</th>
<th>name</th>
<th>start</th>
<th>end</th>
</tr>
</thead>
<#list coverage as temp>
	<tr>
		<td>${temp.id?c}</td>
		<td>${temp.term}</td>
		<td><#if temp.start?has_content>${temp.start?c!''}</#if></td>
		<td><#if temp.end?has_content>${temp.end?c!''}</#if></td>
	</tr>
</#list>
</table>


<h2>Existing Values From our Data Sources</h2>
<table class="table">
<thead>
<tr>
<th>Data Source</th>
<th>Value</th>
<th>Ocurrence</th>
</tr>
</thead>
<#list values as schema, valueList>
    <#list valueList as value>
<tr>
<td>${schema.name}</td>
<td>${value.value}</td>  
<td>${value.occurrence}</td>  
</tr>
    </#list>
</#list>
</table>

</@body.body>
</html>