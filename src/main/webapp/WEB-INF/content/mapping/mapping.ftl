<!DOCTYPE html PUBLIC 
	"-//W3C//DTD XHTML 1.1 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title><sitemesh:write property='title'/></title>
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
function getContextPath() {
	return "${contextPath}";
}
</script>
</head>
<body id="page-home" data-contextPath="${contextPath}">
            <div id="main" class="container-fluid">
	            <div class="span-md-11">
	            	<h3>DataARC Prototype Application</h3>
	        	</div>
        	</div>
                <hr />
  <div class="container" id="schema">

        <div class="col-sm-12">
	      Source: <select v-model="currentSchema" >
	          <option v-for="(option, index) in schema" v-bind:value="index"> {{ option.name }} </option>
	      </select>
	      <br/>
	      <span v-show="currentSchema != undefined">

	       Indicator: <select v-model="currentIndicator">
	          <optgroup label="Existing Indicators" v-if="indicators.length > 0 ">
	          <option v-for="(option, index) in indicators" v-bind:value="index"> {{ option.name }} </option>
	          </optgroup>
	          <option value="new">Create New Indicator</option>
	      </select>
	      </span>
	      </div>
	        <div class="col-sm-12">
	      
	      <ul v-for="value in uniqueValues">
	       <li><b>{{ value.value }}</b> ({{value.occurrence }})</li>
	      </ul>
	   </div>
	   
  <div class="container-fluid" v-if="fields.length > 0 && currentIndicator === parseInt(currentIndicator)">
    <ul class="list-group" v-for="(part, rowNum) in indicators[currentIndicator].query.conditions">
    	 <spart :rowindex="rowNum" :fields="fields" :part="indicators[currentIndicator].query.conditions[rowNum]" :parts="indicators[currentIndicator].query.conditions"></spart>
	</ul>

    <label>Indicator Name</label><input name="indicatorName" v-model="indicators[currentIndicator].name" />
    <br>
    <label>Indicator Id</label> {{indicators[currentIndicator].id}}

<br/>
<span class='debug hidden'>{{ indicators[currentIndicator] }}</span>
Assign Topic:
        <select name='topic' v-model="indicators[currentIndicator].topicIdentifier">
          <option v-for="(topic, index) in topics"  v-bind:value="topic.identifier"> {{ topic.name }} </option>
        </select>
<br/>
                        <button class="btn btn-xs btn" v-on:click="runQuery()">Search</button>
            <button class="btn btn-xs btn" v-on:click="saveIndicator()">Save Indicator</button>
  </div>

	<table v-if="results != undefined" >
	<tr><td colspan=10">{{ results.length }}</td></tr>
	<tr>
		<th>id</th>
		<th>source</th>
		<th>start</th>
		<th>end</th>
		<th>link</th>
		<th>raw</th>
		</tr>
		<tr v-for="result in results" class="table">
			<td>{{result.id}}</td>
			<td>{{result.properties.source}}</td>
			<td>{{result.properties.Start}}</td>
			<td>{{result.properties.End}}</td>
			<td>{{result.properties['Link']}}</td>
			<td><textarea>{{result.properties | json}}</textarea></td>
		</tr>
	</table>
  </div>

</div>
  
  
  <template id="spart-template">
  	<div>
  	 	<select name='fieldName' v-model="part.fieldName"  v-on:change="updateTest()">
          <option v-for="(field, index) in fields"  v-bind:value="field.name"> {{ field.name }} </option>
        </select>

 	 	<select name='type' v-model="part.type">
	 		<option value="EQUALS">Equals</option>
	 		<option value="DOES_NOT_EQUAL">Does Not Equal</option>
	 		<option value="GREATER_THAN">Greater Than</option>
	 		<option value="LESS_THAN">Less Than</option>
	 		<option value="CONTAINS">Contains</option>
	 	</select>
 	<input name='value' value="" v-bind:type="getHtmlFieldType(part.fieldName)" v-model="part.value"/>
 	<span v-show="rowindex > 0">
 	            <button class="btn btn-xs btn" v-on:click="removePart(rowindex)">-</button>
	</span>
 	 <span v-show="rowindex == parts.length -1">
 	            <button class="btn btn-xs btn" v-on:click="addPart()">+</button>
	</span>
  	  	</div>
  
  </template>
  
  <script src="${contextPath}/js/app/mapping/mapping.js"></script>


<!--
 -->

<template id="spart-template">
hi {{part}}
</template>


        <div id="footer" class="clearfix">
        </div>
        

</body>
</html>
	