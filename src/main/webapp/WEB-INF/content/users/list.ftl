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
<h1> Users</h1>
<table class="table">
<thead>
<tr>
<td>id</td>
<td>display name</td>
<td>email</td>
<td>editor?</td>
<td>admin?</td>
</tr>
</thead>
<#list users as user>
	<tr>
		<td>${user.id?c}</td>
		<td>${user.firstName} ${user.lastName}</td>
		<td>${user.email}</td>
		<td>${user.editor?c}</td>
		<td>${user.admin?c}</td>
	</tr>
</#list>
</@body.body>
</html>