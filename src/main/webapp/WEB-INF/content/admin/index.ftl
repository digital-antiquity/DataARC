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
<h1> Admin functions</h1>

<h2>Reindex</h2>
<p>Reindex all of the source and indicator data for the end-user interface</p>
<form action="${contextPath}/api/admin/reindex" method="POST">
<input type="submit" name="reindex" value="reindex" class="button btn btn-primary"/>
</form>

<h2>Add New Data Source</h2>
<p>Create new data source (requires a GeoJSON file)</p>
<form method="POST" action="${contextPath}/a/admin/uploadSourceFile" enctype="multipart/form-data">
    <div class="row">
    <div class="col-sm-6">
            <label for="sourcefileName" class="control-label">Short Name:</label>
             <input type="text" name="name" id="sourcefileName" class="form-control">
    </div>
    <div class="col-sm-6">
        <label for="sourcefile" class="control-label">File to upload:</label> <input id="sourcefile" type="file" name="file">
    </div>
    </div>
        <input type="submit" value="Upload" class="button btn btn-primary">
    </form> 

<h2>Update Topic Map</h2>
    <p>Upload a XML Topic Map file (.xtm) file</p>
   <form method="POST" action="${contextPath}/a/admin/topicUploadFile" enctype="multipart/form-data">
        <label for="topicfile" class="control-label">Topic Map File to upload:</label> <input id="topicfile" type="file" name="file">
        <br/>
        <input type="submit" value="Upload" class="button btn btn-primary">
    </form> 

</br>
</br>
<h2>Delete Everything</h2>
   <form method="POST" action="${contextPath}/a/admin/resetData" enctype="multipart/form-data">
        <input type="submit" value="DELETE!" class="button btn btn-danger"> 
    </form> 

</@body.body>
</html>