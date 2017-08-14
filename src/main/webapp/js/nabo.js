/**
 * This script focuses on the initialization and setup of the Leaflet Maps API and the general interactions with the maps.
 */

var map;
var activeId = -1;
var popup = L.popup();
var geoLayer = undefined;
var hlayer;
var time = 0;
var UNHIGHLIGHTED = .3;
var NORTH, SOUTH, EAST, WEST;
var grid = false;
var start = -9999;
var end = 9999;
var chromaScale;
var showAllPoints = 1;
var srcs = {};
// default style for map objects
var myStyle = {
    "color" : "#ff7800",
    "weight" : 1,
    "opacity" : 1
};

var RedIcon = L.Icon.Default.extend({
    options : {
        iconUrl : 'marker-icon-red.png'
    }
});

var BlueIcon = L.Icon.Default.extend({});

/**
 * Initialize the Map.
 */
function init() {
    // create the map and center it around Iceland
    map = L.map('map').setView([ 66.16495058, -16.68273926 ], 5);

    // setup a color scale for the legend
    chromaScale = chroma.scale([ 'white', 'red' ]);

    setupBaseLayers();
    setupMapShape();

    resetGrid();
    drawGrid();
    attachMapEvents();
    addLegend();
}

/**
 * Add the legend to the map
 */
function addLegend() {

    var legend = L.control({
        position : 'bottomright'
    });

    legend.onAdd = function(map) {
        // for ranges between 0 & 25, add labels

        var div = L.DomUtil.create('div', 'info legend'), grades = [ 0, 5, 10, 15, 20, 25 ], labels = [];

        // loop through our density intervals and generate a label with a colored square for each interval
        for (var i = 0; i < grades.length; i++) {
            div.innerHTML += '<i style="opacity: ' + UNHIGHLIGHTED + '; background:' + chromaScale(grades[i] / 25).hex() + '"></i> ' + grades[i] +
                    (grades[i + 1] ? '&ndash;' + grades[i + 1] + '<br>' : '+');
        }

        return div;
    };

    legend.addTo(map);


    var legend2 = L.control({
        position : 'topright'
    });

    legend2.onAdd = function(map) {
        // for ranges between 0 & 25, add labels

        var div2 = L.DomUtil.create('div', 'info legend');
        
        // loop through our density intervals and generate a label with a colored square for each interval
        for (source in sources) {
            if (!sources.hasOwnProperty(source)) {
                continue;
            }
            div2.innerHTML += '<div ><i style="opacity: ' + UNHIGHLIGHTED + '; background:' +sources[source].color +'"></i> ' +
            "<input class=typeselect type=checkbox name=source id='c"+source+"' value=" + source +" checked><label for='c"+source+"'> &nbsp; " + sources[source].name + "</label></div> ";
        };
        div2.innerHTML += '<div ><i style="background:white"></i> ' +
        "<input type=checkbox name=source id='sall'> Select All</div> ";

        return div2;
    };

    legend2.addTo(map);
    $(".typeselect").click(drawGrid);
    
    $("#sall").click(function(){
        if ($(".typeselect:not(:checked)").length == 0) {
            $(".typeselect").prop('checked',false);
        } else {
            $(".typeselect").prop('checked',true);
        }
    });
}

/**
 * For each layer of the map, change the opacity based on whether we show points or not (this works for all things with an _latlng (e.g. points)
 */
var changeAllOpacity = function() {
    map.eachLayer(function(l) {
        if (l._latlng) {
            geoLayer.getLayer(l._leaflet_id).setStyle({
                fillOpacity : showAllPoints
            });
        }
    });
}
/**
 * FInds all points inside a shape. It then returns two arrays, one for the points inside, one for those outside.
 */
var findInside = function(ly) {
    var points = new Array();
    var outside = new Array();
    map.eachLayer(function(l) {
        if (l._latlng != undefined) {
            var val = inside([ l._latlng.lng, l._latlng.lat ], ly);
            if (l._icon != undefined || l._radius) {
                if (val) {
                    points.push(l);
                } else {
                    outside.push(l);
                }
            }
        }
    });
    return {
        "inside" : points,
        "outside" : outside
    };
}

/**
 * For a point and a list of points, return whether the point is inside that shape
 */
