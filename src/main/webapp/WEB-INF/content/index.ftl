<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>DataARC - Linking Data from Archaeology, the Sagas, and Climate</title>

    <!-- Bootstrap core CSS -->
    <link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom fonts for this template -->
    <link href="vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href='https://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800' rel='stylesheet' type='text/css'>
    <link href='https://fonts.googleapis.com/css?family=Merriweather:400,300,300italic,400italic,700,700italic,900,900italic' rel='stylesheet' type='text/css'>

    <!-- Plugin CSS -->
    <link href="vendor/magnific-popup/magnific-popup.css" rel="stylesheet">
    <link href="vendor/leaflet/leaflet.css" rel="stylesheet">
    <link href="vendor/leaflet.draw/leaflet.draw.css" rel="stylesheet">
    <link href="vendor/leaflet.easybutton/easy-button.css" rel="stylesheet">
    <link href="vendor/datatables/DataTables-1.10.16/css/dataTables.bootstrap4.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="css/custom.css" rel="stylesheet">
    <link href="css/timeline.css" rel="stylesheet">
    <link href="css/results.css" rel="stylesheet">
    <link href="css/concepts.css" rel="stylesheet">
    <link href="css/geography.css" rel="stylesheet">


	<script>
	var testing = false;
	// Global variables

	    function getContextPath() {
        return "${contextPath}";
    }
    
    var FIELDS = {
    <#list fields as field>"${field.name}": "${field.displayName}"<#sep>,
</#sep></#list>
    };
    var SCHEMA = {<#list schema as schema>"${schema.name}": "${schema.displayName}"<#sep>,</#sep></#list>};
    
   var geoJsonInputs =	[
   <#list files as file>
     {id:"${file.id?c}", name:"${file.name}", url:"/geojson/${file.id?c}"}<#sep>, </#sep> 
   </#list>
   	];

	</script>
  </head>
  <body id="page-top">

    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-light fixed-top" id="mainNav">
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
              <a class="nav-link js-scroll-trigger" href="#results-section">Results</a>
            </li>
            <li class="nav-item">
              <a class="nav-link js-scroll-trigger" href="#why-section">Why</a>
            </li>
          </ul>
        </div>
      </div>
    </nav>

    <header class="masthead">
      <div class="header-content">
        <div class="header-content-inner">
          <h1 id="homeHeading">Simple Interface, Data That Matters</h1>
          <hr>
          <p>Enter a word or phrase to begin filtering the result data or select a preconfigured example.</p>
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
                  <p>Using the keyword example, you can see how a simple phrase in the keyword box will filter our results.</p>
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
                  <p>Looking for a way to view results that only relate to specific concapts?</p>
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
            <p class="text-faded">Make your way through the different types of data using the tools below.</p>
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
            <div class="container legend">
              <div class="row justify-content-md-center">
                <div class="col col-lg-2">
                  1 of 3
                </div>
                <div class="col-md-auto">
                  2 of 3
                </div>
                <div class="col col-lg-2">
                  3 of 3
                </div>
              </div>
            </div>
            <div id="timeline">
              <div class="loader col-sm-12 text-center">
                <h1><i class="fa fa-cog fa-spin fa-2x"></i></h1>
              </div>
            </div>
            <button id="filter-timeline" class="btn btn-dark"><i class="fa fa-clock-o text-white sr-icons"></i> Apply Temporal Filter</button>
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
            <div id="map"><div id="mapSpinner"><span class="fa fa-spinner fa-spin"></span></div></div>
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

            <div id="conceptContainer" style="width:100%;height:700px;">
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
              <div id="topicmap"></div>
            </div>

          </div>
          <div class="col-lg-12">
            <table id="infotable"><td></td></table>
          </div>
        </div>
      </div>
    </section>


    <section id="results-section">
      <div class="call-to-action bg-dark">
        <div class="container text-center">
          <h2>Results</h2>
          <hr class="primary">
          <div id="results">
            <div class="result-loader col-sm-12 text-center">
              <h1><i class="fa fa-cog fa-spin fa-2x"></i></h1>
            </div>
          </div>
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
    <script src="vendor/jquery/jquery.min.js"></script>
    <script src="vendor/popper/popper.min.js"></script>
    <script src="vendor/lodash/lodash.min.js"></script>
    <script src="vendor/bootstrap/js/bootstrap.min.js"></script>
    <script src="vendor/d3/d3.v4.min.js"></script>
    <script src="vendor/leaflet/leaflet.js"></script>
    <script src="vendor/leaflet.draw/leaflet.draw.js"></script>
    <script src="vendor/leaflet.easybutton/easy-button.js"></script>
    <script src="vendor/leaflet.esri/esri-leaflet.js"></script>
    <script src="vendor/handlebars/handlebars.js"></script>
    <script src="vendor/moment/moment.min.js"></script>
    <script src="vendor/jquery-easing/jquery.easing.min.js"></script>
    <script src="vendor/scrollreveal/scrollreveal.min.js"></script>
    <script src="vendor/magnific-popup/jquery.magnific-popup.min.js"></script>
    <script src="vendor/datatables/datatables.min.js"></script>
    <script src="vendor/datatables/DataTables-1.10.16/js/dataTables.bootstrap4.min.js"></script>
    <script src="vendor/typeahead.js/dist/typeahead.bundle.min.js"></script>

    <!-- Custom scripts -->
    <script src="js/custom.js"></script>
    <script src="js/search.js"></script>
    <script src="js/timeline.js"></script>
    <script src="js/geography.js"></script>
    <script src="js/concepts.js"></script>
    <script src="js/results.js"></script>


    <script type="text/javascript">
    Handlebars.registerHelper("fieldName", function(name) {
    console.log("|" +name+"|");
    if (FIELDS[name.trim()] != undefined) {
        return FIELDS[name.trim()];
      } 
      return name;
    });
    </script>

    <!-- Page Level Javascript Actions -->
    <script type="text/javascript">
    
    var config = {
          source: "/api/search",
          delay: 100, // delay before search checks in ms
          before: function() { // actions to run before search query begins
            Geography.wait();
          },
          after: function() { // actions to run after search query is finished
            Timeline.refresh("#timeline");
            Geography.refresh();
            Concepts.refresh();
            ResultsHandler = new Results('#results');
          }
        };
        
        if (testing) {
        config.source = "search.php";
        }
      $(document).ready(function() {
        Search.init(config);
      });
    </script>

    <!-- everything below this is automatically generated -->

    <!-- Either leave this template here or incorporate into the ones that are autmoatically generated -->
    <script id="title-template-polygon" type="text/x-handlebars-template">
      <div class="title">
        {{#each this}}<b>{{@key}}</b>: {{this}}<br/>{{/each}}
      </div>
      <button class="btn btn-sm" onclick="Geography.regionFilter('{{this.region}}')">Filter by this polygon</button>
    </script>

   <!--  handlebar templates http://handlebarsjs.com
     -->
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

  </body>
</html>