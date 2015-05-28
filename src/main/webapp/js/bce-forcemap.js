function getWidth() {
    width = $('#infobox').width();
    console.log(width);
    return width;
}

var width = getWidth();
var aspect = 500 / 950;

$(window).resize(resize);

function resize() {
    width = getWidth();
    var height = width * aspect;
    vis.attr("width", width);
    vis.attr("height", height);
    parent.attr("width", width);
    parent.attr("height", height);
    force.size([ width, height ]).resume();
};

var color = d3.scale.category20();
var zoom = d3.behavior.zoom().on("zoom", redraw);
var parent = d3.select("#forcemap").append("svg:svg").attr("preserveAspectRatio", "xMidYMid").attr("width", width).attr("height", width * aspect);
var vis = parent.attr(
        "pointer-events", "all").append('svg:g').append('svg:g');
vis.attr("x", 0);
vis.attr("y", 0);

vis.append('svg:rect').attr('fill', 'white');

function redraw() {
    // console.log("here", d3.event.translate, d3.event.scale);
    vis.attr("transform", "translate(" + d3.event.translate + ")" + " scale(" + d3.event.scale + ")");
}

var force = d3.layout.force().linkDistance(30).linkStrength(2).charge(-200).size([ width, width * aspect ]);

function getLeafNodes(leafNodes, obj, links) {
    if (obj.children) {
        obj.children.forEach(function(child) {
            getLeafNodes(leafNodes, child, links)
        });
    }
    if (obj.id) {
        obj["weight"] = 1;
        obj['name'] = obj.text.caption;
        leafNodes.push(obj);
        var link = {
            source : obj.id,
            target : obj.parentId,
            weight : 1
        };
        links.push(link);
    }
}

var ordIdXref = {};
var nds = [], lns = new Array();

d3.json("js/NABOGHEA_sheep_concept_map.json", function(error, graph) {
    getLeafNodes(nds, graph.mindmap.root, lns);
    var nodes = nds.slice(0), links = [], bilinks = [];

    nds.forEach(function(node, idx) {
        ordIdXref[node.id] = idx;
    });

    lns.forEach(function(link) {
        var s = nodes[ordIdXref[link.source]], t = nodes[ordIdXref[link.target]], i = {
            weight : 0,
            name : 'connector'
        }; // intermediate node
        if (s != undefined && t != undefined) {
            nodes.push(i);
            links.push({
                source : s,
                target : i
            }, {
                source : i,
                target : t
            });
            bilinks.push([ s, i, t ]);
        }
    });

    force.nodes(nodes).links(links).start();

    var link = vis.selectAll(".link").data(bilinks).enter().append("path").attr("class", "link");

    var node = vis.selectAll(".node").data(nds).enter().append("circle").attr("class", "node").attr("r", 5).style("fill", function(d) {
        return color(d.weight);
    }).call(force.drag).on('click', nodeLabelClick);

    function nodeLabelClick(d) {
        var $term = $("#term");
        $term.val(d.name);
        $term.trigger("keyup");
        
//        console.log(d.name);
    }

    // http://jsfiddle.net/vfu78/16/
    node.append("title").text(function(d) {
        return d.name;
    });

    var nodelabels = vis.selectAll(".nodelabel").data(nds).enter().append("text").attr({
        "x" : function(d) {
            return d.x;
        },
        "y" : function(d) {
            return d.y;
        },
        "class" : "nodelabel"
    }).text(function(d) {
        return d.name;
    }).on('click', nodeLabelClick);
    ;

    force.on("tick", function() {
        link.attr("d", function(d) {
            return "M" + d[0].x + "," + d[0].y + "S" + d[1].x + "," + d[1].y + " " + d[2].x + "," + d[2].y;
        });
        node.attr("transform", function(d) {
            return "translate(" + d.x + "," + d.y + ")";
        });
        nodelabels.attr("x", function(d) {
            return d.x;
        }).attr("y", function(d) {
            return d.y;
        });

    });

    node.forEach(function(d) {
        if (d._children || d.children) {
            d.x = width / 2, d.y = (width * aspect) / 2;
            d.fixed = true;
        }
    });
});

function interpolateZoom(translate, scale) {
    var self = this;
    return d3.transition().duration(350).tween("zoom", function() {
        var iTranslate = d3.interpolate(zoom.translate(), translate), iScale = d3.interpolate(zoom.scale(), scale);
        return function(t) {
            zoom.scale(iScale(t)).translate(iTranslate(t));
            zoomed();
        };
    });
}

function zoomed() {
    vis.attr("transform", "translate(" + zoom.translate() + ")" + "scale(" + zoom.scale() + ")");
    vis.attr("x", zoom.translate()[0]);
    vis.attr("y", zoom.translate()[1]);
}

function getCenter() {
    return [ width / 2, (width * aspect) / 2 ];
}

function zoomClick() {
    var clicked = d3.event.target, direction = 1, factor = 0.2, target_zoom = 1, center = getCenter(), extent = zoom.scaleExtent(), translate = zoom
            .translate(), translate0 = [], l = [], view = {
        x : translate[0],
        y : translate[1],
        k : zoom.scale()
    };

    d3.event.preventDefault();
    direction = (this.id === 'zoom_in') ? 1 : -1;
    target_zoom = zoom.scale() * (1 + factor * direction);

    if (target_zoom < extent[0] || target_zoom > extent[1]) {
        return false;
    }

    translate0 = [ (center[0] - view.x) / view.k, (center[1] - view.y) / view.k ];
    view.k = target_zoom;
    l = [ translate0[0] * view.k + view.x, translate0[1] * view.k + view.y ];

    view.x += center[0] - l[0];
    view.y += center[1] - l[1];

    interpolateZoom([ view.x, view.y ], view.k);
}

function moveUp() {
    var x = parseInt(vis.attr('x'));
    var y = parseInt(vis.attr('y'));
    updatePos(x, y - 20);
}

function moveDown() {
    var x = parseInt(vis.attr('x'));
    var y = parseInt(vis.attr('y'));
    updatePos(x, y + 20);
}

function moveLeft() {
    var x = parseInt(vis.attr('x'));
    var y = parseInt(vis.attr('y'));
    updatePos(x - 20, y);
}

function moveRight() {
    var x = parseInt(vis.attr('x'));
    var y = parseInt(vis.attr('y'));
    updatePos(x + 20, y);
}

function updatePos(x, y) {
    vis.attr('x', x);
    vis.attr('y', y);
    vis.attr("transform", "translate(" + x + "," + y + ") " + "scale(" + zoom.scale() + ")");
}

d3.select('#up').on("click", moveUp);
d3.select('#down').on("click", moveDown);
d3.select('#left').on("click", moveLeft);
d3.select('#right').on("click", moveRight);
d3.select('#zoom_in').on('click', zoomClick);
d3.select('#zoom_out').on('click', zoomClick);


$("#expand").click(function() {
    var $info =$("#infobox");
    var $map =$("#mapbox");
    $info.removeClass("col-md-4");
    $info.addClass("col-md-8");
    $map.removeClass("col-md-8");
    $map.addClass("col-md-4");
    resize();
});

$("#contract").click(function() {
    var $info =$("#infobox");
    var $map =$("#mapbox");
    $map.removeClass("col-md-4");
    $map.addClass("col-md-8");
    $info.removeClass("col-md-8");
    $info.addClass("col-md-4");
    resize();
});