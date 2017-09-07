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
<h1>Editing: ${schema.name}</h1>

<ul>
<li>upload new data file</li>
<li>change display name</li>
<li>description</li>
<li>url</li>
</ul>
<table class="table">
<#list schema.fields as field>
	<tr>
	<td>${field.id?c}</td>
	<td>${field.name}</td>
	<td>${field.displayName}</td>
	<td>${field.type}</td>
	<td>edit display name</td>
	</tr>
</#list>
</table>

delete | save
</@body.body>
</html>