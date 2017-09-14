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
<h1> GeoJson Files</h1>

    <h2>Add GeoJson File</h2>
        <form method="POST" action="/a/admin/uploadGeoJsonFile" enctype="multipart/form-data">
        File to upload: <input type="file" name="file">
        <input type="submit" value="Upload"> 
    </form> 
<br/>
    <h2>All GeoJson Files</h2>
<table class="table">
<thead>
<tr>
<td>id</td>
<td>name</td>
<td>display name</td>
</tr>
</thead>
<#list files as geo>
	<tr>
		<td>${geo.id?c}</td>
		<td>${geo.name}</td>
		<td>${geo.displayName}</td>
	</tr>
</#list>
</@body.body>
</html>