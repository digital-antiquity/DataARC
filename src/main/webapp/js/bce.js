var map = L.map('map').setView([ 66.16495058  ,  -16.68273926], 3);
var time = 0;
var NORTH, SOUTH, EAST, WEST;
var grid = false;
var start = -9999;
var end = 9999;
resetGrid();
drawGrid();

var myStyle = {
        "color": "#ff7800",
        "weight": 5,
        "opacity": 1
    };

var RedIcon = L.Icon.Default.extend({
    options: {
            iconUrl: 'marker-icon-red.png' 
    }
 });
 
var hlayer = new L.GeoJSON(hrep, {style:myStyle,
    onEachFeature : function(feature, layer_) {
        layer_.on({
            click : function(event) {
                var ly = event.target.feature.geometry.coordinates[0];
                map.eachLayer(function(l) {
                    if (l._latlng != undefined) {
                        var val = inside([l._latlng.lng, l._latlng.lat] , ly);
                        if (val) {
                            l.setIcon(new RedIcon());
                        }
                    }
                });
            }
        });
    }
}).addTo(map);


var inside = function (point, vs) {
    // ray-casting algorithm based on
    // http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
    
    var x = point[0], y = point[1];
    
    var inside = false;
    for (var i = 0, j = vs.length - 1; i < vs.length; j = i++) {
        var xi = vs[i][0], yi = vs[i][1];
        var xj = vs[j][0], yj = vs[j][1];
        
        var intersect = ((yi > y) != (yj > y))
            && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
        if (intersect) inside = !inside;
    }
    
    return inside;
};

$('#timeslider').slider()
.on('slide', function(ev){
    start = ev.value[0];
    end = ev.value[1];
    drawGrid();
});

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

var tile = L.tileLayer('https://{s}.tiles.mapbox.com/v3/{id}/{z}/{x}/{y}.png', {
    maxZoom : 17,
    attribution : 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, '
            + '<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' + 'Imagery © <a href="http://mapbox.com">Mapbox</a>',
    id : 'examples.map-i875mjb7'
});

tile.addTo(map);

var layer = undefined;


function highlightFeature(e) {
    var layer = e.target;
//
//    layer.setStyle({
//        weight : 5,
//        strokeColor : '#666',
//        dashArray : '',
//        fillOpacity : 1
//    });

//    console.log(layer.feature.properties);
//    $("#info").html("temp:" + layer.feature.properties.temp);

//    if (!L.Browser.ie && !L.Browser.opera) {
//        layer.bringToFront();
//    }
}

function resetHighlight(e) {
    layer.resetStyle(e.target);
}

function onEachFeature(feature, layer) {
    layer.on({
        mouseover : highlightFeature,
        mouseout : resetHighlight,
//        click : clickFeature
    });
//    popupOptions = {maxWidth: 200};
    
    var text = "";
    for (key in feature.properties) {
        if (feature.properties.hasOwnProperty(key) ) {        // These are explained
//            if (feature.properties[key].length > 0) {
                text += "<b>" + key+ ":</b>" + feature.properties[key] + "<br/>";
//            }
        }
    }

    layer.bindPopup(text);
//    console.log(feature);
//    feature.bindPopup('A pretty CSS3 popup. <br> Easily customizable.');
}

function resetGrid() {
    NORTH = map.getBounds()._northEast.lat;
    WEST = map.getBounds()._southWest.lng;
    SOUTH = map.getBounds()._southWest.lat;
    EAST = map.getBounds()._northEast.lng;
    // L.marker([NORTH, WEST]).addTo(map);
    // L.marker([SOUTH, EAST]).addTo(map);
}

function drawGrid() {

    var bounds = map.getBounds();
    var lat = NORTH;
    var lng = WEST;
    var lat_ = SOUTH;
    var lng_ = EAST;
//    var height = Math.abs(Math.abs(lat) - Math.abs(lat_)) / detail;
//    var width = Math.abs(Math.abs(lng) - Math.abs(lng_)) / detail;

    var neLat = bounds._northEast.lat;
    var swLng = bounds._southWest.lng;

//    if (ajax != undefined) {
//        ajax.abort();
//    }

    var req = "/browse/json.action?x1=" + lng + "&y2=" + lat + "&x2=" + lng_ + "&y1=" + lat_ + "&zoom=" + map.getZoom() + "&start=" + start + "&end="+ end + "&term="+ $("#term").val();
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
                    onEachFeature : onEachFeature
                });
                if (layer != undefined) {
                    map.removeLayer(layer);
                }
                layer = layer_;
                layer.addTo(map);
                ajax = undefined;
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
    var req = "/browse/detail.action?x1=" + l1.lng + "&y2=" + l2.lat + "&x2=" + l2.lng + "&y1=" + l1.lat + "&zoom=" +
            map.getZoom() + "&cols=" + detail;
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

var popup = L.popup();

function onMapClick(e) {
    if (drawGrid === true) {
        popup.setLatLng(e.latlng).setContent("You clicked the map at " + e.latlng.toString()).openOn(map);
    } else {
//        getDetail(e.latlng, e.latlng);
    }
}

map.on('click', onMapClick);
