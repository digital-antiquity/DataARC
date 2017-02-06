<!DOCTYPE html PUBLIC 
	"-//W3C//DTD XHTML 1.1 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
	
	<!DOCTYPE html>
<meta charset="utf-8">
<html>
    <head>
        <title>DataARC Prototype</title>
        <meta charset="utf-8" />
    
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <script src="http://api.simile-widgets.org/timeline/2.3.1/timeline-api.js?bundle=true" type="text/javascript"></script>
        <link rel="stylesheet" href="${contextPath}/components/c3/c3.css" />
        <link rel="stylesheet" href="${contextPath}components/seiyria-bootstrap-slider/dist/css/bootstrap-slider.min.css">
    	<link rel="stylesheet" href="${contextPath}/css/nabo.css"/>    
        <link rel="stylesheet" href="${contextPath}/components/leaflet/dist/leaflet.css" />
<!--        <link rel="stylesheet" href="${contextPath}/components/leaflet-cluster/dist/MarkerCluster.css" />
        <link rel="stylesheet" href="${contextPath}/components/leaflet-cluster/dist/MarkerCluster.Default.css" />-->
        <link rel="stylesheet" href="${contextPath}/components/prune-cluster/dist/LeafletStyleSheet.css" />

<script>
var _rollbarConfig = {
    accessToken: "b0b1c2d1df09471c842da3da26a9ec49",
    captureUncaught: true,
    payload: {
        environment: "test"
    }
};
!function(r){function e(t){if(o[t])return o[t].exports;var n=o[t]={exports:{},id:t,loaded:!1};return r[t].call(n.exports,n,n.exports,e),n.loaded=!0,n.exports}var o={};return e.m=r,e.c=o,e.p="",e(0)}([function(r,e,o){"use strict";var t=o(1).Rollbar,n=o(2);_rollbarConfig.rollbarJsUrl=_rollbarConfig.rollbarJsUrl||"https://d37gvrvc0wt4s1.cloudfront.net/js/v1.9/rollbar.min.js";var a=t.init(window,_rollbarConfig),i=n(a,_rollbarConfig);a.loadFull(window,document,!_rollbarConfig.async,_rollbarConfig,i)},function(r,e){"use strict";function o(r){return function(){try{return r.apply(this,arguments)}catch(e){try{console.error("[Rollbar]: Internal error",e)}catch(o){}}}}function t(r,e,o){window._rollbarWrappedError&&(o[4]||(o[4]=window._rollbarWrappedError),o[5]||(o[5]=window._rollbarWrappedError._rollbarContext),window._rollbarWrappedError=null),r.uncaughtError.apply(r,o),e&&e.apply(window,o)}function n(r){var e=function(){var e=Array.prototype.slice.call(arguments,0);t(r,r._rollbarOldOnError,e)};return e.belongsToShim=!0,e}function a(r){this.shimId=++c,this.notifier=null,this.parentShim=r,this._rollbarOldOnError=null}function i(r){var e=a;return o(function(){if(this.notifier)return this.notifier[r].apply(this.notifier,arguments);var o=this,t="scope"===r;t&&(o=new e(this));var n=Array.prototype.slice.call(arguments,0),a={shim:o,method:r,args:n,ts:new Date};return window._rollbarShimQueue.push(a),t?o:void 0})}function l(r,e){if(e.hasOwnProperty&&e.hasOwnProperty("addEventListener")){var o=e.addEventListener;e.addEventListener=function(e,t,n){o.call(this,e,r.wrap(t),n)};var t=e.removeEventListener;e.removeEventListener=function(r,e,o){t.call(this,r,e&&e._wrapped?e._wrapped:e,o)}}}var c=0;a.init=function(r,e){var t=e.globalAlias||"Rollbar";if("object"==typeof r[t])return r[t];r._rollbarShimQueue=[],r._rollbarWrappedError=null,e=e||{};var i=new a;return o(function(){if(i.configure(e),e.captureUncaught){i._rollbarOldOnError=r.onerror,r.onerror=n(i);var o,a,c="EventTarget,Window,Node,ApplicationCache,AudioTrackList,ChannelMergerNode,CryptoOperation,EventSource,FileReader,HTMLUnknownElement,IDBDatabase,IDBRequest,IDBTransaction,KeyOperation,MediaController,MessagePort,ModalWindow,Notification,SVGElementInstance,Screen,TextTrack,TextTrackCue,TextTrackList,WebSocket,WebSocketWorker,Worker,XMLHttpRequest,XMLHttpRequestEventTarget,XMLHttpRequestUpload".split(",");for(o=0;o<c.length;++o)a=c[o],r[a]&&r[a].prototype&&l(i,r[a].prototype)}return e.captureUnhandledRejections&&(i._unhandledRejectionHandler=function(r){var e=r.reason,o=r.promise,t=r.detail;!e&&t&&(e=t.reason,o=t.promise),i.unhandledRejection(e,o)},r.addEventListener("unhandledrejection",i._unhandledRejectionHandler)),r[t]=i,i})()},a.prototype.loadFull=function(r,e,t,n,a){var i=function(){var e;if(void 0===r._rollbarPayloadQueue){var o,t,n,i;for(e=new Error("rollbar.js did not load");o=r._rollbarShimQueue.shift();)for(n=o.args,i=0;i<n.length;++i)if(t=n[i],"function"==typeof t){t(e);break}}"function"==typeof a&&a(e)},l=!1,c=e.createElement("script"),p=e.getElementsByTagName("script")[0],d=p.parentNode;c.crossOrigin="",c.src=n.rollbarJsUrl,c.async=!t,c.onload=c.onreadystatechange=o(function(){if(!(l||this.readyState&&"loaded"!==this.readyState&&"complete"!==this.readyState)){c.onload=c.onreadystatechange=null;try{d.removeChild(c)}catch(r){}l=!0,i()}}),d.insertBefore(c,p)},a.prototype.wrap=function(r,e){try{var o;if(o="function"==typeof e?e:function(){return e||{}},"function"!=typeof r)return r;if(r._isWrap)return r;if(!r._wrapped){r._wrapped=function(){try{return r.apply(this,arguments)}catch(e){throw"string"==typeof e&&(e=new String(e)),e._rollbarContext=o()||{},e._rollbarContext._wrappedSource=r.toString(),window._rollbarWrappedError=e,e}},r._wrapped._isWrap=!0;for(var t in r)r.hasOwnProperty(t)&&(r._wrapped[t]=r[t])}return r._wrapped}catch(n){return r}};for(var p="log,debug,info,warn,warning,error,critical,global,configure,scope,uncaughtError,unhandledRejection".split(","),d=0;d<p.length;++d)a.prototype[p[d]]=i(p[d]);r.exports={Rollbar:a,_rollbarWindowOnError:t}},function(r,e){"use strict";r.exports=function(r,e){return function(o){if(!o&&!window._rollbarInitialized){var t=window.RollbarNotifier,n=e||{},a=n.globalAlias||"Rollbar",i=window.Rollbar.init(n,r);i._processShimQueue(window._rollbarShimQueue||[]),window[a]=i,window._rollbarInitialized=!0,t.processPayloads()}}}}]);
</script>


    <script src="${contextPath}/components/vue/dist/vue.min.js"></script>
    <script src="${contextPath}/components/vue-resource/dist/vue-resource.min.js"></script>

    <link href="${contextPath}/components/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet" media="screen">

    <script src="${contextPath}/components/jquery/dist/jquery.js"></script>
    <script src="${contextPath}/components/bootstrap/dist/js/bootstrap.min.js"></script>