var inside = function(point, vs) {
    // ray-casting algorithm based on
    // http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html

    var x = point[0], y = point[1];

    var inside = false;
    for (var i = 0, j = vs.length - 1; i < vs.length; j = i++) {
        var xi = vs[i][0], yi = vs[i][1];
        var xj = vs[j][0], yj = vs[j][1];

        var intersect = ((yi > y) != (yj > y)) && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
        if (intersect)
            inside = !inside;
    }

    return inside;
};

/**
 * Event capture for when a feature is moused-over, it throws a custom event that allows us to handle mouse-over of layered objects without causing focus to be
 * lost or for the screen to flicker
 */
function highlightFeature(e) {
    if (e.target.parent) {
        var id = e.target.parent._leaflet_id;
        if (geoLayer) {
            geoLayer.getLayer(e.target._leaflet_id).setStyle({
                fillOpacity : 1
            });
        }
        $('#map').trigger("highlight:on", [ hlayer, id ]);
    }
}

/**
 * Sets the opacity/highlight back to normal
 * 
 * @param e
 */
function resetHighlight(e) {
    geoLayer.getLayer(e.target._leaflet_id).setStyle({
        fillOpacity : UNHIGHLIGHTED
    });
    try {
        geoLayer.resetStyle(e.target);
    } catch (e) {}
 }


var markers;

function cluster() {
    return $("#cluster:checked").length > 0;
}



L.Icon.MarkerCluster = L.Icon.extend({
    options: {
        iconSize: new L.Point(48, 48),
        className: 'prunecluster leaflet-markercluster-icon'
    },
    createIcon: function () {
        // based on L.Icon.Canvas from shramov/leaflet-plugins (BSD licence)
        var e = document.createElement('canvas');
        this._setIconStyles(e, 'icon');
        var s = this.options.iconSize;
        e.width = s.x;
        e.height = s.y;
        this.draw(e.getContext('2d'), s.x, s.y);
        return e;
    },
    createShadow: function () {
        return null;
    },
    draw: function(canvas, width, height) {
        var lol = 0;
        var pi2 = Math.PI * 2;
        var start = 0;
        var i =0;
        for (category in sources) {
            if (!sources.hasOwnProperty(category)) {
                continue;
            }
            i++;
            var size = this.stats[category] / this.population;
//            console.log(i + " : " + this.stats[i] + " : " + this.population + " : " + size);
            if (size > 0) {
                canvas.beginPath();
                var angle = Math.PI/4*i;
                var posx = Math.cos(angle) * 18, posy = Math.sin(angle) * 18;
                var xa = 0, xb = 1, ya = 4, yb = 8;
                // var r = ya + (size - xa) * ((yb - ya) / (xb - xa));
                var r = ya + size * (yb - ya);
                //canvas.moveTo(posx, posy);
                canvas.arc(24+posx,24+posy, r, 0, pi2);
                canvas.fillStyle = sources[category].color;
                canvas.fill();
                canvas.closePath();
            }
        }
        canvas.beginPath();
        canvas.fillStyle = 'white';
        canvas.arc(24, 24, 16, 0, Math.PI*2);
        canvas.fill();
        canvas.closePath();
        canvas.fillStyle = '#555';
        canvas.textAlign = 'center';
        canvas.textBaseline = 'middle';
        canvas.font = 'bold 12px sans-serif';
        canvas.fillText(this.population, 24, 24, 48);
    }
});


/**
 * for each feature, add it to the map, also setup the click event which shows detail in the #infodetail div
 * 
 * @param feature
 * @param layer
 */
function addPointsToMap(feature, layer) {
    if (cluster()) {
        var ll = feature.geometry.coordinates;
        var marker = new PruneCluster.Marker(ll[1],ll[0]);
        marker.category = feature.properties.source.toUpperCase();
        marker.properties = feature.properties;
        marker.click= _clickMarkerDetail;
        markers.RegisterMarker(marker);
    } else {
        // setup events
        layer.on({
            mouseover : highlightFeature,
            mouseout : resetHighlight,
            click : _clickMarkerDetail
        });
    }
    if (!showAllPoints) {
        layer.options.opacity = 0;
    }
}


function _clickMarkerDetail(e) {
        var feature = e.target.feature;
        var text = "";
        console.log(e);
        if (!feature) {
            feature = e.target;
        }
        var points = [feature];
        var text = featureToTable(points);
        $("#infodetail").html(text);
}

/**
 * as the window moves around the map, reset the four corners that we track
 */
