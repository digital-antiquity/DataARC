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

        <h1>Add Datafile</h1>
            <form method="POST" action="uploadFile" enctype="multipart/form-data">

        File to upload: <input type="file" name="file">
 
        Name: <input type="text" name="name">
 
        <input type="submit" value="Upload"> Press here to upload the file!
    </form> 
    
    </@body.body>
</html>