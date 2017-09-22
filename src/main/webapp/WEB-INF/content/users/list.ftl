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
<td>role</td>
</tr>
</thead>
<#list users as user>
	<tr>
		<td>${user.id?c}</td>
		<td>${user.firstName} ${user.lastName}</td>
		<td>${user.email}</td>
		<td> 
	    <form method="POST" action="${contextPath}/a/users/${user.id}" enctype="multipart/form-data" class="form-horizontal">
	      <div class="input-group">
	  <select name=role class="form-control">
		      <option name='USER' <#if (!user.editor && !user.admin)>selected</#if>>User</option>
		      <option name='EDITOR' <#if user.editor>selected</#if>>Editor</option>
		      <option name='ADMIN' <#if user.admin>selected</#if>>Admin</option>
		  </select>
         <span class="input-group-btn">
            <input type="submit" value="Save" class="button btn btn-primary"> 
            </span>
            </div>
		  </form>
		  </td>
	</tr>
</#list>
</@body.body>
</html>