function resetGrid() {
    NORTH = map.getBounds()._northEast.lat;
    WEST = map.getBounds()._southWest.lng;
    SOUTH = map.getBounds()._southWest.lat;
    EAST = map.getBounds()._northEast.lng;
}

/**
 * Create a circle feature (point) on the map
 * 
 * @param feature
 * @returns {___anonymous6102_6225}
 */
function createCircleFeatureStyle(feature) {
    var options = {
        radius : 4,
        stroke : .1,
        fillOpacity : .8,
        weight : 1
    };

    var color = colorLookup(feature.properties.source);
    options.fillColor = color;
    
    return options;
}

function colorLookup(color) {
    var color_ = color.toUpperCase();
    if (sources[color_] != undefined) {
        return sources[color_].color;
    } else {
        console.log(color_);
    }
    return "#006400";
}

/**
 * Sends a JSON request to the server, gets the results and re-draws the results
 * 
 * @returns
 */
function drawGrid() {

    var bounds = map.getBounds();
    var lat = NORTH;
    var lng = WEST;
    var lat_ = SOUTH;
    var lng_ = EAST;
    var showSources = $(".legend input:checked").map(function() {
        return this.value;
    }).get();

    var neLat = bounds._northEast.lat;
    var swLng = bounds._southWest.lng;
    var term = $("#term").val();
    // construct a GET/JSON request
    var req = getContextPath() + "/json?x1=" + lng + "&y2=" + lat + "&x2=" + lng_ + "&y1=" + lat_ + "&zoom=" + map.getZoom() + "&start=" + start + "&end=" + end +
            "&term=" + term + "&topic=" + $("#topic").val();
    for (var i=0; i < showSources.length; i++) {
        req += "&types="+ showSources[i];
    }
    console.log(req);
    var ret = $.Deferred();
    ajax = $.getJSON(req);
    shouldContinue = false;

    ajax.success(function(data) {
        shouldContinue = true;
    }).then(function(data) {
        console.log(data);
        // initialize the GeoJSON layer
        if (cluster()) {
            if (markers) {
                map.removeLayer(markers);
            }
            markers = new PruneClusterForLeaflet(60);
            markers.BuildLeafletClusterIcon = function(cluster) {
                var e = new L.Icon.MarkerCluster();
                e.stats = cluster.stats;
                e.population = cluster.population;
                return e;
            };
            
            markers.BuildLeafletCluster = function (cluster, position) {
                var m = new L.Marker(position, {
                    icon: markers.BuildLeafletClusterIcon(cluster)
                  });

                  m.on('click', function() {
                      var markersArea = markers.Cluster.FindMarkersInArea(cluster.bounds);
                      console.log(markersArea);
                      var text = featureToTable(markersArea);
                      $("#infodetail").html(text);
                  });
                  
                  return m;
            };
            
            markers.BuildLeafletMarker = function(marker, position) {
                var m = new L.CircleMarker(position);
                m.setOpacity = L.Util.falseFn; // a fake setOpacity method
                this.PrepareLeafletMarker(m, marker);
                return m;
            }

            markers.PrepareLeafletMarker  = function(leafletMarker, data) {
                leafletMarker.setRadius(12);
                if (data && data.properties && data.properties.source) {
                    var style = createCircleFeatureStyle(data);
                    leafletMarker.setStyle(style);
                    leafletMarker.on("click", _clickMarkerDetail); 
                }
                leafletMarker.properties = data.properties;
                // and other properties
            }
        }
        var layer_ = L.geoJson(data.results, {
            onEachFeature : addPointsToMap,
            pointToLayer : function(feature, latlng) {
                // http://stackoverflow.com/questions/15543141/label-for-circle-marker-in-leaflet
                // setup each point
                var src = feature.properties.source.toUpperCase();
                srcs[src] = 1;
                var style = createCircleFeatureStyle(feature);
                var marker = L.circleMarker(latlng, style);
                return marker;
            }
        });
//        map.fitBounds(layer_.getBounds());
 
        // swap the layers out
        if (geoLayer != undefined) {
            map.removeLayer(geoLayer);
        }
        if (cluster()) {
            map.addLayer(markers);
            geoLayer = markers;
        } else {
        geoLayer = layer_;
        geoLayer.addTo(map);
        }
        ajax = undefined;

        // for each layer, pre-calculate the points inside (we will re-use it)
        hlayer.getLayers().forEach(function(pt) {
            var pts = findInside(pt.feature.geometry.coordinates[0]);
            pt.inside = pts.inside;
            pts.inside.forEach(function(p) {
                p.parent = pt;
            });

            // set the color based on the # of items
            var color = chromaScale(pts.inside.length / 25).hex(); // #FF7F7F
            
            pt.setStyle({
                fillColor : color,
                fillOpacity : UNHIGHLIGHTED
            });
        });

        ret.resolve(req);
    });

    return ret;
}