<script>
function getContext() {
	return "${contextPath}";
}
</script>

   </head>
<body>

            <div id="main" class="container-fluid">
	            <div class="span-md-11">
	            	<h3>DataARC Prototype Application</h3>
	        	</div>
        	</div>
                <hr />

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
				     <span class="">&nbsp;&nbsp;&nbsp;<input id="cluster" type="checkbox">&nbsp;<label for="cluster">cluster results?</label></span>
				     <br/>
				     <!--<img src="http://ads.ahds.ac.uk/arena/search/images/per1.gif">-->
			           <div id="tl" class="timeline-default" style="height: 400px;"></div>

				     
			    </form>
			</div>
	</div>
        
    </div>
    <div id="infobox" class="col-md-4">
    <ul class="nav nav-tabs">
	  <li role="presentation" id="successTab" class=""><a href="?oldjson">Success</a></li>
	  <li role="presentation" id="landscapeTab" class=""><a href="?">Landscape</a></li>
	</ul>
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
        <h4 class="modal-title">Welcome to the DataARC Prototype</h4>
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
    <script src="${contextPath}/components/jquery-cookie/jquery.cookie.js"></script>
    <script src="${contextPath}/components/chroma-js/chroma.min.js"></script>
    <script src="${contextPath}/components/leaflet/dist/leaflet.js"></script>
    <script src="${contextPath}/components/leaflet-pip/leaflet-pip.js"></script>
    
    <script charset="utf-8" src="${contextPath}/components/d3/d3.js"></script>
    <script src="${contextPath}/components/c3/c3.js"></script>
    <script src="${contextPath}/components/seiyria-bootstrap-slider/dist/bootstrap-slider.min.js"></script>
    <!-- <script src="${contextPath}/components/leaflet-cluster/dist/leaflet.markercluster.js"></script> -->
    <script src="${contextPath}/components/prune-cluster/dist/PruneCluster.js"></script>
    
    <script src="${contextPath}/js/nabo.js"></script>
    <script src="${contextPath}/js/nabo-custom-graphs.js"></script>
    <script src="${contextPath}/js/nabo-timeline.js"></script>
    <script src="${contextPath}/js/nabo-forcemap.js"></script>
<script>
// GLOBALS:
var max = 800;
var detail = 160;
var shouldContinue = true;

var sources = {
	"ISLEIF"           : { name: 'ISLEIF', color: "red", idx:0 },
	"SAGAS"            : { name: 'Sagas' , color: "yellow", idx:1},
	"SANDAY"           : { name: 'Sanday' , color: "lightgreen", idx:2},
	"MORTUARY"         : { name: "Mortuary Database" , color: "black" , idx:3},
	"FARM HISTORIES"   : { name: "Farm Histories", color : "brown", idx:4 },
	"EXCAVATED SITE DB": {name: "Excavated Site DB", color:"orange", idx:5},
	"PMS"              : { name: "NABO PMS", color: 'darkblue' , idx:6},
    "NABONE"           : { name: "NABONE", color: 'BlueViolet', idx:7},
	"SEAD"             : { name: "SEAD", color: "darkgreen", idx:8},
	"TDAR"             : { name: "tDAR", color: "darkred", idx:9}
};

var cookieValue = $.cookie("test");
if (!cookieValue) {
	$.cookie("test", 1);
	$('#intro-modal').modal();
}

$(function() {
    if (location.search && location.search.toLowerCase().indexOf("oldjson") > -1) {
        initForceMapJSON();
		$("#successTab").addClass("active");
    } else {
		$("#landscapeTab").addClass("active");
        initForceMapXml();
    }
	
	if (location.search && location.search.toLowerCase().indexOf("expand=forcemap") > -1) {
		$("#expand").click();
	}
});

</script>
</div>


        <div id="footer" class="clearfix">
        </div>
        

</body>
</html>
	