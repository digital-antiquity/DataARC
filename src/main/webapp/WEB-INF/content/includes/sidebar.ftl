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
        <li><a href="/mapping/">Indicators</a></li>
        <#if admin>
        <li><a href="/admin">Admin</a><ul>
            <li><a href="/admin">Reindex</a></li>
            <li><a href="/admin/source">Add Datafile</a></li>
        </ul></li>
        </#if>
    </ul>
    </#if>
</div>