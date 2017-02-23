<!DOCTYPE html PUBLIC 
    "-//W3C//DTD XHTML 1.1 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <title>
            Indicator Creation Tool
        </title>
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
        <script src="${contextPath}/components/vee-validate/dist/vee-validate.min.js"></script>
        <script src="${contextPath}/components/vue-resource/dist/vue-resource.min.js"></script>
        <link href="${contextPath}/components/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet" media="screen">
        <script src="${contextPath}/components/jquery/dist/jquery.js"></script>
        <script src="${contextPath}/components/bootstrap/dist/js/bootstrap.min.js"></script>
        <script>
            function getContextPath() {
                return "${contextPath}";
            }
        </script>
        <style>
            /*
            * Base structure
            */
            /* Move down content because we have a fixed navbar that is 50px tall */
            body {
            padding-top: 0px;
            }
            /*
            * Global add-ons
            */
            .sub-header {
            padding-bottom: 10px;
            border-bottom: 1px solid #eee;
            }
            /*
            * Top navigation
            * Hide default border to remove 1px line.
            */
            .navbar-fixed-top {
            border: 0;
            }
            /*
            * Sidebar
            */
            /* Hide for mobile, show later */
            .sidebar {
            display: none;
            }
            @media (min-width: 768px) {
            .sidebar {
            position: fixed;
            top:0px;
            bottom: 0;
            left: 0;
            z-index: 1000;
            display: block;
            padding: 20px;
            overflow-x: hidden;
            overflow-y: auto; /* Scrollable contents if viewport is shorter than content. */
            background-color: #A9C9DC;
            border-right: 1px solid #eee;
            }
            }
            /* Sidebar navigation */
            .nav-sidebar {
            margin-right: -21px; /* 20px padding + 1px border */
            margin-bottom: 20px;
            margin-left: -20px;
            }
            .nav-sidebar > li > a {
            padding-right: 20px;
            padding-left: 20px;
            }
            .nav-sidebar > .active > a,
            .nav-sidebar > .active > a:hover,
            .nav-sidebar > .active > a:focus {
            color: #fff;
            background-color: #428bca;
            }
            /*
            * Main content
            */
            .main {
            padding: 20px;
            }
            @media (min-width: 768px) {
            .main {
            padding-right: 40px;
            padding-left: 40px;
            }
            }
            .main .page-header {
            margin-top: 0;
            }
            /*
            * Placeholder dashboard ideas
            */
            .placeholders {
            margin-bottom: 30px;
            text-align: center;
            }
            .placeholders h4 {
            margin-bottom: 0;
            }
            .placeholder {
            margin-bottom: 20px;
            }
            .placeholder img {
            display: inline-block;
            border-radius: 50%;
            }
        </style>
    </head>
    <body data-contextPath="${contextPath}">
        <div class="container-fluid">
            <div class="row">
                <div class="col-sm-3 col-md-2 sidebar">
                    <img src="${contextPath}/images/dataarc_logo_final.png" alt="DataARC Logo" class="img-responsive"/>
                    <ul class="nav nav-sidebar">
                        <li><a href="#">Indicators</a></li>
                        <li><a href="#">Admin</a></li>
                    </ul>
                    <!--          <ul class="nav nav-sidebar">
                        <li><a href="">Nav item</a></li>
                        <li><a href="">Nav item again</a></li>
                        <li><a href="">One more nav</a></li>
                        <li><a href="">Another nav item</a></li>
                        <li><a href="">More navigation</a></li>
                        </ul>
                        <ul class="nav nav-sidebar">
                        <li><a href="">Nav item again</a></li>
                        <li><a href="">One more nav</a></li>
                        <li><a href="">Another nav item</a></li>
                        </ul> -->
                </div>
                <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main form-inline">
                    <h1 class="page-header">Indicator Mapping</h1>
                    <div class="container-fluid" id="schema">

                        <div class="row">
                            <div class="col-sm-11">
                                <label for="datasource" class="control-label col-sm-3">Choose a Data Source:</label> 
                                <select v-model="currentSchema" id="datasource" class="form-control" >
                                    <option v-for="(option, index) in schema" v-bind:value="index"> {{ option.name }} </option>
                                </select>
                            </div>
                            <div class="col-sm-1">
                                <span class="glyphicon glyphicon-question-sign" aria-hidden="true"
                                  data-toggle="tooltip" title="Indicators are built from a single data source"></span>
                            </div>
                        </div>
                        <div class="row" v-show="currentSchema != undefined">
                            <div class="col-sm-11">
                                    <label for="choose-indicator" class="control-label col-sm-3">Indicator:</label> 
                                    <select v-model="currentIndicator" id="choose-indicator" class="form-control">
                                        <optgroup label="Existing Indicators" v-if="indicators.length > 0 ">
                                            <option v-for="(option, index) in indicators" v-bind:value="index"> {{ option.name }} </option>
                                        </optgroup>
                                        <option value="new">Create New Indicator</option>
                                    </select>
                                <span  v-if="fields.length > 0 && currentIndicator === parseInt(currentIndicator) && currentIndicator > 0">
                                        <label for="indicatorId" class="control-label col-sm-3">Indicator Id:</label> {{indicators[currentIndicator].id}}
                                </span>

                            </div>
                            <div class="col-sm-1">
                                <span class="glyphicon glyphicon-question-sign" aria-hidden="true"
                                    data-toggle="tooltip" title="Select an exsting indicator, or create a new one" ></span>
                            </div>
                        </div>

                        <div class="row"  v-if="fields.length > 0 && currentIndicator === parseInt(currentIndicator)">
                        <div class="col-sm-11">
                                <label for="indicatorName" class="control-label col-sm-3">Indicator Name:</label>
                                <input id="indicatorName" name="indicatorName" v-model="indicators[currentIndicator].name" class="form-control"/>

                            </div>
                            <div class="col-sm-1">
                            <span class="glyphicon glyphicon-question-sign" aria-hidden="true"
                                    data-toggle="tooltip" title="Choose a unique title for your indicator" ></span>
                            </div>
                        </div>

                        <div class="row" v-show="currentIndicator != undefined">
                            <div class="col-sm-11 col-sm-offset-1">
                                <ul v-for="value in uniqueValues">
                                    <li><b>{{ value.value }}</b> ({{value.occurrence }})</li>
                                </ul>
                            </div>
                        </div>
                        <div class="row"  v-if="fields.length > 0 && currentIndicator === parseInt(currentIndicator)">
                            <div class="col-sm-10 col-sm-offset-1">
                                <ul class="list-group" v-for="(part, rowNum) in indicators[currentIndicator].query.conditions">
                                    <spart :rowindex="rowNum" :fields="fields" :part="indicators[currentIndicator].query.conditions[rowNum]" :parts="indicators[currentIndicator].query.conditions"></spart>
                                </ul>
                            </div>
                            <div class="col-sm-1">
                            <span class="glyphicon glyphicon-question-sign" aria-hidden="true"
                                data-toggle="popover" title="Indicator Queries" data-content="Queries for indicators can be simple or complex, and utilize the combination of fields and values"></span>
                            </div>
                        </div>

                        <div class="row"  v-if="fields.length > 0 && currentIndicator === parseInt(currentIndicator)">
                            <div class="col-sm-10 col-sm-offset-1">
                                
                            </div>
                            <div class="col-sm-1">
                            <span class="glyphicon glyphicon-question-sign" aria-hidden="true"
                                data-toggle="popover" title="Indicator Queries" data-content="Queries for indicators can be simple or complex, and utilize the combination of fields and values"></span>
                            </div>
                        </div>

                        <span class='debug hidden'>{{ indicators[currentIndicator] }}</span>

                        <div class="row"  v-if="fields.length > 0 && currentIndicator === parseInt(currentIndicator)">
                            <div class="col-sm-11">
                                <label for="chooseTopic" class="control-label col-sm-3">Assign Topic:</label>
                                <br/>
                                <!-- fixme: was indicators[currentIndicator].topicIdentifers[_idx]  -->
    							<ul class="list-unstyled">
                                    <li v-for="(ident, _idx) in selectedTopics"  >
                                            <select id="chooseTopic" name='topic' v-model="selectedTopics[_idx]"  class="form-control">
                                                <option v-for="(topic, index) in topics"  v-bind:value="topic.identifier"> {{ topic.name }} </option>
                                            </select>
                                    <span v-show="_idx > 0">
        	                            <button class="btn btn-xs btn-default" v-on:click="removeTopic(_idx)">-</button>
                                    </span>
        
                                    <span v-show="_idx == selectedTopics.length -1">
        	                            <button class="btn btn-xs btn-default" v-on:click="addTopic()">+</button>
                                    </span>
        
                                    </li>
                                </ul>
                            </div>
                            <div class="col-sm-1">
                                <span class="glyphicon glyphicon-question-sign" aria-hidden="true"></span>
                            </div>
                        </div>
                        <br/>
                        <div class="row"  v-if="fields.length > 0 && currentIndicator === parseInt(currentIndicator)">
                            <div class="col-sm-12">
                                <button class="btn btn-xs btn-default" v-on:click="runQuery()" v-bind:disabled='cannotSearch'>Search / Test Query</button>
                                <button class="btn btn-xs btn-success" v-on:click="saveIndicator()" v-bind:disabled='cannotSubmit'>Save Indicator</button>
                                <button class="btn btn-xs btn-danger" v-on:click="deleteIndicator()" >Delete Indicator</button>
                            </div>
                        </div>
                        <br/>
                            <table v-if="results != undefined" class="table">
                            <thead>
                                <tr>
                                    <td colspan=10">Search Results: {{ results.length }}</td>
                                </tr>
                                <tr>
                                    <th>id</th>
                                    <th>source</th>
                                    <th>start</th>
                                    <th>end</th>
                                    <th>link</th>
                                    <th>raw</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr v-for="result in results" class="table">
                                    <td>{{result.id}}</td>
                                    <td>{{result.properties.source}}</td>
                                    <td>{{result.properties.Start}}</td>
                                    <td>{{result.properties.End}}</td>
                                    <td><a v-show="result.properties['Link'] != undefined && result.properties['Link'] != ''" target="_blank" v-bind:href="result.properties['Link']"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a></td>
                                    <td><textarea>{{result.properties | json}}</textarea></td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                        </div>
                        
                    <template id="spart-template">
                        <div>
                            <select name='fieldName' v-model="part.fieldName"  v-on:change="updateTest()" class="form-control">
                                <option v-for="(field, index) in fields"  v-bind:value="field.name"> {{ field.name }} </option>
                            </select>
                            <select name='type' v-model="part.type" class="form-control">
                                <option v-for="(limit, index) in getLimits()" v-bind:value="limit.value"> {{ limit.text }} </option>
                            </select>
                            <input name='value' value="" v-bind:type="getHtmlFieldType(part.fieldName)" v-model="part.value" class="form-control"/>
                            <span v-show="rowindex > 0">
                            <button class="btn btn-xs btn-default" v-on:click="removePart(rowindex)">-</button>
                            </span>
                            <span v-show="rowindex == parts.length -1">
                            <button class="btn btn-xs btn-default" v-on:click="addPart()">+</button>
                            </span>
                        </div>
                    </template>
                    <script src="${contextPath}/js/app/mapping/mapping.js"></script>
                    <!--
                        -->
                    <template id="spart-template">
                        hi {{part}}
                    </template>
                </div>
            </div>
        </div>
    </body>
</html>