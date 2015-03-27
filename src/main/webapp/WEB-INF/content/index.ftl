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
    <link rel="stylesheet" href="components/bootstrap-slider/slider.css"></script>
    
    <link rel="stylesheet" href="components/leaflet/dist/leaflet.css" />
</head>
<body>
<div class="container-fluid">
<div class="row">
<div id="status" style="font-size:10pt" class="col-md-12"></div>
</div>
<div class="row">
    <div id="mapbox"  class="col-md-6">
        <div id="map" style="width: 1200px; height: 600px"></div>
    </div>
    <div id="infobox" class="col-md-6">
        <div id="infostatus"></div>
        <div id="chart"></div>
        <div id="infodetail"></div>
    
    </div>
</div>
<div class="row">
    <div class="col-md-12">
    <input type="text" name="term" id="term"/>
    <input type="text" class="span2" value="" data-slider-min="-200" data-slider-max="2000" data-slider-step="1" data-slider-value="[800,1200]"
     data-slider-orientation="horizontal" data-slider-selection="after" data-slider-tooltip="show" id="timeslider"
     data-slider-handle="round">
    
    </div>
</div>

    <script src="components/jquery/dist/jquery.js"></script>
    <script src="components/bootstrap/dist/js/bootstrap.min.js"></script>
    <script src="components/leaflet/dist/leaflet.js"></script>
    <script src="components/leaflet-pip/leaflet-pip.js"></script>
    <script src="components/d3/d3.js"></script>
    <script src="components/jquery.preload/jquery.preload.js"></script>
    <script src="components/c3/c3.js"></script>
    <script src="components/bootstrap-slider/bootstrap-slider.js"></script>
    <script src="components/chroma-js/chroma.min.js"></script>
    <script src="js/bce.js"></script>
<script>
// GLOBALS:
var max = 800;
var detail = 160;
var shouldContinue = true;
var ajax;

var hrepp = 

</script>
</div>
</body>
</html>
	