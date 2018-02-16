'use strict';
/**
 * Geography handler class for data-arc
 * Requires ECMA6, Lodash, jQuery
 */

/**
 * Geography handler
 * @param {Object} settings
 */
class GeographyHandler {
  constructor(settings) {
    this.settings = {
      container: '#map',
      types: ['archaeological', 'textual', 'environmental']
    }
    // set the container
    this.container = $(this.settings.container);

    // define the basemap layers
    this.basemaps = {
      'Streets': L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 18,
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, Points &copy 2012 LINZ'
      }),
      'Terrain': L.tileLayer('https://stamen-tiles-{s}.a.ssl.fastly.net/terrain-background/{z}/{x}/{y}.{ext}', {
        attribution: 'Map tiles by <a href="http://stamen.com">Stamen Design</a>, <a href="http://creativecommons.org/licenses/by/3.0">CC BY 3.0</a> &mdash; Map data &copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
        subdomains: 'abcd',
        ext: 'jpg'
      }),
      'Terrain w/Labels': L.tileLayer('https://stamen-tiles-{s}.a.ssl.fastly.net/terrain/{z}/{x}/{y}.{ext}', {
        attribution: 'Map tiles by <a href="http://stamen.com">Stamen Design</a>, <a href="http://creativecommons.org/licenses/by/3.0">CC BY 3.0</a> &mdash; Map data &copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
        subdomains: 'abcd',
        ext: 'png'
      }),
      'Arctic DEM': L.esri.imageMapLayer({
        url: 'https://elevation2.arcgis.com/arcgis/rest/services/Polar/ArcticDEM/ImageServer',
        renderingRule: {'rasterFunction': 'Hillshade Elevation Tinted'},
        useCors: false
      })
    };

    // define the overlays
    this.overlays = {};
    for (let geojson of geoJsonInputs) {
      this.overlays[geojson.title] = L.layerGroup();

    }

    // initialize the map
    // scrollWheelZoom: false
    var center = L.latLng(-37.79, 175.27);
    this.map = L.map('map', { center: center, zoom: 5, layers: this.basemaps['Streets'] });

    // add the layers control to the map
    if (this.layerscontrol != null) this.layerscontrol.remove();
    this.layerscontrol = L.control.layers(this.basemaps, this.overlays);
    this.layerscontrol.addTo(this.map);

    // set the center and zoom
    this.map.setView([65, -18.5], 5);

    // .on('focus', function() { map.scrollWheelZoom.enable() })
    // .on('blur', function() { map.scrollWheelZoom.disable() })
    // .on('click', function() {
    //   if (map.scrollWheelZoom.enabled()) { map.scrollWheelZoom.disable() } else { map.scrollWheelZoom.enable(); }
    // })

  }

  /**
   * Refresh UI
   * @return {void}
   */
  refresh() {
    this.loader = Loader.large;
    this.features = Search.results['all'].features;
    this.matched = Search.results['matched'].ids;

    // remove the loader
    $(this.settings.container + ' .loader').remove();

    // define the overlays
    this.overlays = {
      'archaeological': L.layerGroup(),
      'textual': L.layerGroup(),
      'environmental': L.layerGroup()
    };

    // loop through the features and create the markers
    for (var i = 0; i < this.features.length; i++) {
      var marker = this.createMarker(this.features[i]);
      marker.addTo(this.overlays[marker.type]);
    }

    // create the cluster
    if (this.cluster != null) this.cluster.clearLayers();
    this.cluster = this.createCluster();
    for (let type of this.settings.types) {
      this.cluster.addLayers(this.overlays[type]);
    }
    this.map.addLayer(this.cluster);
  }

  wait() {
    this.loader = Loader.large;
    $(this.settings.container).append(this.loader);
  }

  createMarker(feature) {
    var active = (this.matched.indexOf(feature.properties.id) != -1);
    // var active = true;
    var type = feature.properties.category.toLowerCase();
    var marker = L.marker(L.latLng(feature.geometry.coordinates[1], feature.geometry.coordinates[0]), {
      icon: L.icon({
        iconUrl: 'img/icons/' + type + (active ? '' : '_gray') + '.png',
        shadowUrl: 'img/icons/shadow.png',
        iconSize: [26, 39],
        shadowSize: [35, 16],
        iconAnchor: [13, 39],
        shadowAnchor: [8, 15],
        popupAnchor: [1, -30]
      })
    });
    marker.active = active;
    marker.type = type;
    marker.feature = feature;
    marker.bindPopup(Loader.medium, {maxWidth: 600});
    marker.on('click', this.showInfo);
    return marker;
  }

  showInfo(event) {
    var marker = event.target;
    if (marker.details == null) {
      Search.getDetailsById(marker.feature.properties.id, (data) => {
        var feature = data.results.features[0];
        var handlebarHandler = $("#title-template-"+ feature.properties.schema_id).length ? $("#title-template-"+ feature.properties.schema_id) : $("#title-template-generic");
        console.log(feature.properties);
        console.log(handlebarHandler.html());
        var template = Handlebars.compile(handlebarHandler.html());
        var content = template(feature.properties);
        marker.setPopupContent(content + "<span class='badge badge-secondary'>"+SCHEMA[feature.properties.source]+"</span>");
        marker.details = feature;
      });
    }
  }

  createCluster() {
    return L.markerClusterGroup({
      maxClusterRadius: 80,
      iconCreateFunction: (cluster) => {
        var counts = {
          'archaeological': 0,
          'archaeological_total': 0,
          'textual': 0,
          'textual_total': 0,
          'environmental': 0,
          'environmental_total': 0,
          'active': 0,
          'total': cluster.getChildCount()
        };
        var markers = cluster.getAllChildMarkers();
        for (var i = 0; i < markers.length; i++) {
          if (markers[i].active) {
            counts[markers[i].type]++;
            counts.active++;
          }
          counts[markers[i].type + '_total']++;
        }
        var size = 30;
        if (counts.active > 99) size = 40;
        if (counts.active > 999) size = 50;
        if (counts.active > 9999) size = 60;
        var html  = '<div class="marker-cluster-archaeological"><span>' + counts.archaeological.toLocaleString() + '</span></div>';
            html += '<div class="marker-cluster-textual"><span>' + counts.textual.toLocaleString() + '</span></div>';
            html += '<div class="marker-cluster-environmental"><span>' + counts.environmental.toLocaleString() + '</span></div>';
            html += '<span>' + counts.active.toLocaleString() + '</span>';
        return L.divIcon({
          html: html,
          className: 'marker-cluster' + (counts.active == 0 ? ' empty' : '') + ' marker-cluster-' + size,
          iconSize: L.point(size, size)
        });
      },
      //Disable all of the defaults:
      spiderfyOnMaxZoom: true,
      showCoverageOnHover: false,
      zoomToBoundsOnClick: true
    });
  }

}


