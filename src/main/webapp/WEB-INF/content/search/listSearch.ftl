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
<h1>Searches</h1>
<table class="table">
<thead>
<tr>
<td>id</td>
<td>title</td>
<td>owner</td>
<td>link</td>
</tr>
</thead>
<#list searches as search>
    <tr>
        <td>${search.id?c}</td>
        <td><a href="/?id=${search.id?c}">${search.title}</a></td>
        <td>${search.user.firstName}  ${search.user.lastName}</td>
        <td>${(search.views!0)?c}</td>
<!--        <td>${search.data}</td> -->
    </tr>
</#list>
</@body.body>
</html>