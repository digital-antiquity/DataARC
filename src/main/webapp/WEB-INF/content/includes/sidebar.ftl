<div class="col-sm-3 col-md-2 sidebar">
    <a href="/"><img src="${contextPath}/images/dataarc_logo_final.png" alt="DataARC Logo" class="img-responsive"/></a>
    <hr>
    <br>
    <#if authenticated>
    <#assign aDateTime = .now>
    <#assign mod = aDateTime?string("ss")?number % 3 />

<b>
    <#if mod == 0>Hello
    <#elseif mod == 1>
    Hall&oacute;
    <#elseif mod == 2>
    Hall&aring;
    </#if>
 
   ${currentUserDisplayName!'none' }</b>
    <ul class="nav nav-sidebar">
    <#if editor>
        <li><a href="/a/schema/">Data Sources</a></li>
        </ul>
        <p>Combinators</p>
        <ul class="nav nav-sidebar">
            <ul>
                <li><a href="/a/mapping/">Create / Edit</a></li>
                <li><a href="/a/combinators">Preview</a></li>
            </ul>
        </ul>
        <ul class="nav nav-sidebar">
        <#if admin>
        <li><a href="/a/admin">Admin</a><ul>
            <li><a href="/a/geojson">GeoJson Files</a></li> 
            <li><a href="/a/coverage">Temporal Coverage</a></li> 
            <li><a href="/a/users/list">Users</a></li> 
            <li><a href="/a/searches/list">Searches</a></li> 
            <li><a href="/a/topics">Topics</a></li> 
        </ul></li>
        </#if>
        </#if>
        <li><a href="/logout">Log Out</a></li>
    </ul>
    </#if>
</div>