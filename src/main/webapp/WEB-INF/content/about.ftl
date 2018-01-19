<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="utf-8">

    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="description" content="">
  <meta name="author" content="">
  <title>About DataARC - Linking Data from Archaeology, the Sagas, and Climate</title>
	<#include "includes/public-header.ftl">

  <style type="text/css">
    section.masthead {
      padding:8% 0 14% 0;
      color:#222222;
      background-image: url("../img/about_header.jpg");
      background-position: center;
      -webkit-background-size: cover;
      -moz-background-size: cover;
      -o-background-size: cover;
      background-size: cover;
    }
    div.panel {
      padding-bottom:20px;
    }
    a.panel-link, a.panel-link:hover, a.panel-link:focus {
      color:inherit;
      text-decoration: none;
    }
    a.panel-link.active .fa-plus, a.panel-link .fa-minus {
      display:none;
    }
    a.panel-link .fa-plus, a.panel-link.active .fa-minus {
      display:inline;
    }
    ul.list-group {
      border-radius: 0px;
    }
    li.list-group-item, li.list-group-item:first-child, li.list-group-item:last-child {
      background-color:inherit;
      border-radius: 0px;
      border-left:0px;
      border-right:0px;
      border-top:0px;
      padding:0.5em;
      border-color: #e1eaed;
    }
    li.list-group-item:last-child {
      border-bottom:0px;
    }
  </style>
    
</head>

<body style="padding-top:50px;">
  <nav class="navbar navbar-expand-lg navbar-light fixed-top navbar-shrink" id="mainNav">
    <div class="container">
      <a class="navbar-brand js-scroll-trigger" href="#page-top">DataARC</a>
      <button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarResponsive">
        <ul class="navbar-nav ml-auto">
          <li class="nav-item">
            <a class="nav-link js-scroll-trigger" href="#page-top">Start</a>
          </li>
          <li class="nav-item">
            <a class="nav-link js-scroll-trigger" href="#explore-section">Explore</a>
          </li>
          <li class="nav-item">
            <a class="nav-link js-scroll-trigger" href="#timeline-section">Timeline</a>
          </li>
          <li class="nav-item">
            <a class="nav-link js-scroll-trigger" href="#map-section">Map</a>
          </li>
          <li class="nav-item">
            <a class="nav-link js-scroll-trigger" href="#concept-section">Concept</a>
          </li>
          <li class="nav-item">
            <a class="nav-link js-scroll-trigger" href="#results-section">Results <span id="results-count" class="badge badge-dark"></span></a>
          </li>
          <li class="nav-item">
            <a class="nav-link js-scroll-trigger" href="#why-section">Why</a>
          </li>
        </ul>
      </div>
    </div>
  </nav>
  <section id="about-section" class="masthead">
    <div class="container">
      <div class="row">
        <div class="col-lg-8 mx-auto text-center">
          <h2 class="section-heading">About DataARC</h2>
          <hr>
          <p class="mb-5">A brief description of the project should go here...</p>
          <a class="btn btn-dark btn-xl js-scroll-trigger" href="#contributors-section"><i class="fa fa-database text-white sr-icons"></i> Data Contributors</a>
          <a class="btn btn-dark btn-xl js-scroll-trigger" href="#rd-section"><i class="fa fa-edit text-white sr-icons"></i> Research &amp; Development</a>
          <a class="btn btn-dark btn-xl js-scroll-trigger" href="#footer-section"><i class="fa fa-dollar text-white sr-icons"></i> Funding</a>
        </div>
      </div>
    </div>
  </section>

  <section id="contributors-section" style="background-color:#182235;color:#e1eaed;">
    <div class="container">
      <div class="row">
        <div class="col-lg-11 mx-auto">
          <h2 class="section-heading text-center">Data Contributors</h2>
          <hr>
          <div class="row mt-5">
            <div class="col-md-8">
              <div class="panel-group" id="accordion">
              
            <#list schema as scheme>
            <#if true>
                <div class="panel panel-default">
                  <div class="panel-heading">
                    <h4 class="panel-title">
                      <a class="panel-link">
                        ${scheme.displayName!scheme.name}</a>
                    </h4>
                  </div>
                  <div id="collapse${scheme_index}">
                    <div class="panel-body">
                      <div class="row">
                        <div class="col-sm-12">
                        <#if (scheme.logoUrl?length > 0)>
                          <a href="${scheme.url}"><img class="img img-thumbnail pull-left" style="margin:0 15px 15px 0;" src="${scheme.logoUrl}"></a>
                          </#if>
                          <blockquote>
                            ${scheme.description}
                          </blockquote>
                        </div>
                      </div>
                      
                    </div>
                  </div>
                </div>            
            <#else>
                <div class="panel panel-default">
                  <div class="panel-heading">
                    <h4 class="panel-title">
                      <a class="panel-link <#if scheme_index == 0>active<#else>collapsed</#if>" data-toggle="collapse" data-parent="#accordion" href="#collapse${scheme_index}">
                        <i class="fa fa-plus"></i><i class="fa fa-minus"></i> ${scheme.displayName!scheme.name}</a>
                    </h4>
                  </div>
                  <div id="collapse${scheme_index}" class="panel-collapse <#if scheme_index == 0>collapse show<#else>collapsed</#if>">
                    <div class="panel-body">
                      <div class="row">
                        <div class="col-sm-12">
                        <#if (scheme.logoUrl?length > 0)>
                          <a href="${scheme.url}"><img class="img img-thumbnail pull-left" style="margin:0 15px 15px 0;" src="${scheme.logoUrl}"></a>
                          </#if>
                          <blockquote>
                            ${scheme.description}
                          </blockquote>
                        </div>
                      </div>
                      
                    </div>
                  </div>
                </div>
                </#if>
              </#list>
              </div>
            </div>
            <div class="col-md-4">
              <h4>Recent Data Updates</h4>
              <ul class="list-group" style="font-size:15px;">
		          <#list changes as change>
                  <li class="list-group-item d-flex justify-content-between align-items-center">
	                  <span>
	                    <span class="badge badge-<#if change.type=='SAVE'>info<#elseif change.type=='UPDATE' >secondary<#else>danger</#if>"">
	                    ${change.objectType.label} </span> ${change.description}
	                    <br/><small>${change.user.displayName}   @</small> <i class="small italic"> ${change.dateCreated!'-1'?string}</i></span>
	                  <span class="badge badge-info badge-pill">${change.type}</span>
	                </li>
		        </#list>
              </ul>
            </div>
          </div>
          
        </div>
      </div>
    </div>
  </section>
  
  <section id="rd-section" style="color:#182235;">
    <div class="container">
      <div class="row">
        <div class="col-lg-8 mx-auto text-center">
          <h2 class="section-heading">Research &amp; Development</h2>
          <hr>
          <p>A description of the scope and initial development of the project, the PIs, the institutions, and on-going research/support... Maybe cite some publications and conference presentations? </p>
        </div>
      </div>
    </div>
    <div class="container">
      <div class="row">
        
      </div>
    </div>
  </section>
    <#include "includes/public-footer.ftl">
    
  <script type="text/javascript">
    $(document).ready(function() {
      $('.panel-link').click(function() {
        if($(this).hasClass('active')) {
          $(this).removeClass('active');
          return;
        }
          $('.panel-link').removeClass('active');
          $(this).addClass('active');
      });
    });
  </script>

  </body>
</html>