function clickFeature(e) {
    var layer = e.target;
    var l1 = layer._latlngs[0];
    var l2 = layer._latlngs[2];
}

/**
 * Sets the time-silder time
 * 
 * @param year
 */

function setTime(year) {
    time = year;
    $("#time").html("year:" + year);
}

/**
 * reset everything
 */
function reset() {
    setTime(0);
    shouldContinue = true;
    drawGrid();
}

/*
 * debug
 */
function onMapClick(e) {
    if (drawGrid === true) {
        popup.setLatLng(e.latlng).setContent("You clicked the map at " + e.latlng.toString()).openOn(map);
    } else {
        // getDetail(e.latlng, e.latlng);
    }
}

/**
 * loads and sets-up base layers for the map.
 */
function setupBaseLayers() {

    // http://nls-0.tileserver.com/NLS_API/$%7Bz%7D/$%7Bx%7D/$%7By%7D.jpg
//    L.tileLayer('https://{s}.tiles.mapbox.com/v4/<your mapid>/{z}/{x}/{y}.png?access_token=<your access token>').addTo(map);
    var tile = L.tileLayer('https://{s}.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={token}', {
        maxZoom : 17,
        attribution : 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, '
                + '<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' + 'Imagery © <a href="http://mapbox.com">Mapbox</a>',
        id : 'abrin.n80ipon2',
        token: "pk.eyJ1IjoiYWJyaW4iLCJhIjoiMGM2OTlmNjU4MzI4NzAzZWU1Nzc0Y2IwODk2OWNjZDQifQ.yO-4vMyb9c46hq4Z2JXt8w"
    });

    tile.addTo(map);

    var rum = L
            .tileLayer(
                    'http://georeferencer-{s}.tileserver.com//727d3d8823d70aee89431e334ca43d042abb6961/map/cak1TkoVtiCcwmwo8mNCcg/201411241520-xOjZAY/tps/{z}/{x}/{y}.png',
                    {
                        maxZoom : 17,
                        subdomains : '0123'
                    });

    var rum2 = L
            .tileLayer(
                    'http://georeferencer-{s}.tileserver.com//727d3d8823d70aee89431e334ca43d042abb6961/map/FAflLWXi7TvPEgOfODdz4t/201505140124-oKHZqH/polynomial/{z}/{x}/{y}.png',
                    {
                        maxZoom : 17,
                        subdomains : '0123'
                    });

    var map1843 = new L.TileLayer.WMS("http://archviz.humlab.umu.se:8080/geoserver/Maps/wms", {
        layers : "1843_fornfraedafelagid",
        format : 'image/png',
        transparent : true,
        attribution : ""
    });

    var gudbr = new L.TileLayer.WMS("http://archviz.humlab.umu.se:8080/geoserver/Maps/wms", {
        layers : "Gudbrandur_isn93",
        format : 'image/png',
        transparent : true,
        attribution : ""
    });

    var rumGroup = L.layerGroup([ tile, rum ]);
    tile.addTo(map);
    L.control.layers({
        'Basic Map' : tile,
    }, {
        'Iceland -- Physical-Political, 1928' : rumGroup,
        'Islande. Europe 1 bis. 1827' : rum2,
        '1843 Map' : map1843,
        'Gudbrandur Map' : gudbr
    }).addTo(map);

}

/**
 * Trigger highlight event
 * 
 * @param event
 */
function mouseEnterShape(event) {
    $("#map").trigger("highlight:on", [ hlayer, event.target._leaflet_id ]);
}

/**
 * Trigger unhighlight event
 * 
 * @param event
 */
function mouseLeaveShape(event) {
    $("#map").trigger("highlight:off", [ hlayer, event.target._leaflet_id ]);
}

/**
 * highlights the shape on mouse-over
 * 
 * @param _leaflet_id
 */
