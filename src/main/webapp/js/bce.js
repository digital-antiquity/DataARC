var map;
var popup = L.popup();
var geoLayer = undefined;
var hlayer;
var time = 0;
var NORTH, SOUTH, EAST, WEST;
var grid = false;
var start = -9999;
var end = 9999;

var showAllPoints = 0;

$("#showAll").click(function() {
    if (!$("#showAll")[0].checked) {
        showAllPoints = 0;
    } else {
        showAllPoints = 1;
    }
    changeAllOpacity();

});

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

$("#term").keyup(function() {
    if ($("#term").val().length && !showAllPoints) {
        $("#showAll").trigger("click");
    }
    drawGrid();
});

function init() {
    map = L.map('map').setView([ 66.16495058, -16.68273926 ], 5);
    setupBaseLayers();
    setupMapShape();

    resetGrid();
    drawGrid();
    attachMapEvents();
}

var changeAllOpacity = function() {
    map.eachLayer(function(l) {
        if (l._latlng) {
            geoLayer.getLayer(l._leaflet_id).setOpacity(showAllPoints);
        }
    });
}

var findInside = function(ly) {
    var points = new Array();
    var outside = new Array();
    map.eachLayer(function(l) {
        if (l._latlng != undefined) {
            var val = inside([ l._latlng.lng, l._latlng.lat ], ly);
            if (l._icon != undefined) {
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

$('#timeslider').slider().on('slide', function(ev) {
    start = ev.value[0];
    end = ev.value[1];
    drawGrid();
});

function highlightFeature(e) {
    // var layer = e.target;
    // if (e.target != undefined) {
    // layer.setStyle({
    // weight : 5,
    // strokeColor : '#666',
    // dashArray : '',
    // fillOpacity : 1
    // });
    // }
    // console.log(layer.feature.properties);
    // $("#info").html("temp:" + layer.feature.properties.temp);

    // if (!L.Browser.ie && !L.Browser.opera) {
    // layer.bringToFront();
    // }
}

function resetHighlight(e) {
    geoLayer.resetStyle(e.target);
}

function addPointsToMap(feature, layer) {

    layer.on({
        mouseover : highlightFeature,
        mouseout : resetHighlight,
        click : function(e) {
            var feature = e.target.feature;
            var text = "";
            for (key in feature.properties) {
                if (feature.properties.hasOwnProperty(key)) { // These are explained
                    text += "<b>" + key + ":</b>" + feature.properties[key] + "<br/>";
                }
            }

            $("#infodetail").html(text);
        }
    });
    if (!showAllPoints) {
        layer.options.opacity = 0;
    }
    // feature.setStyle({opacity:0, fillOpacity:0});
}

function resetGrid() {
    NORTH = map.getBounds()._northEast.lat;
    WEST = map.getBounds()._southWest.lng;
    SOUTH = map.getBounds()._southWest.lat;
    EAST = map.getBounds()._northEast.lng;
}

function drawGrid() {

    var bounds = map.getBounds();
    var lat = NORTH;
    var lng = WEST;
    var lat_ = SOUTH;
    var lng_ = EAST;

    var neLat = bounds._northEast.lat;
    var swLng = bounds._southWest.lng;

    var req = "/browse/json.action?x1=" + lng + "&y2=" + lat + "&x2=" + lng_ + "&y1=" + lat_ + "&zoom=" + map.getZoom() + "&start=" + start + "&end=" + end +
            "&term=" + $("#term").val();
    console.log(req);
    var ret = $.Deferred();
    ajax = $.getJSON(req);
    shouldContinue = false;

    ajax.success(function(data) {
        shouldContinue = true;
    }).then(
            function(data) {
                $("#status").html(
                        "timeCode:" + time + " zoom: " + map.getZoom() + " (" + bounds._northEast.lng + ", " + bounds._northEast.lat + ") x (" +
                                bounds._southWest.lng + ", " + bounds._southWest.lat + ")");
                var json = data;
                var layer_ = L.geoJson(json, {
                    onEachFeature : addPointsToMap
                });
                if (geoLayer != undefined) {
                    map.removeLayer(geoLayer);
                }

                // hlayer
                // var pts = findInside(feature.geometry.coordinates[0]);
                // console.log("hi" , pts);

                geoLayer = layer_;
                geoLayer.addTo(map);
                ajax = undefined;

//                hlayer.getLayers().forEach(function(layer) {
//                    var count = 0;
//                    var coords = layer.feature.geometry.coordinates;
//                    if (coords.length == 1) {
//                        geoLayer.getLayers().forEach(function(pt) {
//                          if (inside(pt.feature.geometry.coordinates[0], coords[0])) {
//                              console.log("hi!!!");
//                              count++;
//                          }
//                      });
//                    } else {
//                        for (var i = 0; i < coords.length; i++) {
//                            geoLayer.getLayers().forEach(function(pt) {
//                              if (inside(pt.feature.geometry.coordinates[0], coords[i][0])) {
//                                  console.log("hi");
//                                  count++;
//                              }
//                          });
//
//                        }
//                    }
////                    console.log(layer._leaflet_id , count);
//                });

                hlayer.getLayers().forEach(function(pt) {
                    var pts = findInside(pt.feature.geometry.coordinates[0]);
                    if (pts.inside.length > 0) {
                        pt.setStyle({fillColor:'red',fillOpacity:1});
                    }
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

function getDetail(l1, l2) {
    var req = "/browse/detail.action?x1=" + l1.lng + "&y2=" + l2.lat + "&x2=" + l2.lng + "&y1=" + l1.lat + "&zoom=" + map.getZoom() + "&cols=" + detail;
    console.log(req);
    var ret = $.Deferred();
    ajax = $.getJSON(req);

    ajax.success(function(data) {
    }).then(
            function(data) {
                $("#infostatus").html("<h3>details</h3>");
                var json = data;
                data.unshift("data");
                var chart = c3.generate({
                    data : {
                        columns : [ data ],
                        type : 'bar'
                    },
                    bar : {
                        width : {
                            ratio : 0.5
                        // this makes bar width 50% of length between ticks
                        }
                    },
                    subchart : {
                        show : true
                    }
                });

                $("#infodetail").html(
                        "<p>" + "timeCode:" + time + " zoom: " + map.getZoom() + " (" + l1.lng + ", " + l1.lat + ") x (" + l2.lng + ", " + l2.lat + ")</p>");
                ret.resolve(req);
            });

}

function setTime(year) {
    time = year;
    $("#time").html("year:" + year);
}

function reset() {
    setTime(0);
    shouldContinue = true;
    drawGrid();
}

function onMapClick(e) {
    if (drawGrid === true) {
        popup.setLatLng(e.latlng).setContent("You clicked the map at " + e.latlng.toString()).openOn(map);
    } else {
        // getDetail(e.latlng, e.latlng);
    }
}

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

function setupMapShape() {
    hlayer = new L.GeoJSON(hrep, {
        style : myStyle,
        onEachFeature : function(feature, layer_) {

            layer_.on({
                mouseover : function(event) {
                    var ly = event.target.feature.geometry.coordinates[0];
                    var points = findInside(ly);
                    hlayer.getLayer(event.target._leaflet_id).setStyle({
                        fillOpacity : 0.3,
                        fillColor : '#ddd'
                    });
                    if (!showAllPoints) {
                        for (var i = 0; i < points.inside.length; i++) {
                            var l_ = points.inside[i];
                            var ll = geoLayer.getLayer(l_._leaflet_id);
                            ll.setOpacity(1);
                        }
                    }
                },
                mouseout : function(event) {
                    var ly = event.target.feature.geometry.coordinates[0];
                    hlayer.getLayer(event.target._leaflet_id).setStyle({
                        fillOpacity : .2,
                        fillColor : "#ff7800"
                    });
                    var points = findInside(ly);
                    if (!showAllPoints) {
                        for (var i = 0; i < points.inside.length; i++) {
                            var l_ = points.inside[i];
                            var ll = geoLayer.getLayer(l_._leaflet_id);
                            ll.setOpacity(0);
                        }
                    }
                },
                click : function(event) {
                    var ly = event.target.feature.geometry.coordinates[0];
                    var text = "";
                    var keys = {};

                    var points = findInside(ly);
                    for (var i = 0; i < points.inside.length; i++) {
                        var l_ = points.inside[i];
                        var ll = geoLayer.getLayer(l_._leaflet_id);
                        console.log(l_._leaflet_id);
                        var l = points.inside[i];
                        l.options.opacity = 1;
                        for (key in l.feature.properties) {
                            if (keys[key] == undefined) {
                                keys[key] = {};
                            }
                            var v = l.feature.properties[key].trim();
                            if (key == 'Sagas tags') {
                                console.log(v);
                            }
                            // FIXME: hack
                            if (keys[key].hasOwnProperty(v)) {
                                continue;
                            }
                            keys[key][v] = 1;
                        }
                    }
                    for (key in keys) {
                        if (keys.hasOwnProperty(key)) {
                            var vals = keys[key];
                            var out = "";
                            for (val in vals) {
                                if (vals.hasOwnProperty(val)) {
                                    if (out.length > 0) {
                                        out += ", ";
                                    }
                                    out += val;
                                }
                            }
                            text += "<b>" + key + ":</b> " + out + "<br/>";
                        }
                    }
                    $("#infodetail").html(text);

                }
            });
        }
    }).addTo(map);
}

function attachMapEvents() {
    // events
    // http://leafletjs.com/reference.html#events
    map.on('zoomend', function() {
        resetGrid();
        drawGrid();
    });

    map.on('resize', function() {
        drawGrid();
    });

    map.on('dragend', function() {
        drawGrid();
    });

    map.on('click', onMapClick);

}

$(function() {
    init();
});