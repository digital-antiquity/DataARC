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
<h1>Editing: ${schema.name} Templates</h1>
 <div class="col-sm-12">
 <p><b>Field Names</b></p>
 <style>
 #t {width:1px;height:1px;border:none;}
 #t:focus{outline:none;}</style>
 <textarea id=t></textarea>
 
 <script>
 var focusArea = undefined;
 $(document).ready(function() {
 $("textarea.form-control").focus(function(e) {
    focusArea = $(e.target);
 });
 });
 </script>

 	 	<#list schema.fields as field>
 	<span class="label label-default" onClick="$('#t').html('{{${schema.name}_${field.name}}}');$('#t').select();document.execCommand('copy');focusArea.focus();">${schema.name}_${field.name}</span>
	</#list>
	<br/>
	<a href="http://handlebarsjs.com">Handlebars documentation</a><br/>
    <form method="POST" action="${contextPath}/a/admin/schema/template/${schema.id?c}" enctype="multipart/form-data" class="form-horizontal">
		<input type="hidden" name="id" value="${schema.id?c}" />
		<div class="form-group">
	        <label for="titleTemplate" class="control-label">Title Template:</label>
	        <textarea id="titleTemplate" name="titleTemplate" class="form-control">${titleTemplate!''}</textarea>
		</div>
		<div class="form-group">
	        <label for="resultTemplate" class="control-label">Results Template:</label>
	        <textarea id="resultTemplate" name="resultTemplate" class="form-control">${resultTemplate!''}</textarea>
		</div>
		<div class="form-group">
	        <label for="linkTemplate" class="control-label">Link Template:</label>
	        <textarea id="linkTemplate" name="linkTemplate" class="form-control">${linkTemplate!''}</textarea>
		</div>
        <input type="submit" value="Save" class="button btn btn-primary"> 

    </form>
    </div>

</@body.body>
</html>