// map = new L.Map("map", { center: [65, -18.5], zoom: 5, scrollWheelZoom: false })
//   .addLayer(basemap)
//   .on('focus', function() { map.scrollWheelZoom.enable() })
//   .on('blur', function() { map.scrollWheelZoom.disable() })
//   // .on('click', function() {
//   //   if (map.scrollWheelZoom.enabled()) { map.scrollWheelZoom.disable() } else { map.scrollWheelZoom.enable(); }
//   // })
//   ;

// map.createPane('activeFeatures');
// map.getPane('activeFeatures').style.zIndex = 450;


// L.drawLocal.draw.toolbar.buttons.rectangle = "Create Geographic Filter";

// var drawnItems = L.featureGroup().addTo(map);

// map.addControl(new L.Control.Draw({
//   edit: {
//     featureGroup: drawnItems,
//     edit: false,
//     remove: false
//   },
//   draw: {
//     polygon: false,
//     polyline: false,
//     circle: false,
//     rectangle: true,
//     marker: false,
//     circlemarker: false
//   }
// }));

// L.easyButton({
//   states:[{
//     icon:'fa-times-rectangle fa-lg',
//     onClick: function(btn, map){
//       Geography.clearFilter();
//       },
//     title:'Clear Geographic Filter'
//   }]
// }).addTo(map);