function highlightShape(_leaflet_id) {
    var layer = hlayer.getLayer(_leaflet_id);
    var ly = layer.feature.geometry.coordinates[0];
    var points = layer.inside;
    layer.setStyle({
        weight : 3
    });
    if (!showAllPoints) {
        for (var i = 0; i < points.length; i++) {
            var l_ = points[i];
            var ll = geoLayer.getLayer(l_._leaflet_id);
            ll.setStyle({
                fillOpacity : 1
            });
        }
    }
}

/**
 * removes shape highlight on mouse-out
 * 
 * @param _leaflet_id
 */
function removeShapeHighlight(_leaflet_id) {
    var layer = hlayer.getLayer(_leaflet_id);
    var ly = layer.feature.geometry.coordinates[0];

    layer.setStyle({
        weight : .5
    });
    var points = layer.inside;
    if (!showAllPoints) {
        for (var i = 0; i < points.length; i++) {
            var l_ = points[i];
            var ll = geoLayer.getLayer(l_._leaflet_id);
            ll.setStyle({
                fillOpacity : UNHIGHLIGHTED
            });
        }
    }
}

/**
 * Loads the Hrepprurs or shapes based from the custom GeoJson objects.
 */
function setupMapShape() {
    // loaded separately (iceland.json)
    hlayer = new L.GeoJSON(hrep, {
        style : myStyle,
        onEachFeature : function(feature, layer_) {
            layer_.on({
                mouseover : mouseEnterShape,
                mouseout : mouseLeaveShape,
                click : function(event) {
                    // this shows the #infodetail window

                    var ly = event.target.feature.geometry.coordinates[0];
                    var text = featureToTable(event.target.inside);
                    $("#infodetail").html(text);

                }
            });
        }
    }).addTo(map);

    // we make our lives a lot easier if we set the ID on the leaflet object that matches the leaflet id, so we can switch quickly between the leaflet object
    // and the html object
    hlayer.getLayers().forEach(function(layer_) {
        if (layer_._container) {
            layer_._container._leaflet_id = layer_._leaflet_id;
        }
    })
}

function featureToTable(points) {
    var keys = {};
    // for each point, aggregate the data by "source"
    for (var i = 0; i < points.length; i++) {
        var l_ = points[i];
//        var ll = geoLayer.getLayer(l_._leaflet_id);
        var l = points[i];
        var props = l.properties;
        if (props == undefined) {
            if (l.feature && l.feature.properties) {
                props = l.feature.properties;
            } else {
                props =[];
            }
            if (l.options != undefined) {
                l.options.opacity = 1;
            }
        }

        for (key in props) {
            if (keys[key] == undefined) {
                keys[key] = [];
            }
            keys[key].push(props[key]);
        }
    }
console.log(keys);
    // for each "source," create a tab-panel
    var text = '<div role="tabpanel">';
    text += '  <ul class="nav nav-tabs" role="tablist">';
    var active = 'active';
    for (key in keys) {
        if (!keys.hasOwnProperty(key) || key == 'source') {
            continue;
        }
        text += ' <li role="presentation" class="' + active + '"><a href="#' + createTabId(key) +
                '" aria-controls="home" role="tab" data-toggle="tab">' + key + '</a></li>';
        active = '';
    }
    text += "  </ul>";

    text += '  <div class="tab-content">';
    active = 'active';

    // create each "tab"
    for (key in keys) {
        if (!keys.hasOwnProperty(key) || key == 'source') {
            continue;
        }
        text += '<div role="tabpanel" class="tab-pane ' + active + '" id="' + createTabId(key) + '">';
        active = '';
        var vals = keys[key];
        text += "<h3>" + key + "</h3>";
        var groupByDate = {};
        var fieldNames = {};
        // for each value, aggregate field/value pairs by date
        consolidateValues(vals, groupByDate, fieldNames);

        // create table header with a list of fields
        text += "<table class='table'>";
        text += writeTableHeader(fieldNames);
        text += writeTableBody(groupByDate);
        text += "</table>";
        text += "</div>";
    }
    text += "</div>";
    return text;
}

