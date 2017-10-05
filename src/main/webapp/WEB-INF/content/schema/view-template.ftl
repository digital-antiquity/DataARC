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
 var feature = {};
 
 
    var FIELDS = {
    <#list schema.fields as field>"${field.name}": "${field.displayName}"<#sep>,
</#sep></#list>
    };

    Handlebars.registerHelper("fieldName", function(name) {
    if (name != undefined) {
        if (FIELDS[name.trim()] != undefined) {
            return FIELDS[name.trim()];
        } 
        return name;
      }
      return "";
    });


 
 $(document).ready(function() {
    $("textarea.form-control").focus(function(e) {
        focusArea = $(e.target);
    });
 
     var url = "/api/getId?schemaId=${schema.id?c}";
     $.getJSON( url, function( data ) {
         feature = data.results.features[0];
         console.log(feature);
     });

 
 
     $("#renderTitle").click(function(e) {
        render("#titleTemplate","#titleTarget");
     });
     $("#renderResult").click(function(e) {
        render("#resultTemplate","#resultTarget");
     });
     $("#renderLink").click(function(e) {
        render("#linkTemplate","#linkTarget");
     });
     
 });
 
 function render(templateName, targetName) {
     var handlebarHandler = $(templateName);
     console.log(feature.properties);
     console.log(handlebarHandler.val());
     var template = Handlebars.compile(handlebarHandler.val());
     var content = template(feature.properties);
     $(targetName).empty().append(content);
}
 </script>

 	 	<#list schema.fields as field>
 	<span class="label label-default" title="${field.id?c}" onClick="$('#t').html('{{${schema.name}${field.name}}}');$('#t').select();document.execCommand('copy');focusArea.focus();">${field.name}</span>
	</#list>
	<br/>
	<a href="http://handlebarsjs.com">Handlebars documentation</a><br/>
    <form method="POST" action="${contextPath}/a/admin/schema/template/${schema.id?c}" enctype="multipart/form-data" class="">
		<input type="hidden" name="id" value="${schema.id?c}" />
		<div class="form-group col-6-sm">
	        <label for="titleTemplate" class="control-label">Title Template:</label>
	        <textarea id="titleTemplate" name="titleTemplate" class="form-control">${titleTemplate!''}</textarea>
		</div>
		<button name="render" type="button" id="renderTitle">Render</button>
		<div class="col-12-sm well" id="titleTarget">
		
		</div>
        <div class="form-group col-12-sm">
	        <label for="resultTemplate" class="control-label">Results Template:</label>
	        <textarea id="resultTemplate" name="resultTemplate" class="form-control">${resultTemplate!''}</textarea>

        </div>
        <button name="render" type="button" id="renderResult">Render</button>
        <div class="col-12-sm well" id="resultTarget">
        
		
		</div>
        <div class="form-group col-12-sm">
	        <label for="linkTemplate" class="control-label">Link Template:</label>
	        <textarea id="linkTemplate" name="linkTemplate" class="form-control">${linkTemplate!''}</textarea>
        </div>
        <button name="render" type="button" id="renderLink">Render</button>
        <div class="col-12-sm well" id="linkTarget">
        

		</div>
        <input type="submit" value="Save" class="button btn btn-primary"> 

    </form>
    </div>

</@body.body>
</html>