// map.on(L.Draw.Event.DRAWSTART, function (event) {
//   map.closePopup();
//   Geography.resetStyles();
//   drawnItems.clearLayers();
// });

// map.on(L.Draw.Event.CREATED, function (event) {
//   Geography.filter = event.layer;
//   drawnItems.addLayer(Geography.filter);
//   Geography.rectFilter(Geography.filter);
// });

// var layerControl = L.control.layers({"Terrain":basemap,"Terrain with labels":basemap2,"ArcticDEM":arcticDem},{}).addTo(map);

// // Enables the svg on top of leaflet using d3
// // svg = d3.select(map.getPanes().overlayPane).append("svg");
// // g = svg.append("g").attr("class", "leaflet-zoom-hide");

// var pLayer = function(settings){
//   $.extend(this,settings);
//   this.init();
// };

// pLayer.prototype = {
//   init: function() {
//     this.selected = false;
//     this.get();
//   },
//   get: function() {
//     var _this = this;
//     $.getJSON(this.url,function(data){
//       _this.set(data);
//     });
//   },
//   set: function(data) {
//     var _this = this;
//     this.layer = new L.geoJSON(data, {
//       style: _this.normalStyle,
//       onEachFeature: function(feature,layer){
// 					layer.bindPopup("Loading...");
//         }
//     }).on('click',function(e){
//       if (_this.selected) {
//         e.target.resetStyle(_this.selected)
//       }
//       _this.selected = e.layer;
//       _this.selected.bringToFront();
//       Geography.layer.bringToFront();
//       _this.selected.setStyle(_this.highlightStyle);
// 			_this.handlePolyClick(e);

//     });
//     map.addLayer(this.layer);
//     layerControl.addOverlay(this.layer, this.title);
//   },
//   normalStyle: {
//     "fillColor": "#000",
//     "color": "#AC6839",
//     "weight":2,
//     "opacity":1,
//     "fillOpacity":0
//   },
//   highlightStyle: {
//     "fillColor": "#000",
//     "color": "#0DD",
//     "weight":2,
//     "opacity":1,
//     "fillOpacity":0
//   },
//   resetStyles: function() {
//     this.selected = false;
//     if (this.layer != undefined) {
//         this.layer.setStyle(this.normalStyle);
//     }
//   },
// 	handlePolyClick(e){
// 		var popup = e.layer.getPopup();
// 		var feature = e.layer.feature;
// 		var region = this.id+"_____"+feature.properties.id;
// 		var template = Handlebars.compile($("#title-template-polygon").html());
// 		var properties = $.extend({"region":region},feature.properties);
// 		var content = template(properties);
// 		popup.setContent(content);
//     popup.update();
// 	}

// };

// // requires search.js

// var Geography = {
//   init: function() {
//     this.layer = null;
//     this.values = {};
//     this.filter = false;
//     this.pLayers = {};
//     this.addLayers();
//   },
//   get: function(key) {
//     var value;
//     if (this.values.hasOwnProperty(key)) {
//       value = this.values[key];
//     }
//     return value;
//   },
//   set: function(key, value) {
//     this.values[key] = value;
//   },
//   unset: function(key) {
//     delete this.values[key];
//   },
//   wait: function() {
//     $('#mapSpinner').show();
//   },
//   refresh: function() {
//     this.addFeatures(Search.results['all'].features, {
//       radius: 5,
//       fillColor: "#eee",
//       color: "#000",
//       weight: 1,
//       opacity: 0.4,
//       fillOpacity: 0.1
//     });
//     $('#mapSpinner').hide();
//   },
//   eachFeature: function(feature, layer) {
//   },
//   addFeatures: function(geojson, style) {
//     this.clear();
//     this.layer = L.geoJSON(geojson, {
//       style: styleFunction,
//       onEachFeature: Geography.eachFeature,
//       pointToLayer: function(feature, latlng) {
//         var shp = L.circleMarker(latlng, style);
// 				shp.bindPopup("Loading...");
//         $(shp).data('properties', feature.properties);
//         shp.on('click', _handleCircleClick);
//         return shp;
//       }
//     }).addTo(map);

