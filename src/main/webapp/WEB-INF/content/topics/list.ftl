<!DOCTYPE html PUBLIC 
    "-//W3C//DTD XHTML 1.1 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <title>Admin</title>
    <#include "/includes/header.ftl"/>
    <#import "/macros/body.ftl" as body />
    </head>
    
<#macro makeSection cnt=0 topicId=-1 category="">
<div class="row">
<div class="col-sm-6">
	<label for="topic${cnt}">Topic:</label>
	<select name="topicIds[]" id="topic${cnt}" class="form-control">
		<option value=""></option>
		<#list flattened as topic>
			<option value="${topic.id?c}" <#if topicId == topic.id>selected</#if>>${topic.name}</option>
		</#list>
	</select>
	</div>
<div class="col-sm-6">
	<label for="category${cnt}">Category:</label>
	<select name="category[]" id="category${cnt}" class="form-control">
		<option value=""></option>
		<#list categories as cat>
			<option value="${cat}" <#if category == cat>selected</#if>>${cat}</option>
		</#list>
	</select>
	</div>
	</div>
</#macro>
    <@body.body>
<h1> Topics</h1>
<p><b>Currently using Map:</b> ${topicMap.name}</p>
<form method="POST" action="/a/topics">
<h3>Apply categories</h3>
<p>for each category selected, for all children apply the selected category... if multiple parents, the closest parent's category will win</p> 
	<#assign cnt = 0>
	<#list categoryAssociations as assoc>
		<@makeSection cnt=cnt topicId=(assoc.topic.id)!-1 category=(assoc.category)!'' />
		<#assign cnt = cnt + 1>
	</#list>
	<@makeSection cnt=cnt />
	<@makeSection cnt=cnt+1 />
	<@makeSection cnt=cnt+2 />
	<@makeSection cnt=cnt+3 />
	<br/>
	<br/>
    <input type="submit" value="Save" class="button btn btn-primary">

</form>
<h3>Update Topic Map</h3>
    <p>Upload a XML Topic Map file (.xtm) file</p>
   <form method="POST" action="${contextPath}/a/admin/topicUploadFile" enctype="multipart/form-data">
        <label for="topicfile" class="control-label">Topic Map File to upload:</label> <input id="topicfile" type="file" name="file">
        <br/>
        <input type="submit" value="Upload" class="button btn btn-primary">
    </form> 

<br/>

    <ul class="nav nav-tabs">
        <li class="active"><a data-toggle="tab" href="#topics">Topics</a></li>
        <li><a data-toggle="tab" href="#associations">Associations between Topics</a></li>
    </ul>
    <div class="tab-content">
        <div id="topics" class="tab-pane fade in active">
        <br><p> <b>Note: somehow not all of the parent relationships are showing</b></p>
            <table class="table">
            <thead>
            <tr>
            <th>id</th>
            <th>display name</th>
            <th>category</th>
            <th>parent</th>
            <th>children</th>
            <th>identifier</th>
            </tr>
            </thead>
            <#list flattened as topic>
            <tr>
                <td>${topic.id?c}</td>
                <td>${topic.name!'NO NAME'}<#if topic.varients?has_content ><i> (${topic.varients?join(", ")})</i></#if> </td>
                <td>${topic.category!''}</td>
                <td> <#if topic.parents?has_content ><#list topic.parents as parent>${parent.name!'NO NAME'} (${parent.id?c})<#sep>; </#sep></#list> <#else><i>[none]</i></#if></td>
                <td> <#if topic.children?has_content ><#list topic.children as parent>${parent.name!'NO NAME'} (${parent.id?c})<#sep>; </#sep></#list> <#else><i>[none]</i></#if></td>
                <td>${topic.identifier}</td>
            </tr>
            </#list>
            </table>

        </div>
        <div id="associations" class="tab-pane fade in ">
            <table class="table">
            <thead>
            <tr>
            <th>id</th>
            <th>from</th>
            <th></th>
            <th>type</th>
            <th></th>
            <th>to</th>
            </tr>
            </thead>
            <#list topicMap.associations as assoc>
            <tr>
                <td>${assoc.id?c}</td>
                <td>${assoc.from.name}</td>
                <td>&#8680;</td>
                <td>${assoc.type.name}</td>
                <td>&#8680;</td>
                <td>${assoc.to.name}</td>
            </tr>
            </#list>
            </table>
        </div>
    </div>

</@body.body>
</html>