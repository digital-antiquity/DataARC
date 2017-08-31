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
<h1> Login </h1>
<h3>Login with Username and Password</h3><form name='f' action='/login' method='POST'>
<#if param??>
${param}
</#if>
<#if param?? && param.error??>
      <div class="alert alert-error">    
        Invalid username and password.
      </div>
</#if>
<#if param?? && param.logout??>
    <div  class="alert alert-success"> 
        You have been logged out.
    </div>
</#if>
<table>
    <tr><td>User:</td><td><input type='text' name='username' value=''></td></tr>
    <tr><td>Password:</td><td><input type='password' name='password'/></td></tr>
    <tr><td colspan='2'><input name="submit" type="submit" value="Login"/></td></tr>
</table>
<a href="/google-login">Login with Google</a> | <a href="/facebook-login">Login with Facebook</a>
</@body.body>
</html>