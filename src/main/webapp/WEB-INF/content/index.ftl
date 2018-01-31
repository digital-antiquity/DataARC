<!DOCTYPE html>
<html lang="en">
<head>
  <title>DataARC - Linking Data from Archaeology, the Sagas, and Climate</title>
  <#include "includes/public-header.ftl" />

  <!-- REPLACE "replaces/script_variables.ftl" -->
  <script>
  var testing = false;
  function getContextPath() {
    return "${contextPath}";
  }
  var FIELDS = { <#list fields as field>"${field.name}": "${field.displayName}"<#sep>, </#sep></#list> };
  var SCHEMA = { <#list schema as schema>"${schema.name}": "${schema.displayName}"<#sep>, </#sep></#list> };
  var geoJsonInputs = [ <#list files as file>{id:"${file.id?c}", name:"${file.name}", title:"${file.title!'untitled'}", url:"/geojson/${file.id?c}"}<#sep>, </#sep></#list> ];
  var coverage = [<#list coverage as c>
  	{ start:${c.startDate?c}, 
  		end:${c.endDate?c},
  	   term:"${c.term!''?json_string}",
description:"${c.description!''?replace('"',"'")?json_string}"}<#sep>,</#sep></#list>];
    </script>
  <!-- /REPLACE -->

  <style>
  .homesubhead {font-size:140%;}
  .service-box p {font-size:80% !important; font-weight:bold !Important}
  .hidden {display:none;visibility:hidden}
  </style>
</head>
<body id="page-top">
  <div class="loading loading-pg pull-right">
    <ul>
      <li></li>
      <li></li>
      <li></li>
      <li></li>
      <li></li>
      <li></li>
    </ul>
  </div>
  <!-- Navigation -->
  <nav class="navbar navbar-expand-lg navbar-light navbar-shrink fixed-top" id="mainNav">
    <div class="container">
      <a class="navbar-brand js-scroll-trigger" href="#page-top">DataARC</a>
      <button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarResponsive">
        <ul class="navbar-nav ml-auto">
          <li class="nav-item"><a class="nav-link js-scroll-trigger" href="#page-top">Start</a></li>
          <li class="nav-item"><a class="nav-link js-scroll-trigger" href="#explore-section">Explore</a></li>
          <li class="nav-item"><a class="nav-link js-scroll-trigger" href="#timeline-section">Timeline</a></li>
          <li class="nav-item"><a class="nav-link js-scroll-trigger" href="#map-section">Map</a></li>
          <li class="nav-item"><a class="nav-link js-scroll-trigger" href="#concept-section">Concept</a></li>
          <li class="nav-item"><a class="nav-link js-scroll-trigger" href="#results-section">Results <span id="results-count" class="badge badge-dark"></span></a></li>
          <li class="nav-item"><a class="nav-link js-scroll-trigger" href="#why-section">Why</a></li>
        </ul>
      </div>
    </div>
  </nav>
  <header class="masthead home-shore">
    <div class="header-content">
      <div class="header-content-inner">
        <h1 id="homeHeading">Human Ecodynamics in the North Atlantic, Interdisciplinary Research Tool <b><a href="http://www.data-arc.org">learn more</a></b> </h1>
        <hr>
        <p>Find contextualized data from ecological, archaeological, and historical sources for the North Atlantic.</p>
        <p>
          <div class="row justify-content-md-center">
            <div class="col-lg-6 text-center">
              <div class="input-group">
                <input id="keywords-field" type="text" class="form-control" placeholder="Search for..." aria-label="Search for...">
                <span class="input-group-btn">
                  <a id="keywords-btn" class="btn btn-primary btn-xl js-scroll-trigger" href="#explore-section">Explore!</a>
                </span>
              </div>
            </div>
          </div>
        </p>
        <p>
          <div class="row">
            <div class="col-lg-3 col-md-6 text-center">
              <div class="service-box">
                <i class="fa fa-4x fa-superpowers text-primary sr-icons"></i>
                <h3>Keyword Example</h3>
                <p>Using the keyword example, you can see how a simple phrase in the keyword box will filter our results based on terms that you specify.</p>
              </div>
            </div>
            <div class="col-lg-3 col-md-6 text-center">
              <div class="service-box">
                <i class="fa fa-4x fa-compass text-primary sr-icons"></i>
                <h3>Spatial Example</h3>
                <p>Want to see results only with a specific bounding box?</p>
              </div>
            </div>
            <div class="col-lg-3 col-md-6 text-center">
              <div class="service-box">
                <i class="fa fa-4x fa-clock-o text-primary sr-icons"></i>
                <h3>Temporal Example</h3>
                <p>This example uses our timeline to filter the result data.</p>
              </div>
            </div>
            <div class="col-lg-3 col-md-6 text-center">
              <div class="service-box">
                <i class="fa fa-4x fa-sitemap text-primary sr-icons"></i>
                <h3>Concept Example</h3>
                <p>Looking for a way to view results that only relate to specific concepts? Filter results based on the concept map. Learn about <a href="https://www.data-arc.org/conceptmapping/">dataARC&apos;s concept map</a>.</p>
              </div>
            </div>
          </div>
        </p>
      </div>
    </div>
  </header>
  <section id="explore-section" class="bg-primary">
    <div class="container">
      <div class="row">
        <div class="col-lg-8 mx-auto text-center">
          <h2 class="section-heading text-white">Explore the data!</h2>
          <hr class="light">
          <p class="text-faded">You can search and filter by any combination of space, time and concept. Combine filters to narrow your search.</p>
          <a class="btn btn-dark btn-xl js-scroll-trigger" href="#timeline-section"><i class="fa fa-clock-o text-white sr-icons"></i> Timeline</a>
          <a class="btn btn-dark btn-xl js-scroll-trigger" href="#map-section"><i class="fa fa-map-o text-white sr-icons"></i> Map</a>
          <a class="btn btn-dark btn-xl js-scroll-trigger" href="#concept-section"><i class="fa fa-sitemap text-white sr-icons"></i> Concept</a>
        </div>
      </div>
    </div>
  </section>
  <section id="timeline-section">
    <div class="container">
      <div class="row">
        <div class="col-lg-12 text-center">
          <h2 class="section-heading">Timeline</h2>
          <hr class="primary">
          <div class="legend justify-content-md-center">
            <ul class="list-inline">
              <li class="list-inline-item"><span class="legend-item legend-item-one">&nbsp;&nbsp;</span> Archaeological Sources</li>
              <li class="list-inline-item"><span class="legend-item legend-item-two">&nbsp;&nbsp;</span> Textual Sources</li>
              <li class="list-inline-item"><span class="legend-item legend-item-three">&nbsp;&nbsp;</span> Environmental Sources</li>
            </ul>
          </div>
          <div id="timeline">
            <div class="loader h-100 justify-content-center align-items-center">
              <h1><span class="fa fa-cog fa-spin fa-2x"></span></h1>
            </div>
          </div>
          <button id="filter-timeline-apply" class="btn btn-primary"><i class="fa fa-clock-o text-white sr-icons"></i> Apply Filter</button>
        </div>
      </div>
    </div>
  </section>
  <section id="map-section">
    <div class="container">
      <div class="row">
        <div class="col-lg-12 text-center">
          <h2 class="section-heading">Map</h2>
          <hr class="primary">
          <div class="legend justify-content-md-center">
            <ul class="list-inline">
              <li class="list-inline-item"><span class="legend-item legend-item-one">&nbsp;&nbsp;</span> Archaeological Sources</li>
              <li class="list-inline-item"><span class="legend-item legend-item-two">&nbsp;&nbsp;</span> Textual Sources</li>
              <li class="list-inline-item"><span class="legend-item legend-item-three">&nbsp;&nbsp;</span> Environmental Sources</li>
            </ul>
          </div>
          <div id="map">
            <div id="mapSpinner"><span class="fa fa-spinner fa-spin"></span></div>
          </div>
        </div>
      </div>
    </div>
  </section>
  <section id="concept-section">
    <div class="container">
      <div class="row">
        <div class="col-lg-12 text-center">
          <h2 class="section-heading">Concept</h2>
          <hr class="primary">
        </div>
        <div class="col-lg-12">
          <div id="topicControls" class="btn-toolbar justify-content-between">
            <div class="btn-group">
              <button title="zoom in" id="topicmapZoomIn" class="btn btn-secondary"><span class="fa fa-search-plus"></span></button>
              <button title="zoom out" id="topicmapZoomOut" class="btn btn-secondary"><span class="fa fa-search-minus"></span></button>
              <button title="reset" id="topicmapReset" class="btn btn-secondary"><span class="fa fa-repeat"></span></button>
              <button title="pause" id="topicmapPause" class="btn btn-secondary"><span class="fa fa-pause"></span></button>
              <button title="continue" id="topicmapProceed" class="btn btn-secondary"><span class="fa fa-play"></span></button>
            </div>
            <div id="topicSearch" class="input-group"></div>
          </div>
          <div id="conceptContainer" style="width:100%;">
            <div id="topicmap"></div>
          </div>
        </div>
      </div>
    </div>
  </section>
  <section id="results-section">
    <div class="call-to-action bg-gray">
      <div class="container text-center">
        <h2>Filters</h2>
        <hr class="primary">
        <div id="filters" class="row">&nbsp;</div>
        <button id="filter-save" class="btn btn-light"><i class="fa fa-bookmark sr-icons"></i> Save Search</button>
        <button id="filter-share" class="btn btn-light"><i class="fa fa-print sr-icons"></i> Share Search</button>
      </div>
    </div>
    <div class="call-to-action bg-dark">
      <div class="container text-center">
        <h2>Results</h2>
        <hr class="primary">
        <div id="results">
          <div class="result-loader col-sm-12 text-center">
            <h1><i class="fa fa-cog fa-spin fa-2x"></i></h1>
          </div>
        </div>
        <button id="results-print" class="btn btn-light"><i class="fa fa-print sr-icons"></i> Print Results</button>
      </div>
    </div>
  </section>
  <section id="why-section">
    <div class="container">
      <div class="row">
        <div class="col-lg-8 mx-auto text-center">
          <h2 class="section-heading">Why</h2>
          <hr class="primary">
          <p>Why did you get these results? We will explain how the results were obtained in order to provide a level of confidence for how the data was processed to produce what you are seeing.</p>
        </div>
      </div>
    </div>
  </section>
  <!-- Vendor scripts -->

  <#include "includes/public-footer.ftl">

  <!-- Custom scripts -->
  <script src="js/global.js"></script>
  <script src="js/search.js"></script>
  <script src="js/filter.js"></script>
  <script src="js/timeline.js"></script>
  <script src="js/geography.js"></script>
  <script src="js/concepts.js"></script>
  <script src="js/results.js"></script>

  <!-- everything below this is automatically generated -->

  <!-- Either leave this template here or incorporate into the ones that are autmoatically generated -->
  <script id="title-template-polygon" type="text/x-handlebars-template">
    <div class="title">{{#each this}}<b>{{@key}}</b>: {{this}}<br/>{{/each}}</div>
    <button class="btn btn-sm" onclick="Geography.regionFilter('{{this.region}}')">Filter by this polygon</button>
  </script>

  <!-- handlebar templates http://handlebarsjs.com -->
  <!-- REPLACE "replaces/handlebar_templates.ftl" -->
  <#list schema as schemum>
  <script id="title-template-${schemum.id?c}" type="text/x-handlebars-template">
    <div class="title">
      <#if schemum.titleTemplate?has_content && schemum.titleTemplate != ''>
      ${schemum.titleTemplate}
      <#else>
      {{#each this}}<b>{{fieldName @key}}</b>: {{this}}<br/>{{/each}}
      </#if>
    </div>
  </script>
  <script id="results-template-${schemum.id?c}" type="text/x-handlebars-template">
    <div class="description">
      <#if schemum.resultTemplate?has_content && schemum.resultTemplate != ''>
      ${schemum.resultTemplate}
      <#else>
      {{#each this}}<b>{{fieldName @key}}</b>: {{this}}<br/>{{/each}}
      </#if>
    </div>
  </script>
  </#list>
  <div class="hidden">
  <#list schema as schemum>
    <div id="${schemum.name?replace(" ","_")}_bio">${schemum.description!"No description"}</div>
  </#list>
  </div>
  <!-- /REPLACE -->

</body>
</html>