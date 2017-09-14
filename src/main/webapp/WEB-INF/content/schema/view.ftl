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
 <div class="col-sm-12">
    <form method="POST" action="${contextPath}/a/schema/${schema.name}" enctype="multipart/form-data" class="form-horizontal">
		<input type="hidden" name="id" value="${schema.id?c}" />
		<div class="form-group">
	        <label for="schemaName" class="control-label">Name:</label>
	        <input type="text" id="schemaName" name="displayName" value="${schema.displayName}" class="form-control"/>
		</div>
		<div class="form-group">
	        <label for="schemaUrl" class="control-label">Url:</label>
	        <input type="text" id="schemaUrl" name="url" value="${schema.url!''}" class="form-control"/>
		</div>
		<div class="form-group">
	        <label for="schemaCategory" class="control-label">Category:</label>
	        
	        <select id="schemaCategory" name="category" class="form-control">
	           <#list categories as category>
	               <option value="${category}" <#if category == schema.category!''>selected</#if>>${category}</option>
	           </#list>
	        </select>
		</div>
		<div class="form-group">
	        <label for="schemaDescription" class="control-label">Description:</label>
	        <textarea id="schemaDescription" name="description" class="form-control">
	        ${schema.description!''}
	        </textarea>
		</div>
        <input type="submit" value="Save" class="button btn btn-primary"> 

    </form>
    </div>
     <div class="col-sm-12">

    <form method="POST" action="${contextPath}/a/admin/uploadSourceFile" enctype="multipart/form-data">
        <h3>Update Data (GeoJSON)</h3>
         <input type="file" name="file">
      <input type="hidden" name="name" value="${schema.name}"/>
      <br/>
        <input type="submit" value="Upload" class="button btn btn-primary"> 
      <br/>
    </form> 
</div>
 <div class="col-sm-12">

<table class="table">
<#list schema.fields as field>
	<tr>
	<td>${field.id?c}</td>
	<td>${field.name}</td>
	<td>
	   <div class="input-group">
         <input type="text" value="${field.displayName}" class="form-control"/>
         <span class="input-group-btn">
           <button class="btn btn-default" type="button">Save</button>
          </span>
     </div>
	</td>
	<td>${field.type}</td>
	</tr>
</#list>
</table>
</div>
<form method="POST" action="${contextPath}/a/schema/delete/${schema.name}" enctype="multipart/form-data" class="form-horizontal">
    <button name="delete" class="button btn btn-danger" value="Delete">Delete Data Source</button>
</form>
</@body.body>
</html>