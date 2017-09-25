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
<h1> Topics</h1>
${topicMap}
<table class="table">
<thead>
<tr>
<td>id</td>
<td>display name</td>
<td>email</td>
<td>role</td>
</tr>
</thead>
<#list flattened as topic>
<tr>
	<td>${topic.id?c}</td>
	<td>${topic.name!''} </td>
	<td><#if topic.parents?has_content >${topic.parents} </#if></td>
</tr>
</#list>
</@body.body>
</html>