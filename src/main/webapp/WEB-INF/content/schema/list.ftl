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
<h1>Data Sources</h1>
<table class="table">
<thead>
<tr>
<th>id</th>
<th>name</th>
<th>display name</th>
<th>description</th>
<th>url</th>
<th>category</th>
<th># of entries</th>
</tr>
</thead>
<#list schema as schemum>
	<tr>
		<td>${schemum.id?c}</td>
		<td>${schemum.name}</td>
		<td>${schemum.displayName}</td>
		<td>${schemum.description!''}</td>
		<td>${schemum.url!''}</td>
		<td>${schemum.category!''}</td>
		<td>${(schemum.rows!0)?c}</td>
		<td><a class="button btn btn-primary" href="/a/schema/${schemum.id?c}">edit</a></td>
	</tr>
</#list>
</@body.body>
</html>