//     function _handleCircleClick(e) {
//         var popup = e.target.getPopup();
//         var feature = e.target.feature;
//         console.log(e.target.feature.properties);
//         Search.getDetailsById(feature.properties.id, function( data ) {
//           var feature = data.results.features[0];
//           var handlebarHandler = $("#title-template-"+ feature.properties.schema_id).length ? $("#title-template-"+ feature.properties.schema_id) : $("#title-template-generic");
//           console.log(feature.properties);
//           console.log(handlebarHandler.html());
//           var template = Handlebars.compile(handlebarHandler.html());
//           var content = template(feature.properties);
//           popup.setContent(content + "<span class='badge badge-secondary'>"+SCHEMA[feature.properties.source]+"</span>");
//           popup.update();
//         });
//     }
// 		function styleFunction(feature){
//       var fill = "#fff";
//       var color = "#333";
//       var opacity = 1;
//       var fillOpacity = 0.8;
//       var active = Geography.checkActive(feature);
//       var pane = active ? 'activeFeatures' : 'overlayPane';
//       if(typeof Geography.categoryColors[feature.properties.category] != 'undefined' && active){
//         color = fill = Geography.categoryColors[feature.properties.category];
//       }
//       if(!active){
//         opacity = 0.4;
//         fillOpacity = 0.3;
//       }
//       var style = {
//         "fillColor": fill,
//         "color": color,
//         "weight":1,
//         "opacity":opacity,
//         "fillOpacity":fillOpacity,
//         "pane":pane
//       }
//       return style;
//     }
//   },
//   categoryColors: {
//     "ARCHAEOLOGICAL": category_colors[0],
//     "TEXTUAL":category_colors[1],
//     "ENVIRONMENTAL":category_colors[2]
//   },
//   checkActive: function(feature){
//     // return true;
//     results = Search.results['matched'].ids;
//     // results = ["599afd832ab79c0006a35692","599afd832ab79c0006a35693","599afd832ab79c0006a35694","599afd832ab79c0006a35695","599afd832ab79c0006a35696","599afd832ab79c0006a35697","599afd832ab79c0006a35698","599afd832ab79c0006a35699","599afd832ab79c0006a3569a","599afd832ab79c0006a3569b","599afd832ab79c0006a3569c","599afd832ab79c0006a3569d","599afd832ab79c0006a3569e","599afd832ab79c0006a3569f","599afd832ab79c0006a356a0","599afd832ab79c0006a356a1","599afd832ab79c0006a356a2","599afd832ab79c0006a356a3","599afd832ab79c0006a356a4","599afd832ab79c0006a356a5","599afd832ab79c0006a356a6","599afd832ab79c0006a356a7","599afd832ab79c0006a356a8","599afd832ab79c0006a356a9","599afd832ab79c0006a356aa","599afd832ab79c0006a356ab","599afd832ab79c0006a356ac","599afd832ab79c0006a356ad","599afd832ab79c0006a356ae","599afd832ab79c0006a356af","599afd832ab79c0006a356b0","599afd832ab79c0006a356b1","599afd832ab79c0006a356b2","599afd832ab79c0006a356b3","599afd832ab79c0006a356b4","599afd832ab79c0006a356b5","599afd832ab79c0006a356b6","599afd832ab79c0006a356b7","599afd832ab79c0006a356b8","599afd832ab79c0006a356b9","599afd832ab79c0006a356ba","599afd832ab79c0006a356bb","599afd832ab79c0006a356bc","599afd832ab79c0006a356bd","599afd832ab79c0006a356be","599afd832ab79c0006a356bf","599afd832ab79c0006a356c0","599afd832ab79c0006a356c1","599afd832ab79c0006a356c2","599afd832ab79c0006a356c3","599afd832ab79c0006a356c4","599afd832ab79c0006a356c5","599afd832ab79c0006a356c6","599afd832ab79c0006a356c7","599afd832ab79c0006a356c8","599afd832ab79c0006a356c9","599afd832ab79c0006a356ca","599afd832ab79c0006a356cb","599afd832ab79c0006a356cc","599afd832ab79c0006a356cd","599afd832ab79c0006a356ce","599afd832ab79c0006a356cf","599afd832ab79c0006a356d0","599afd832ab79c0006a356d1","599afd832ab79c0006a356d2","599afd832ab79c0006a356d3","599afd832ab79c0006a356d4","599afd832ab79c0006a356d5","599afd832ab79c0006a356d6","599afd832ab79c0006a356d7","599afd832ab79c0006a356d8","599afd832ab79c0006a356d9","599afd832ab79c0006a356da","599afd832ab79c0006a356db","599afd832ab79c0006a356dc","599afd832ab79c0006a356dd","599afd832ab79c0006a356de","599afd832ab79c0006a356df","599afd832ab79c0006a356e0","599afd832ab79c0006a356e1","599afd832ab79c0006a356e2","599afd832ab79c0006a356e3","599afd832ab79c0006a356e4","599afd832ab79c0006a356e5","599afd832ab79c0006a356e6","599afd832ab79c0006a356e7","599afd832ab79c0006a356e8","599afd832ab79c0006a356e9","599afd832ab79c0006a356ea","599afd832ab79c0006a356eb","599afd832ab79c0006a356ec","599afd832ab79c0006a356ed","599afd832ab79c0006a356ee","599afd832ab79c0006a356ef","599afd832ab79c0006a356f0","599afd832ab79c0006a356f1","599afd832ab79c0006a356f2","599afd832ab79c0006a356f3","599afd832ab79c0006a356f4","599afd832ab79c0006a356f5","599afd832ab79c0006a356f6","599afd832ab79c0006a356f7","599afd832ab79c0006a356f8","599afd832ab79c0006a356f9","599afd832ab79c0006a356fa","599afd832ab79c0006a356fb","599afd832ab79c0006a356fc","599afd832ab79c0006a356fd","599afd832ab79c0006a356fe","599afd832ab79c0006a356ff","599afd832ab79c0006a35700","599afd832ab79c0006a35701","599afd832ab79c0006a35702","599afd832ab79c0006a35703","599afd832ab79c0006a35704","599afd832ab79c0006a35705","599afd832ab79c0006a35706","599afd832ab79c0006a35707","599afd832ab79c0006a35708","599afd832ab79c0006a35709","599afd832ab79c0006a3570a","599afd832ab79c0006a3570b","599afd832ab79c0006a3570c","599afd832ab79c0006a3570d","599afd832ab79c0006a3570e","599afd832ab79c0006a3570f","599afd832ab79c0006a35710","599afd832ab79c0006a35711","599afd832ab79c0006a35712","599afd832ab79c0006a35713","599afd832ab79c0006a35714","599afd832ab79c0006a35715","599afd832ab79c0006a35716","599afd832ab79c0006a35717","599afd832ab79c0006a35718","599afd832ab79c0006a35719","599afd832ab79c0006a3571a","599afd832ab79c0006a3571b"];
//     return _.indexOf(results,feature.properties.id) > -1;
//   },
//   rectFilter: function(layer){
//     var coords = layer.getLatLngs();
//     if(coords[0].length == 4){
//       var spatial = {
//         "topLeft":[coords[0][1].lng, coords[0][1].lat],
//         "bottomRight":[coords[0][3].lng, coords[0][3].lat]
//       };
//       Search.set('spatial', spatial);
//     }
//   },
//   regionFilter: function(region){
//     map.closePopup();
//     if(typeof region == 'undefined') return;
//     var spatial = {
//       "region":region
//     };
//     Search.set('spatial',spatial);
//   },
//   resetStyles:function(){
//     _.each(Geography.pLayers,function(pLayer){
//       pLayer.resetStyles();
//     });
//   },
//   clearFilter: function(){
//     map.closePopup();
//     this.resetStyles();
//     if(this.filter) map.removeLayer(this.filter);
//     Search.set('spatial',null);
//     this.filter = false;
//   },
//   clear: function(key) {
//     if (this.layer) {
//       map.removeLayer(this.layer);
//       this.layer = null;
//     }
//   },
//   addLayers: function(){
//     _.each(geoJsonInputs,function(input){
//       Geography.pLayers[input.id] = new pLayer(input);
//     });
//   },

// };
// // Geography.init();
// // Geography = new GeographyObject();