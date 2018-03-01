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
  var coverage = ${coverage!''};
  </script>
  <!-- /REPLACE -->

  <style>
  .hidden {display:none;visibility:hidden}
  </style>
</head>
<body id="page-top">
  <!-- Navigation -->
  <nav class="navbar navbar-expand-lg navbar-light navbar-shrink fixed-top" id="mainNav">
    <div class="container">
      <a class="navbar-brand js-scroll-trigger" href="#page-top">DataARC</a>
      <a class="navbar-about btn btn-sm btn-outline-dark" href="/about">About Our Data</a>
      <button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarResponsive">
        <ul class="navbar-nav ml-auto">
          <li class="nav-item"><a class="nav-link js-scroll-trigger" href="#page-top">Start</a></li>
          <li class="nav-item"><a class="nav-link js-scroll-trigger" href="#keyword-section">Keyword</a></li>
          <li class="nav-item"><a class="nav-link js-scroll-trigger" href="#timeline-section">Timeline</a></li>
          <li class="nav-item"><a class="nav-link js-scroll-trigger" href="#map-section">Map</a></li>
          <li class="nav-item"><a class="nav-link js-scroll-trigger" href="#concept-section">Concept</a></li>
          <li class="nav-item"><a class="nav-link js-scroll-trigger" href="#filters-section">Filters <span id="filters-count" class="badge badge-primary">0</span></a></li>
          <li class="nav-item"><a class="nav-link js-scroll-trigger" href="#results-section">Results <span id="results-count" class="badge badge-danger">0</span></a></li>
          <li class="nav-item"><a class="nav-link js-scroll-trigger" href="#why-section">Why</a></li>
        </ul>
      </div>
    </div>
  </nav>
  <header class="masthead home-shore">
    <div class="header-content">
      <div class="header-content-inner">
        <h1 id="homeHeading">dataARC Search Tool</h1>
        <hr class="light">
        <h3>Find contextualized data from ecological, archaeological, and historical sources for the North Atlantic.</h3>
        <p>&nbsp;</p>
        <p><a href="/help" class="btn btn-sm btn-light">Not sure where to start?</a></p>
        <p>
          <div class="row">
            <div class="col-lg-3 col-md-6 text-center">
              <div class="service-box">
                <i class="fa fa-4x fa-font text-primary sr-icons"></i>
                <h4>Keyword</h4>
                <p>Using the keyword example, you can see how a simple phrase in the keyword box will filter our results based on terms that you specify.</p>
              </div>
            </div>
            <div class="col-lg-3 col-md-6 text-center">
              <div class="service-box">
                <i class="fa fa-4x fa-clock-o text-success sr-icons"></i>
                <h4>Temporal</h4>
                <p>This example uses our timeline to filter the result data.</p>
              </div>
            </div>
            <div class="col-lg-3 col-md-6 text-center">
              <div class="service-box">
                <i class="fa fa-4x fa-compass text-danger sr-icons"></i>
                <h4>Spatial</h4>
                <p>Want to see results only with a specific bounding box?</p>
              </div>
            </div>
            <div class="col-lg-3 col-md-6 text-center">
              <div class="service-box">
                <i class="fa fa-4x fa-sitemap text-warning sr-icons"></i>
                <h4>Concept</h4>
                <p>Looking for a way to view results that only relate to specific concepts? Filter results based on the concept map. Learn about <a href="https://www.data-arc.org/conceptmapping/">dataARC&apos;s concept map</a>.</p>
              </div>
            </div>
          </div>
        </p>
      </div>
    </div>
  </header>
  <section id="explore-section" class="bg-primary">
    <div class="container call-to-action">
      <div class="row justify-content-center">
        <div class="col-lg-8 mx-auto text-center">
          <h2 class="section-heading text-white">Explore the data!</h2>
          <hr class="light">
          <p class="text-faded">You can search and filter by any combination of keyword, time, space and concept. Combine filters to narrow your search.</p>
          <a class="btn btn-dark btn-xl js-scroll-trigger" href="#keyword-section"><i class="fa fa-search text-white sr-icons"></i> Keyword</a>
          <a class="btn btn-dark btn-xl js-scroll-trigger" href="#timeline-section"><i class="fa fa-clock-o text-white sr-icons"></i> Timeline</a>
          <a class="btn btn-dark btn-xl js-scroll-trigger" href="#map-section"><i class="fa fa-map-o text-white sr-icons"></i> Map</a>
          <a class="btn btn-dark btn-xl js-scroll-trigger" href="#concept-section"><i class="fa fa-sitemap text-white sr-icons"></i> Concept</a>
        </div>
      </div>
    </div>
  </section>
  <section id="keyword-section" class="bg-light">
    <div class="container call-to-action">
      <div class="row justify-content-center">
        <div class="col-lg-6 text-center">
          <h2 class="section-heading">Keyword</h2>
          <hr class="primary">
          <div class="input-group">
            <input id="keywords-field" type="text" class="form-control" placeholder="Filter by..." aria-label="Filter by...">
            <span class="input-group-btn">
              <a id="keywords-btn" class="btn btn-primary btn-xl js-scroll-trigger" href="#keyword-section">Filter</a>
            </span>
          </div>
        </div>
      </div>
    </div>
  </section>
  <section id="timeline-section" class="bg-white">
    <div class="container call-to-action">
      <div class="row">
        <div class="col-lg-12 text-center">
          <h2 class="section-heading">Timeline<sup><a href="www.data-arc.org/time/" class="text-dark" target="_blank" data-toggle="tooltip" title="How dataarc thinks about time"><i class="fa fa-info-circle"></i></a></sup></h2>
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
  <section id="map-section" class="bg-light">
    <div class="container call-to-action">
      <div class="row">
        <div class="col-lg-12 text-center">
          <h2 class="section-heading">Map<sup><a href="www.data-arc.org/space/" class="text-dark" target="_blank" data-toggle="tooltip" title="How dataarc thinks about space and place"><i class="fa fa-info-circle"></i></a></sup></h2>
          <hr class="primary">
          <div class="legend justify-content-md-center">
            <ul class="list-inline">
              <li class="list-inline-item"><img src="img/icons/archaeological.png" class="mx-auto" alt="Archaeological Source Icon"> Archaeological Sources</li>
              <li class="list-inline-item"><img src="img/icons/textual.png" class="mx-auto" alt="Textual Source Icon"> Textual Sources</li>
              <li class="list-inline-item"><img src="img/icons/environmental.png" class="mx-auto" alt="Environmental Source Icon"> Environmental Sources</li>
            </ul>
          </div>
          <div id="map"></div>
        </div>
      </div>
    </div>
  </section>
  <section id="concept-section" class="bg-white">
    <div class="container call-to-action">
      <div class="row">
        <div class="col-lg-12 text-center">
          <h2 class="section-heading">Concepts<sup><a href="www.data-arc.org/conceptmapping/" class="text-dark" target="_blank" data-toggle="tooltip" title="How to use dataarc concepts in your search"><i class="fa fa-info-circle"></i></a></sup></h2>
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
  <section id="filters-section" class="bg-secondary">
    <div class="call-to-action">
      <div class="container text-center">
        <h2 class="section-heading">Filters</h2>
        <hr class="primary">
        <div id="filters" class="row">&nbsp;</div>
        <button id="filter-save" class="btn btn-primary"><i class="fa fa-bookmark sr-icons"></i> Save Search</button>
        <button id="filter-share" class="btn btn-primary"><i class="fa fa-print sr-icons"></i> Share Search</button>
      </div>
    </div>
  </section>
  <section id="results-section" class="bg-dark text-white">
    <div class="call-to-action">
      <div class="container text-center">
        <h2 class="section-heading">Results</h2>
        <hr class="primary">
        <p>&nbsp;</p>
      </div>
    </div>
  </section>
  <section id="why-section" class="bg-light">
    <div class="call-to-action">
      <div class="container">
        <div class="row">
          <div class="col-lg-8 mx-auto text-center">
            <h2 class="section-heading">Why</h2>
            <hr class="primary">
            <p>Why did you get these results? We will explain how the results were obtained in order to provide a level of confidence for how the data was processed to produce what you are seeing.</p>
          </div>
        </div>
      </div>
    </div>
  </section>

