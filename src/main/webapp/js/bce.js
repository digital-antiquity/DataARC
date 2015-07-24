/**
 * This script focuses on the inititalization and setup of the Leaflet Maps API and the general interactions with the maps.
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

        var div2 = L.DomUtil.create('div', 'info legend'), colors = [ 'Sead', 'PMS', 'Sagas','Isleif', 'NABONE','tDAR','Site Database' ];

        // loop through our density intervals and generate a label with a colored square for each interval
        for (var i = 0; i < colors.length; i++) {
            div2.innerHTML += '<div ><i style="opacity: ' + UNHIGHLIGHTED + '; background:' +colorLookup(colors[i])+'"></i> ' + colors[i] + "</div> ";
        }

        return div2;
    };

    legend2.addTo(map);
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
        geoLayer.getLayer(e.target._leaflet_id).setStyle({
            fillOpacity : 1
        });
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
    geoLayer.resetStyle(e.target);
}

/**
 * for each feature, add it to the map, also setup the click event which shows detail in the #infodetail div
 * 
 * @param feature
 * @param layer
 */
function addPointsToMap(feature, layer) {

    // setup events
    layer.on({
        mouseover : highlightFeature,
        mouseout : resetHighlight,
        click : function(e) {
            var feature = e.target.feature;
            var text = "";
            var points = [feature];
            var text = featureToTable(points);
            $("#infodetail").html(text);
        }
    });
    if (!showAllPoints) {
        layer.options.opacity = 0;
    }
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
    if (color_ == 'ISLEIF') {
        return  "red";
    }
    if (color_ == 'SAGAS') {
        return  "yellow";
    }
    if (color_ == 'PMS') {
         return  "darkblue";
    }
    if (color_ == 'NABONE') {
         return  "BlueViolet";
    }
    if (color_ == 'SEAD') {
        return  "darkgreen";
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

    var neLat = bounds._northEast.lat;
    var swLng = bounds._southWest.lng;

    // construct a GET/JSON request
    var req = "/browse/json?x1=" + lng + "&y2=" + lat + "&x2=" + lng_ + "&y1=" + lat_ + "&zoom=" + map.getZoom() + "&start=" + start + "&end=" + end +
            "&term=" + $("#term").val();
    console.log(req);
    var ret = $.Deferred();
    ajax = $.getJSON(req);
    shouldContinue = false;

    ajax.success(function(data) {
        shouldContinue = true;
    }).then(function(data) {
        // initialize the GeoJSON layer
        var layer_ = L.geoJson(data, {
            onEachFeature : addPointsToMap,
            pointToLayer : function(feature, latlng) {
                // http://stackoverflow.com/questions/15543141/label-for-circle-marker-in-leaflet
                // setup each point
                var style = createCircleFeatureStyle(feature);
                return L.circleMarker(latlng, style);
            }
        });

        // swap the layers out
        if (geoLayer != undefined) {
            map.removeLayer(geoLayer);
        }

        geoLayer = layer_;
        geoLayer.addTo(map);
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
    var tile = L.tileLayer('https://{s}.tiles.mapbox.com/v3/{id}/{z}/{x}/{y}.png', {
        maxZoom : 17,
        attribution : 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, '
                + '<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' + 'Imagery © <a href="http://mapbox.com">Mapbox</a>',
        id : 'examples.map-i875mjb7'
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
        fillOpacity : 0.6
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
        fillOpacity : .2
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
        var ll = geoLayer.getLayer(l_._leaflet_id);
        var l = points[i];
        var props = l.properties;
        if (props == undefined) {
            props = l.feature.properties;
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
                if (k != "_data") {
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
                    out += ", ";
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

function createCustomGraphs(key, input) {
    var txt = "";
    if (key.indexOf("SEAD") == 0 || key.indexOf("NABONE") == 0) {
        txt ="<tr><td></td><td colspan=10>";
    }

    if (key.indexOf("SEAD") == 0) {
        txt += "<div id='barChart' style='height:400px'></div>";
        var data = [];
        var sampleLabels = [];
        var grouping = [];
        var categories = ["Aquatics", "Carrion", "Disturbed/arable", "Dung/foul habitats", "Ectoparasite", "General synanthropic", "Halotolerant", "Heathland & moorland", "Indicators: Coniferous", "Indicators: Deciduous", "Indicators: Dung", "Indicators: Standing water", "Meadowland", "Mould beetles", "Open wet habitats", "Pasture/Dung", "Sandy/dry disturbed/arable", "Stored grain pest", "Wetlands/marshes", "Wood and trees"];
        var ignore = [];
        for (v in input) {
            if (!input.hasOwnProperty(v)) {
                continue;
            }
            var jd = JSON.parse(v);
            for (site in jd) {
                if (!jd.hasOwnProperty(site)) {
                    continue;
                }
                var samples = jd[site];
                
                for (var s=0;s<samples.length;s++) {
                    sampleLabels.push(samples[s][0]);
                }
                for (var c =0 ; c < categories.length; c++) {
                    data[c] = [];
                    var cat = categories[c];
                    // we need to add one to make space for the label at the beginning
                    var catSampleOffset = c+1;
                    var total = 0.0;
                    // test each category to see what actually needs to be displayed
                    for (var s =0; s < samples.length; s++) {
                        total += parseFloat(samples[s][catSampleOffset]);
                    }
                    console.log(cat +" total:" + total);
                    // skip category entries that have no values
                    if (!(total > 0.0)) {
                        ignore.push(cat);
                        continue;
                    }
                    // buiild the actual output array per category
                    for (var s =0; s < samples.length; s++) {
                        if (s == 0) {
                            data[c][0] = cat;
                        } 
                        data[c][s+1] = samples[s][catSampleOffset];
                    }
                    
                }
                
            }
        }
        categories = $(categories).not(ignore).get();
        console.log("filtered:", categories);
        console.log(sampleLabels);
        console.log(data);
        var chartData = {
            bindto : "#barChart",
            data : {
                columns : data,
                type : 'bar',
                groups: [categories]
            },
            axis: {
                rotated: true,
                x: {
                    type: 'category',
                    categories: sampleLabels
                }
            }
        };
        // we need a slight delay here to register the #radioChart div in the DOM
        if (data.length > 0) {
            txt += "<script>c3.generate(" + JSON.stringify(chartData) + ");</script>";
        }
    }

    // for NABONE, create the pie-chart based on the values
    if (key.indexOf("NABONE") == 0) {
        txt += "<div id='radioChart'></div>";
        var data = [];
        for (v in input) {
            if (!input.hasOwnProperty(v)) {
                continue;
            }
            var jd = JSON.parse(v);
            var percLabels = ["Dom %","Whale %","Seal %","Walrus %","Deer %","Other mam %","Bird %","Fish %","Mol Arth Gast %"];
            for (var i=0; i < percLabels.length; i++) {
                var val = parseFloat(jd['perc'][i]);
                if (val > 0) {
                    data.push([percLabels[i],val]);
                }
            }
        }

        var chartData = {
            bindto : "#radioChart",
            data : {
                columns : data,
                type : 'pie'
            }
        };
        // we need a slight delay here to register the #radioChart div in the DOM
        if (data.length > 0) {
            txt += "<script>c3.generate(" + JSON.stringify(chartData) + ");</script>";
        }
    }
    if (key.indexOf("SEAD") == 0 || key.indexOf("NABONE") == 0) {
        txt +="</td></tr>";
    }
    return txt;
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

    // input box for search, bind keyup
    $("#term").keyup(function() {
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

}

// init on load
$(function() {
    init();
});