<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">
	<script>
	var testing = false;
	
	    function getContextPath() {
        return "${contextPath}";
    }

	</script>
    <title>DataARC - Linking Data from Archaeology, the Sagas, and Climate</title>

    <!-- Bootstrap core CSS -->
    <link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <!-- FOR CYTOSCAPE -->
    <link rel="stylesheet" href="js/components/qtip2/jquery.qtip.min.css" />
    <link href="css/custom-cyto.css" rel="stylesheet">
    <!-- END CYTOSCAPE -->

    <!-- Custom fonts for this template -->
    <link href="vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href='https://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800' rel='stylesheet' type='text/css'>
    <link href='https://fonts.googleapis.com/css?family=Merriweather:400,300,300italic,400italic,700,700italic,900,900italic' rel='stylesheet' type='text/css'>

    <!-- Plugin CSS -->
    <link href="vendor/magnific-popup/magnific-popup.css" rel="stylesheet">
    <link href="vendor/leaflet/leaflet.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="css/custom.css" rel="stylesheet">
    <link href="css/timeline.css" rel="stylesheet">
    <link href="css/results.css" rel="stylesheet">

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
            <div id="timeline"></div>
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
            <div id="map"></div>
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

            <div id="topicmap" style="width:100%;height:600px;border:1px solid #eee;">
              <div class="btn-group topicmap-tools">
                <button title='zoom in' class="btn btn-secondary" id="zoom_in"><span class="fa fa-search-plus"></span></button>
                <button title='zoom out' class="btn btn-secondary" id="zoom_out"><span class="fa fa-search-minus"></span></button>
                <button title="change layout" class="btn btn-secondary" id="change_layout"><span class="fa fa-random"></span></button>
                <button title='reset' class="btn btn-secondary" id="reset"><span class="fa fa-repeat"></span></button>
              </div>
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
          <div id="results"></div>
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


    <div id="layouts" style="display:none;">
      <select id="layout-select">
        <option value="breadthfirst">Breadth First</option>
        <option value="circle">Circle</option>
        <option value="cola" selected>Cola</option>
        <option value="concentric">Concentric</option>
        <option value="cose">Cose</option>
        <option value="grid">Grid</option>
        <option value="spread">Spread</option>
      </select>
    </div>

    <!-- Vendor scripts -->
    <script src="vendor/jquery/jquery.min.js"></script>
    <script src="vendor/popper/popper.min.js"></script>
    <script src="components/handlebars/handlebars.js"></script>
    <script src="vendor/lodash/lodash.min.js"></script>
    <script src="vendor/bootstrap/js/bootstrap.min.js"></script>
    <script src="vendor/d3/d3.v4.min.js"></script>
    <script src="vendor/leaflet/leaflet.js"></script>
    <script src="vendor/moment/moment.min.js"></script>
    <script src="vendor/jquery-easing/jquery.easing.min.js"></script>
    <script src="vendor/scrollreveal/scrollreveal.min.js"></script>
    <script src="vendor/magnific-popup/jquery.magnific-popup.min.js"></script>

    <!-- FOR CYTOSCAPE -->
    <script src="js/components/cytoscape/dist/cytoscape.min-edit.js"></script>
    <script src="js/components/cytoscape-cola/cola.js"></script>
    <script src="js/components/cytoscape-cola/cytoscape-cola.js"></script>
    <script src="js/components/cytoscape-spread/cytoscape-spread.js"></script>
    <script src="js/components/qtip2/jquery.qtip.min.js"></script>
    <script src="js/components/cytoscape-qtip/cytoscape-qtip.js"></script>
    <script src="js/components/bluebird/js/browser/bluebird.min.js"></script>
    <script src="js/components/typeahead.js/dist/typeahead.bundle.js"></script>
    <script src="js/topicmap.js"></script>

    <!-- END CYTOSCAPE -->

    <!-- Custom scripts -->
    <script src="js/custom.js"></script>
    <script src="js/search.js"></script>
    <script src="js/timeline.js"></script>
    <script src="js/geography.js"></script>
    <script src="js/concepts.js"></script>
    <script src="js/results.js"></script>

    <!-- Page Level Javascript Actions -->
    <script type="text/javascript">
      $(document).ready(function() {
      var req = {
          source: "/api/search",
          delay: 100, // in ms
          callback: function() {
            // Handler function
            Timeline.refresh('#timeline', function (val) {
              console.log(val);
            });
            Geography.refresh();
            // Concepts.refresh();
            ResultsHandler = new Results('#results');
          }
        };
        if (testing) {
        	req.source = "src/features_17.08.21.json";
    	}; 
        Search.init(req);
      });
    </script>
   <script type="application/json">
   	[
   <#list files as file>
     {id:"${file.id?c}", name:"${file.name}", url:"/geojson/${file.id?c}"}}<#sep>, </#sep> 
   </#list>
   	]
   </script>
   <!--  handlebar templates http://handlebarsjs.com
     -->
  <#list schema as schemum>    
    <script id="title-template-${schemum.id?c}" type="text/x-handlebars-template">
	  <div class="title">
	  	${schemum.titleTemplate!'{{#each this}}<b>{{@key}}</b>: {{this}}<br/>{{/each}}'}
	  </div>
	</script>
    <script id="results-template-${schemum.id?c}" type="text/x-handlebars-template">
	  <div class="description">
	  	${schemum.resultTemplate!'{{#each this}}<b>{{@key}}</b>: {{this}}<br/>{{/each}}'}
	  </div>
	</script>
  </#list>

  </body>
</html>