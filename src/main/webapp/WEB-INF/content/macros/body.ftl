<#macro body>

    <body data-contextPath="${contextPath}">
        <div class="container-fluid">
            <div class="row">
                <#include "/includes/sidebar.ftl"/>
                <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main ">
                    <#nested />
                </div>
            </div>
        </div>
    <#include "/includes/footer.ftl"/>

        </body>


</#macro>