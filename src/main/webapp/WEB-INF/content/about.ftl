<!DOCTYPE html>
<html lang="en">
  <head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="description" content="">
  <meta name="author" content="">
  <title>About DataARC</title>
      <#include "includes/public-header.ftl">

  
    </head>
<body id="about">
  <!-- Navigation -->
  <nav class="navbar navbar-expand-lg navbar-light fixed-top" id="mainNav">
    <div class="container">
      <a class="navbar-brand js-scroll-trigger" href="#page-top">DataARC</a>
      <button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
    </div>
  </nav>
  <header class="masthead">
    <div class="header-content">
      <div class="header-content-inner">
        <h1 id="homeHeading">About dataARC</h1>
        <hr>
        <p>
          <div class="row justify-content-md-center">
            <div class="col-lg-6 text-center">

            
            </div>
          </div>
        </p>
      </div>
    </div>
  </header>
  <section>
    <div class="container">
      <div class="row">
        <div class="col-lg-8">
          <h2 class="section-heading">Our Contributors</h2>
          <hr class="light">
          <ul class="list-unstyled">
              <#list schema as scheme>
                  <li class="media">
                      <img class="mr-3" src="..." alt="Generic placeholder image">
                      <div class="media-body">
                        <h5 class="mt-0 mb-1">${scheme.displayName!scheme.name}</h5>
                        <p>${scheme.description!''}</p>
                      </div>
                </li>
              </#list>
          </ul>
        </div>
        <div class="col-lg-4">
        <h3>Recent Updates</h3>
        <ul class="list-unstyled">
        <#list changes as change>
            <li class='media'> <span class="badge badge-<#if change.type=='SAVE'>info<#elseif change.type=='UPDATE' >secondary<#else>danger</#if>">${change.objectType.label}</span>
            <div class="media-body"><p style="margin-left:1em"> ${change.user.displayName} ${change.type} ${change.description}
            <br/><i class="small italic"> ${change.dateCreated?string}</i></p>
            </div>
            </li>
        </#list>
        </ul>
        </div>
      </div>
    </div>
  </section>
    <#include "includes/public-footer.ftl">
  </body>
</html>