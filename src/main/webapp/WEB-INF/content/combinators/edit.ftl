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
<h1> Edit Combinator: ${indicator.name!'no name?'}</h1>
<form method="POST" action="/a/combinators/${indicator.id?c}" enctype="multipart/form-data">
    <textarea name="combinator" class="form-control" rows=10>${combinator!''}</textarea>
    <br/>
<button name='submit' value='submit'>search</button>
    </form> 

</@body.body>
</html>