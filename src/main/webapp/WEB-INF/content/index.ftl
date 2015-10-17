<!DOCTYPE html PUBLIC 
	"-//W3C//DTD XHTML 1.1 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
	
	<!DOCTYPE html>
<html>
    <head>
        <title>CyberNABO Prototype</title>
        <meta charset="utf-8" />
    
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="components/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet" media="screen">
        <script src="http://api.simile-widgets.org/timeline/2.3.1/timeline-api.js?bundle=true" type="text/javascript"></script>
        <link rel="stylesheet" href="components/c3/c3.css" />
        <link rel="stylesheet" href="components/seiyria-bootstrap-slider/dist/css/bootstrap-slider.min.css">
    	<link rel="stylesheet" href="css/nabo.css"/>    
        <link rel="stylesheet" href="components/leaflet/dist/leaflet.css" />
   </head>
<body>
<div class="container-fluid">
<div class="row">
    <div id="mapbox"  class="col-md-8">
        <div id="map" style="height: 600px"></div>
        <div class="row">
	        <div class="col-md-12">
			    <form class="form-inline">
				    <input type="text" name="term" class="form-control" id="term" placeholder="search"/>
				    <span class='slider-span'>&nbsp;Limit by year:</span> <b>0</b> 
				            <input data-slider-id='ex1Slider' type="text" data-slider-min="0" data-slider-max="2000" data-slider-step="1" data-slider-value="[800,1200]"
				     data-slider-orientation="horizontal" data-slider-selection="after" data-slider-tooltip="show" id="timeslider"
				     data-slider-handle="round"> <b>2016</b>
				     <br/>
				     <!--<img src="http://ads.ahds.ac.uk/arena/search/images/per1.gif">-->
			           <div id="tl" class="timeline-default" style="height: 400px;"></div>

				     
			    </form>
			</div>
	</div>
        
    </div>
    <div id="infobox" class="col-md-4">
        <div id="forcemap"></div>
        <div><p><small>Select a node to center it. Click on the + symbols to expand along a branch. Double click on a node to access linked URLs pointing at relevant databases and descriptions of key metrics.</small></p></div>
	<div class="btn-toolbar" role="toolbar">
        <div class="btn-group">
        
		<button title='zoom in' class="btn btn-default" id="zoom_in"><span class="glyphicon glyphicon-zoom-in"></span></button>
		<button title='zoom out' class="btn btn-default" id="zoom_out"><span class="glyphicon glyphicon-zoom-out"></span></button>
		
		<button title='scroll up' class="btn btn-default" id="up"><span class="glyphicon glyphicon-triangle-top"></span></button>
		<button title='scroll down' class="btn btn-default" id="down"><span class="glyphicon glyphicon-triangle-bottom"></span></button>
		<button title='scroll left' class="btn btn-default" id="left"><span class="glyphicon glyphicon-triangle-left"></span></button>
		<button title='scroll right' class="btn btn-default" id="right"><span class="glyphicon glyphicon-triangle-right"></span></button>
        </div>
        <div class="btn-group pull-right">
        <button title='expand' class="btn btn-default" id="expand"><span class="glyphicon glyphicon-resize-full"></span></button>
        <button title='contract' class="btn btn-default" id="contract"><span class="glyphicon glyphicon-resize-small"></span></button>
        </div>
        </div>
        <br/>
        <div id="infostatus">
        </div>
        <div id="chart"></div>
        <div id="infodetail">
            <div id="infoabout">
            <h4>Detail Info Box</h4>
            <p><b>Click</b> on a point on the map, a region, or a node in the topic map to see details about that concept or point here.</p>
            </div>
        </div>
    
    </div>
</div>