<!--   <div class="counts">
    <ul class="list-unstyled">
      <li class="icon"><a class="js-scroll-trigger" href="#filters-section">Filters<span id="filters-count" class="badge badge-primary">0</span></a></li>
      <li class="icon"><a class="js-scroll-trigger" href="#results-section">Results<span id="results-count" class="badge badge-danger">0</span></a></li>
    </ul>
  </div> -->

<!--   <div class="counts">
    <div>
      <i class="fa fa-filter"></i>
      <span id="results-count" class="badge badge-danger">0</span>
      <strong>results</strong>
    </div>
    <span id="filters-count" class="badge badge-primary">0</span>
  </div> -->

  <!-- Details modal -->
  <div class="modal fade" id="results-details" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header text-light bg-dark"></div>
        <div class="modal-body"></div>
      </div>
    </div>
  </div>

  <#include "includes/public-footer.ftl">

  <!-- Custom scripts -->
  <script src="js/loader.js"></script>
  <script src="js/search.js"></script>
  <script src="js/filter.js"></script>
  <script src="js/timeline.js"></script>
  <script src="js/geography.js"></script>
  <script src="js/concepts.js"></script>
  <script src="js/results.js"></script>
  <script src="js/global.js"></script>

  <!-- everything below this is automatically generated -->

  <!-- Either leave this template here or incorporate into the ones that are autmoatically generated -->
  <script id="title-template-polygon" type="text/x-handlebars-template">
    <ul class="list-unstyled">{{#each this}}<li><strong>{{@key}}</strong>: {{this}}</li>{{/each}}</ul>
    <button class="btn btn-primary btn-sm" onclick="Geography.filterByRegion('{{this.region}}')">Filter by this polygon</button>
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