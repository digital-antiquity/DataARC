<!DOCTYPE html PUBLIC 
	"-//W3C//DTD XHTML 1.1 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
	
	<!DOCTYPE html>
<html>
<head>
    <title> Prototype</title>
    <meta charset="utf-8" />

    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="components/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet" media="screen">
    <script src="http://api.simile-widgets.org/timeline/2.3.1/timeline-api.js?bundle=true" type="text/javascript"></script>
    <link rel="stylesheet" href="components/c3/c3.css" />
    <link rel="stylesheet" href="components/seiyria-bootstrap-slider/dist/css/bootstrap-slider.min.css">
	<link rel="stylesheet" href="css/bce.css"/>    
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
					<!--    <label for='showAll'><input type="checkbox" name="showAll" id="showAll" class="form-control" /> Show All Points</label> -->
				    <span>&nbsp;Limit by year:</span>
				            <input data-slider-id='ex1Slider' type="text" data-slider-min="0" data-slider-max="2000" data-slider-step="1" data-slider-value="[800,1200]"
				     data-slider-orientation="horizontal" data-slider-selection="after" data-slider-tooltip="show" id="timeslider"
				     data-slider-handle="round">
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
        <div id="infostatus"></div>
        <div id="chart"></div>
        <div id="infodetail"></div>
    
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
					<div class="col-md-6">
					<b>Explore by map</b>
	    	        </div>
					<div class="col-md-6">
					<b>Explore by topic</b>
					<br/>
			<p>Archaeologists draw on a wide variety of data to investigate any topic. The concept maps are intended to aid in the process of gathering together all the specialist data that may be relevant to thinking about a topic. They point first to linked topics, and then to the available data sources and key indicators within those data sources that would be relevant to that topic. The aim within the cyberNABO project is to make specialists more aware of the most relevant aspects of one another’s data to shared research questions, and to tease out how these data map together, not at the level of integrating columns or fields in a database, but at the level of bringing together different approaches to a topic and pointing to key data sources. </p>
			<p>Navigate the concept map by starting with a central idea, e.g. storage, branching out to different types of information that might inform on storage. For example, from storage we might branch out to consumable goods, and from consumable goods to fodder. From an environmental specialist's point of view, we might then link from fodder to insect assemblages and from there to the SEAD database and to specific species ratios.</p> 
	    	        </div>
		        </div>
				<div class="row">
					<div class="col-md-12">
	    		      <img src="images/preview-small.png"/>
	    	        </div>
		       </div>
		<br/>

      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" data-dismiss="modal">Got it!</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
    <script src="iceland.json"></script>
    <script src="components/jquery/dist/jquery.js"></script>
    <script src="components/jquery-cookie/jquery.cookie.js"></script>
    <script src="components/bootstrap/dist/js/bootstrap.min.js"></script>
    <script src="components/chroma-js/chroma.min.js"></script>
    <script src="components/leaflet/dist/leaflet.js"></script>
    <script src="components/leaflet-pip/leaflet-pip.js"></script>
    
    <script src="components/d3/d3.js"></script>
    <script src="components/c3/c3.js"></script>
    <script src="components/seiyria-bootstrap-slider/dist/bootstrap-slider.min.js"></script>
    
    <script src="js/bce.js"></script>
    <script src="js/bce-timeline.js"></script>
    <script src="js/bce-forcemap.js"></script>
<script>
// GLOBALS:
var max = 800;
var detail = 160;
var shouldContinue = true;
var cookieValue = $.cookie("test");
if (!cookieValue) {
	$.cookie("test", 1);
	$('#intro-modal').modal();
}
</script>
</div>
</body>
</html>
	