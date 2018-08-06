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
      center: [65, -18.5],
      zoom: 5,
      types: ['archaeological', 'textual', 'environmental'],
      styles: {
        'normal': {
          'fillColor': '#000000',
          'color': '#ac6839',
          'weight': 2,
          'opacity': 1,
          'fillOpacity': 0
        },
        'highlight': {
          'fillColor': '#000000',
          'color': '#00dddd',
          'weight': 2,
          'opacity': 1,
          'fillOpacity': 0
        }
      }
    }
    // set the container
    this.container = $(this.settings.container);

    // define the basemap layers
    this.basemaps = {
      'Arctic DEM': L.esri.imageMapLayer({
        url: 'https://elevation2.arcgis.com/arcgis/rest/services/Polar/ArcticDEM/ImageServer',
        renderingRule: {'rasterFunction': 'Hillshade Elevation Tinted'},
        useCors: false
      }),
      'ESRI Streets': L.esri.basemapLayer('Streets'),
      'ESRI Topographic': L.esri.basemapLayer('Topographic'),
      'ESRI Imagery': L.esri.basemapLayer('ImageryClarity'),
      'Google Terrain': L.tileLayer('https://www.google.com/maps/vt?lyrs=p@189&gl=cn&x={x}&y={y}&z={z}', {
        attribution: 'Google'
      }),
      'Google Satellite': L.tileLayer('https://www.google.com/maps/vt?lyrs=s,h@189&gl=cn&x={x}&y={y}&z={z}', {
        attribution: 'Google'
      }),
      'Open Street Map': L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 18,
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
      }),
      'Stamen Terrain': L.tileLayer('https://stamen-tiles-{s}.a.ssl.fastly.net/terrain/{z}/{x}/{y}.{ext}', {
        attribution: 'Map tiles by <a href="http://stamen.com">Stamen Design</a>, <a href="http://creativecommons.org/licenses/by/3.0">CC BY 3.0</a> &mdash; Map data &copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
        subdomains: 'abcd',
        ext: 'png'
      })
    };

    // define the geojson layers and defer the requests
    this.geojson = {};
    this.overlays = {};
    var requests = [];
    for (let geojson of geoJsonInputs) {
      requests.push(
        $.ajax({
          url: geojson.url,
          dataType: 'json',
          success: (data) => {
            this.overlays[geojson.title] = L.geoJSON(data, {
              style: this.settings.styles.normal,
              // for each feature, bind the click
              onEachFeature: (feature, layer) => {
                if (this.geojson[geojson.id] == null) {
                  this.geojson[geojson.id] = geojson;
                  this.geojson[geojson.id].data = data;
                  this.geojson[geojson.id].features = {};
                }
                this.geojson[geojson.id].features[feature.properties.id] = feature;
                layer.source = geojson;
                layer.bindPopup(Loader.medium, {maxWidth: 600});
                layer.on('click', (event) => { this.showPolygonInfo(event); });
              }
            });

            // add the layer to the map
            this.map.addLayer(this.overlays[geojson.title]);
          },
          error: (xhr) => {
            console.log('Cannot load external geojson file.', xhr);
          }
        })
      );
    }

    // initialize the map
    // scrollWheelZoom: false
    this.map = new L.map('map', { center: this.settings.center, zoom: this.settings.zoom, layers: this.basemaps['ESRI Topographic'] });

    // setup the drawing layer for bounding box filters
    L.drawLocal.draw.toolbar.buttons.rectangle = "Create Geographic Filter";
    this.drawnitems = L.featureGroup().addTo(this.map);
    this.drawcontrol = new L.Control.Draw({
      edit: {
        featureGroup: this.drawnitems,
        edit: false,
        remove: false
      },
      draw: {
        polygon: false,
        polyline: false,
        circle: false,
        rectangle: true,
        marker: false,
        circlemarker: false
      }
    });
    this.map.addControl(this.drawcontrol);
    this.map.on(L.Draw.Event.DRAWSTART, (event) => {
      this.map.closePopup();
      this.drawnitems.clearLayers();
    });
    this.map.on(L.Draw.Event.CREATED, (event) => {
      this.drawnitems.addLayer(event.layer);
      this.filterByBounds(event.layer);
    });

    // when all the fetching is finished then add the layers and control to the map
    $.when.apply(null, requests).done(() => {
      // add the layers control to the map
      if (this.layerscontrol != null) this.layerscontrol.remove();
      this.layerscontrol = L.control.layers(this.basemaps, this.overlays);
      this.layerscontrol.addTo(this.map);
      $(document.body).trigger('doneGeographyInit');
    }).fail(function(error) {
      console.log('requests failed', error);
    });
  }

  /**
   * called when you load from save
   */
  reApplyFilter(filter) {
      console.log("geography:: reapply", filter);
      this.drawnitems.clearLayers();
      if (filter.region != undefined) {
          //???
      }
      if (filter.topLeft != undefined) {
          // fixme box in wrong place
          var bounds = [[filter.bottomRight[0], filter.topLeft[1]], [filter.topLeft[0], filter.bottomRight[1]]];
       // create an orange rectangle
          console.log(bounds);
       L.rectangle(bounds).addTo(this.drawnitems);
//          this.drawnitems.addLayer();
      }
      Filters.addSpatial(filter,true);
      
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

    // define the markers layer
    this.markers = {
      'archaeological': L.layerGroup(),
      'textual': L.layerGroup(),
      'environmental': L.layerGroup()
    };

    // loop through the features and create the markers
    for (var i = 0; i < this.features.length; i++) {
      var marker = this.createMarker(this.features[i]);
      marker.addTo(this.markers[marker.type]);
    }

    // create the cluster
    if (this.cluster != null) this.cluster.clearLayers();
    this.cluster = this.createCluster();
    for (let type of this.settings.types) {
      this.cluster.addLayers(this.markers[type]);
    }
    this.map.addLayer(this.cluster);
  }

  wait() {
    this.loader = Loader.large;
    $(this.settings.container).append(this.loader);
  }

  createMarker(feature) {
    var active = (this.matched.indexOf(feature.properties.id) != -1);
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
    marker.on('click', (event) => { this.showMarkerInfo(event); });
    return marker;
  }

  showMarkerInfo(event) {
    var marker = event.target;
    if (!marker.cached) {
      Search.getDetailsById(marker.feature.properties.id, (data) => {
        var feature = data.results.features[0];
        var handler = $('#title-template-' + feature.properties.schema_id).length ? $('#title-template-' + feature.properties.schema_id) : $('#title-template-generic');
        var template = Handlebars.compile(handler.html());
        var content = template(feature.properties);
        marker.setPopupContent(content + '<span class="badge badge-secondary">' + SCHEMA[feature.properties.source] + '</span>');
        marker.cached = true;
      });
    }
  }

  showPolygonInfo(event) {
    var polygon = event.target;
    if (!polygon.cached) {
      var feature = event.target.feature;
      var handler = $('#title-template-polygon');
      var template = Handlebars.compile(handler.html());
      var properties = $.extend({'region': polygon.source.id + '_____' + feature.properties.id}, feature.properties);
      var content = template(properties);
      // polygon.setStyle(this.settings.styles.highlight);
      polygon.setPopupContent(content);
      polygon.cached = true;
    }
  }

  getGeojsonFeature(layerid, featureid) {
    return this.geojson[layerid].features[featureid];
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
      spiderfyOnMaxZoom: true,
      showCoverageOnHover: false,
      zoomToBoundsOnClick: true
    });
  }

  filterByRegion(region) {
    this.map.closePopup();
    if (region == null) return;
    Search.set('spatial', {'region': region});
    this.showClearBtn();
  }

  filterByBounds(layer) {
    var coords = layer.getLatLngs();
    if(coords[0].length == 4){
      var spatial = {
        'topLeft': [coords[0][1].lng, coords[0][1].lat],
        'bottomRight': [coords[0][3].lng, coords[0][3].lat]
      };
      Search.set('spatial', spatial);
      this.showClearBtn();
    }
  }

  showClearBtn() {
    // remove the button if it already exists
    if (this.clearbutton) this.clearbutton.removeFrom(this.map);

    // create the clear filter button
    this.clearbutton = L.easyButton({
      states:[{
        icon: 'fa-ban fa-lg',
        onClick: (btn, map) => { this.clearFilters(); },
        title: 'Clear Geographic Filter'
      }]
    });
    this.clearbutton.addTo(this.map);
  }

  clearFilters() {
    this.map.closePopup();
    this.drawnitems.clearLayers();
    Search.set('spatial', null);
    // remove the clear filter button
    this.clearbutton.removeFrom(this.map);
  }

}
