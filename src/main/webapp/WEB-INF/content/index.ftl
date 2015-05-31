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
    <link rel="stylesheet" href="components/c3/c3.css" />
    <link rel="stylesheet" href="components/seiyria-bootstrap-slider/dist/css/bootstrap-slider.min.css">
    
    <link rel="stylesheet" href="components/leaflet/dist/leaflet.css" />

 <style>
   
   .slider {padding-left:40px;;margin-left:50px}
   .slider-selection {
        background: #BABABA;
    }

   #sigma-container {
    height: 300px;
    margin: auto;
    background-color:#EFEFEF;
   }



.node {
//  stroke: #fff;
//  stroke-width: 1.5px;
}

.link {
  fill: none;
  stroke: #bbb;
}

.nodeLabel {
	font-size: 12px;
	fill: black;
	border:0;
}

.overlay {
  fill: none;
  pointer-events: all;
}

   </style> 
   
   </head>
<body>
<div class="container-fluid">
<div class="row">
<div id="status" style="font-size:10pt" class="col-md-12"></div>
</div>
<div class="row">
    <div id="mapbox"  class="col-md-8">
        <div id="map" style="height: 600px"></div>
    </div>
    <div id="infobox" class="col-md-4">
        <div id="forcemap"></div>
		<button id="zoom_in"><span class="glyphicon glyphicon-zoom-in"></span></button>
		<button id="zoom_out"><span class="glyphicon glyphicon-zoom-out"></span></button>
		
		<button id="up"><span class="glyphicon glyphicon-triangle-top"></span></button>
		<button id="down"><span class="glyphicon glyphicon-triangle-bottom"></span></button>
		<button id="left"><span class="glyphicon glyphicon-triangle-left"></span></button>
		<button id="right"><span class="glyphicon glyphicon-triangle-right"></span></button>
        
        
        <button id="expand"><span class="glyphicon glyphicon-resize-full"></span></button>
        <button id="contract"><span class="glyphicon glyphicon-resize-small"></span></button>
        <div id="infostatus"></div>
        <div id="chart"></div>
        <div id="infodetail"></div>
    
    </div>
</div>
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
     <img src="http://ads.ahds.ac.uk/arena/search/images/per1.gif">
    </form>
    </div>
</div>

    <script src="iceland.json"></script>
    <script src="components/jquery/dist/jquery.js"></script>
    <script src="components/bootstrap/dist/js/bootstrap.min.js"></script>
    <script src="components/chroma-js/chroma.min.js"></script>
    <script src="components/leaflet/dist/leaflet.js"></script>
    <script src="components/leaflet-pip/leaflet-pip.js"></script>
    <script src="components/d3/d3.js"></script>
    <script src="components/c3/c3.js"></script>
    <script src="components/seiyria-bootstrap-slider/dist/bootstrap-slider.min.js"></script>
    <script src="js/bce.js"></script>
    <script src="js/bce-forcemap.js"></script>
<script>
// GLOBALS:
var max = 800;
var detail = 160;
var shouldContinue = true;

</script>
</div>
</body>
</html>
	