function consolidateValues(vals, groupByDate, fieldNames) {
    for (var i = 0; i < vals.length; i++) {
        var dateKey = vals[i]['date'];
        if (!groupByDate[dateKey]) {
            groupByDate[dateKey] = {};
        }
        var fields = groupByDate[dateKey];

        // create a field{fieldName}[valueList] object

        for (k in vals[i]) {
            if (vals[i] && vals[i].hasOwnProperty(k) && k != 'date' && vals[i][k]) {
                if (fields[k] == undefined) {
                    fields[k] = {};
                }
                fieldNames[k] = 1;
                // split on "," to further unify where possible
                var value = vals[i][k][0];
                if (k != "_data" && k != 'description' && isNaN(value)) {
                    var values = value.split(",");
                    for (var j = 0; j < values.length; j++) {
                        fields[k][values[j].trim()] = 1;
                    }
                } else {
                    fields[k][value] = 1;
                }
            }
        }
    }
}

function writeTableHeader(fieldNames) {
    var text = "<thead><tr>";
    text += "<th>Date</th>";
    for (name in fieldNames) {
        // skip the p_ fields which are used for a pie chart
        if (!fieldNames.hasOwnProperty(name) || name.indexOf("_") == 0) {
            continue;
        }

        if (-1 == key.indexOf("NABONE") && name == 'nisp') {
            continue;
        }
        text += "<th>" + name + "</th>";
    }
    text += "</tr></thead>";
 return text;
}

function writeTableBody(groupByDate) {
    var out = "";
    for (dateKey in groupByDate) {
        if (!groupByDate.hasOwnProperty(dateKey)) {
            continue;
        }
        // print the date
        out += "<tr><td><b>" + dateKey + "</b></td>";
        var fields = groupByDate[dateKey];
        // for each field

        // for each field, print the values, skip NABONE special p_ fields unless you're printing NABONE
        for (field in fields) {
            if (!fields.hasOwnProperty(field) || field.indexOf("_") == 0) {
                continue;
            }
//            console.log(key, field, fields[field]);
            if (-1 == key.indexOf("NABONE") && field == 'nisp') {
                continue;
            }

            out += "<td>";
            var values = fields[field];
            var count = 0;
            // for each value, print it, for a link, create the actual link
            for (v in values) {
                if (!values.hasOwnProperty(v)) {
                    continue;
                }
                if (count > 0) {
                    if (field == 'description') {
                        out += "<br><br>";
                    } else {
                        out += ", ";
                    }
                }
                if (field == 'link') {
                    out += '<a href="' + v + '" target="_blank"><span class="glyphicon glyphicon-link"></span></a>';
                } else {
                    out += v;
                }
                count++;
            }
            out += "</td>";
        }
        out += "</tr>";
        out += createCustomGraphs(key, fields['_data']);

    }
    return out;
}


/** cleanup and create the tableId */
function createTabId(key) {
    return key.replace(/(?!\w)[\x00-\xC0]/g, '');
}

/** binds events */
function attachMapEvents() {
    // http://leafletjs.com/reference.html#events

    // zoom
    map.on('zoomend', function() {
        resetGrid();
        drawGrid();
    });

    // resize the screen
    map.on('resize', function() {
        drawGrid();
    });

    // drag object
    map.on('dragend', function() {
        resetGrid();
        drawGrid();
    });

    
    // click on map
    map.on('click', onMapClick);

    // mouseover of leaflet created layer
    $(".leaflet-zoom-animated").on('mouseover', function() {
        $('#map').trigger("highlight:on", [ undefined, -1 ]);
    });

    // custom highlight event
    $(document).on("highlight:on", function(event, layer, id) {
        if (activeId > -1) {
            removeShapeHighlight(activeId);
        }
        activeId = id;
        if (layer == hlayer) {
            highlightShape(id);
        }
    });

    // custom highlight-off event
    $(document).on("highlight:off", function(event, layer, id) {
        // activeId = id;
        if (layer == hlayer) {
            // removeShapeHighlight(id);
        }
    });

    // show-all button
    $("#showAll").click(function() {
        return;
        if (!$("#showAll")[0].checked) {
            showAllPoints = 0;
        } else {
            showAllPoints = 1;
        }
        changeAllOpacity();

    });

    $("body").on("nodeclicked", function(e){
       $("#topic").val(e.id); 
       drawGrid();
    });
    // input box for search, bind keyup
    $("#term").keyup(function() {
        $("#topic").val("");
        if ($("#term").val().length && !showAllPoints) {
            $("#showAll").trigger("click");
        }
        drawGrid();
    });

    // change the time-slider
    $('#timeslider').slider().on('slide', function(ev) {
        start = ev.value[0];
        end = ev.value[1];
        drawGrid();
    });
    
   $('#cluster').click(drawGrid);

}

// init on load
$(function() {
    init();
});