<div class="modal" id="intro-modal">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
        <h4 class="modal-title">Welcome to the CyberNABO Prototype</h4>
      </div>
      <div class="modal-body">
				<div class="row">
					<div class="col-md-12">
	    		      <img src="images/preview-small.png"/>
	    	        </div>
		       </div>
				<div class="row">
					<div class="col-md-6">
					<b>Explore by Map</b>
					<p style="font-size:90%">The map provides a more traditional view of the data and data sources. Exploring the map provides a visual overview of where data exists 
					and how it overlaps. Points show specific areas of interest or data collection from a single source, while regions are highlighted based on the
					number of points that they contain.</p>
					<p style="font-size:90%">Clicking on a point or a region will show data collected in the info box on the right. </p>
	    	        </div>
					<div class="col-md-6">
					<b>Explore by Topic</b>
					<br/>
			<p style="font-size:90%">Archaeologists draw on a wide variety of data to investigate any topic. The concept maps are intended to aid in the
    		 process of gathering together all the specialist data that may be relevant to a topic. They point first
    		 to linked topics, and then to the available data sources and key indicators that would be
		     relevant. The aim is to make specialists more aware of the most relevant
    		 aspects of one another’s data to shared research questions, and to tease out how these data map together, not at
    		 the level of integrating columns or fields in a database, but at the level of bringing together different approaches 
    		 to a topic and pointing to key data sources. </p>
    		<p style="font-size:90%">Start with a central idea, e.g. <i>storage</i>, branching out to different types of information
    		 that might inform on <i>storage</i>. For example, from <i>storage</i> we might branch out to <i>consumable goods</i>, and from <i>consumable good</i>s
    		 to <i>fodder</i>.<!-- From an environmental specialist's point of view, we might then link from fodder to insect assemblages and 
    		 from there to the SEAD database and to specific species ratios.--></p> 
	    	        </div>
		        </div>

      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" data-dismiss="modal">Got it!</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
    <script src="data/iceland.json"></script>
    <script src="components/jquery/dist/jquery.js"></script>
    <script src="components/jquery-cookie/jquery.cookie.js"></script>
    <script src="components/bootstrap/dist/js/bootstrap.min.js"></script>
    <script src="components/chroma-js/chroma.min.js"></script>
    <script src="components/leaflet/dist/leaflet.js"></script>
    <script src="components/leaflet-pip/leaflet-pip.js"></script>
    
    <script src="components/d3/d3.js"></script>
    <script src="components/c3/c3.js"></script>
    <script src="components/seiyria-bootstrap-slider/dist/bootstrap-slider.min.js"></script>
    
    <script src="js/nabo.js"></script>
    <script src="js/nabo-custom-graphs.js"></script>
    <script src="js/nabo-timeline.js"></script>
    <script src="js/nabo-forcemap.js"></script>
<script>
// GLOBALS:
var max = 800;
var detail = 160;
var shouldContinue = true;

var sources = {
	"ISLEIF"           : { name: 'ISLEIF', color: "red" },
	"SAGAS"            : { name: 'Sagas' , color: "yellow"},
	"SANDAY"           : { name: 'Sanday' , color: "lightgreen"},
	"MORTUARY"         : { name: "Mortuary Database" , color: "black" },
	"FARM HISTORIES"   : { name: "Farm Histories", color : "brown" },
	"EXCAVATED SITE DB": {name: "Excavated Site DB", color:"orange"},
	"PMS"              : { name: "NABO PMS", color: 'darkblue' },
    "NABONE"           : { name: "NABONE", color: 'BlueViolet'},
	"SEAD"             : { name: "SEAD", color: "darkgreen"},
	"TDAR"             : { name: "tDAR", color: "darkred"}
};

var cookieValue = $.cookie("test");
if (!cookieValue) {
	$.cookie("test", 1);
	$('#intro-modal').modal();
}
</script>
<br><br>
<ul>
    <li><a href="?oldjson">View "Success" Topic Map</a></li>
    <li><a href="?">View "Landscape" Topic Map</a></li>
<ul>
</div>
</body>
</html>
	