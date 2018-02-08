var topicSettings = {
  container: 'topicmap',
  dataUrl: '/api/topicmap/view',
  gravity: -200,
  minRadius: 5,
  maxRadius: 15,
  searchContainer: 'topicSearch'
}

if (testing) {
  topicSettings.dataUrl = 'dev/api_topicmap.php';
}

var TopicMap = function(settings) {
  this.settings = {}

  this.settings.filterCallback = false;

  this.loaded = false;

  $.extend(this.settings, settings);

  this.graph = {};

  this.container = $('#' + this.settings.container);

  this.messageContainer = $('<div>', { 'class': 'topic-message-container' });
  this.messageBox = $('<div>', { 'class': 'topic-messages' });
  this.messageClose = $('<div>', { 'class': 'topic-message-close' });
  this.messageClose.append('<span class="fa fa-arrow-up" style="line-height:34px;vertical-align: middle;"></span>');
  this.messageClose.click(function() {
    $(this).parent().slideUp();
  });

  this.container.append(this.messageContainer);
  this.messageContainer.append(this.messageBox);
  this.messageContainer.append(this.messageClose);

  var searchContainer = $('#' + this.settings.searchContainer);
  this.searchBox = $('<input>', { 'class': 'typeahead', 'type': 'text', 'placeholder': ' Concept Search' });
  searchContainer.append(this.searchBox);

  this.topics = typeof this.topics == 'undefined' ? [] : this.topics;
  // this.topics = ['45','52','53','64','136'];

  this.maxLinks = 0;

  this.concentricActive = false;

  this.paused = false;



  if (typeof settings.maxRadStep == 'undefined')
    this.settings.maxRadStep = 100;

  this.fetchData();
}

TopicMap.prototype = {

  init: function() {

    var _this = this;

    $('#topicmapReset').click(function() {
      _this.reset();
    });
    $('#topicmapPause').click(function() {
      _this.pause();
    });
    $('#topicmapProceed').click(function() {
      _this.proceed();
    });
    $('#topicmapZoomIn').click(function() {
      _this.zoomIn();
    });
    $('#topicmapZoomOut').click(function() {
      _this.zoomOut();
    });
    $(window).on('resize', _.debounce(function() {
      _this.resize();
    }, 100));

    this.initSearch();

    this.initGraphSettings();

    this.processLinks();

    this.drawForceGraph();

    this.highlightTopics();

  },

  fetchData: function() {
    var _this = this;
    $.ajax({
      //url: getContextPath() + '/api/topicmap/view',
      url: _this.settings.dataUrl,
      type: 'GET',
      dataType: 'json',
      context: this,
      success: function(resp) {
        _this.rawData = resp;
        callback: this.formatData(resp);
      }
    });

  },

  formatData: function(data) {
    var _this = this;
    this.graph.nodes = [];
    this.graph.links = [];
    _.each(data.topics, function(value) {
      _this.graph.nodes.push({
        "id": value.id.toString(),
        "identifier": value.identifier,
        "shared_name": value.name,
        "name": value.name,
        "SUID": value.id,
        "category": value.category,
        "selected": false
      });
    });
    _.each(data.associations, function(value) {
      _this.graph.links.push({
        "id": value.id.toString(),
        "shared_name": value.name,
        "name": value.name,
        "shared_interaction": value.name,
        "interaction": value.name,
        "identifier": value.identifier,
        "SUID": value.id,
        "selected": false,
        "source": value.from,
        "target": value.to
      });
    });

    this.init();

  },

  initGraphSettings: function() {

    var _this = this;

    this.w = this.container.width();
    this.h = this.container.height();
    if (typeof this.svg == 'undefined') {
      this.svg = d3.select('div#' + this.settings.container)
        .append('svg')
        .attr('id', 'topic-svg')
        .attr('width', _this.w)
        .attr('height', _this.h);
    } else {
      this.svg.empty();
    }

    // .attr("preserveAspectRatio", "xMinYMin meet")
    // .attr('viewbox', '0 0 '+_this.w+' 798');
    // this.svg = d3.select('#topic-svg');
    // this.svg.empty();
    // this.svg.attr('viewbox', '0 0 '+_this.w+' '+_this.h);
    // this.svg.attr('width', _this.w);
    // this.svg.attr('height', _this.h);

    this.gContainer = this.svg.append('g')
      .attr('class', 'container');

    this.setInitZoom();

    this.circles = this.gContainer.append('g').attr('class', 'boundary');

    this.color = d3.scaleOrdinal(d3.schemeCategory20);

    this.simulation = d3
      .forceSimulation()
      .force("link", d3
        .forceLink()
        .id(
          function(d) {
            return d.id;
          })
      )
      .force("charge", d3
        .forceManyBody()
        .strength(_this.settings.gravity)
        .distanceMax(600)
        .distanceMin(100))
      .force('collide', d3
        .forceCollide(15)
        .strength(2)
        .iterations(50))
      .force("center", d3
        .forceCenter(_this.w / 2, _this.h / 2))
      // .alphaTarget(0.5)
      .alphaDecay(0.1) // controls how long the animations run, default is 0.0228, closer to 1 means animation decays faster
    ;

  },

  setInitZoom: function() {
    var _this = this;

    // ZOOM PARAMETERS
    this.settings.minZoom = 0.1;
    this.settings.maxZoom = 7;

    this.zoom = d3.zoom()
      .scaleExtent([this.settings.minZoom, this.settings.maxZoom])
      .on("zoom", goZoom);
    this.svg.call(_this.zoom);
    this.transform = d3.zoomIdentity
      .translate(_this.w / 6, _this.h / 6)
      .scale(0.5);

    this.svg.call(_this.zoom.transform, _this.transform);


    function goZoom() {
      _this.gContainer.attr("transform", d3.event.transform);
    }
  },

  processLinks: function() {
    var _this = this;

    this.linkedByIndex = {};

    this.graph.nodes.forEach(function(node) {
      _this.linkedByIndex[node.id + "," + node.id] = 1;
    });

    this.graph.links.forEach(function(link) {
      // find the neighbors
      _this.linkedByIndex[link.source + "," + link.target] = 1;

      // find other links with same target+source or source+target
      var same = _.filter(_this.graph.links, {
        'source': link.source,
        'target': link.target
      });
      var sameAlt = _.filter(_this.graph.links, {
        'source': link.target,
        'target': link.source
      });
      var sameAll = same.concat(sameAlt);

      _.each(sameAll, function(s, i) {
        s.sameIndex = (i + 1);
        s.sameTotal = sameAll.length;
        s.sameTotalHalf = (s.sameTotal / 2);
        s.sameUneven = ((s.sameTotal % 2) !== 0);
        s.sameMiddleLink = ((s.sameUneven === true) && (Math.ceil(s.sameTotalHalf) === s.sameIndex));
        s.sameLowerHalf = (s.sameIndex <= s.sameTotalHalf);
        s.sameArcDirection = s.sameLowerHalf ? 0 : 1;
        s.sameIndexCorrected = s.sameLowerHalf ? s.sameIndex : (s.sameIndex - Math.ceil(s.sameTotalHalf));
      });

      // find the source and target by link ids
      var source = _this.graph.nodes.filter(node => node.id == link.source)[0];
      var target = _this.graph.nodes.filter(node => node.id == link.target)[0];

      // check linkcount
      if (typeof source["linkCount"] == 'undefined') source["linkCount"] = 0;
      if (typeof target["linkCount"] == 'undefined') target["linkCount"] = 0;

      // count it up
      source["linkCount"]++;
      target["linkCount"]++;

      if (source["linkCount"] > _this.maxLinks || target["linkCount"] > _this.maxLinks) {
        _this.maxLinks = Math.max(source["linkCount"], target["linkCount"]);
      }

    });

    var maxSame = _.chain(_this.graph.links)
      .sortBy(function(x) {
        return x.sameTotal;
      })
      .last()
      .value().sameTotal;

    _.each(_this.graph.links, function(link) {
      link.maxSameHalf = Math.floor(maxSame / 3);
    });

  },

  drawForceGraph: function() {

    var _this = this;

    this.scale = d3
      .scaleLinear()
      .domain([0, _this.maxLinks])
      .range([_this.settings.minRadius, _this.settings.maxRadius]);

    // this dude has to be a PATH, not a line in order to curve
    // svg rules apparently
    this.link = _this.gContainer.append("g")
      .attr("class", "links")
      .selectAll("path")
      .data(_this.graph.links)
      .enter().append("path")
      .attr("stroke-width", function(d) { return 2; })
      //.attr("stroke", function(d){return _this.color(d.sameIndex - 1);})
      .attr("stroke", function(d) { return "#ddf"; })
      .attr("fill", "none")
      // .attr('marker-mid', 'url(#arrowhead)')
      .on('click', function() {
        _this.linkClicked(this);
      });

    this.markerPath = _this.gContainer.append("g")
      .attr("class", "markers")
      .selectAll("path.marker")
      .data(_this.graph.links)
      .enter().append("path")
      .attr("class", "marker_only")
      .attr("fill", "none")
      .attr("marker-end", function(d) { return "url(#arrowhead)"; });

    this.node = _this.gContainer.append("g")
      .attr("class", "nodes")
      .selectAll("circle")
      .data(_this.graph.nodes)
      .enter().append("circle")
      .attr("r", function(d) {
        return _this.scale((typeof d.linkCount == 'undefined' ? _this.settings.minRadius : d.linkCount));
      })
      .attr("fill", function(d) { return nodeColor(d); })
      .on('mouseover', function(d) { _this.hoverNode(d); })
      .on('mouseout', function(d) { _this.highlightTopics(); })
      .call(d3.drag()
        .on("start", dragstarted)
        .on("drag", dragged)
        .on("end", dragended))
      .on('click', function() {
        _this.nodeClicked(this);
      });

    this.label = _this.gContainer.append("g")
      .attr("class", "labels")
      .selectAll("text")
      .data(_this.graph.nodes)
      .enter()
      .append("text")
      .text(function(d) { return d.name; })
      .style("text-anchor", "middle")
      .style("alignment-baseline", "middle")
      .style("fill", function(d) { return labelColor(d); })
      .style("font-family", "Arial")
      .style("font-size", 12)
      .on('mouseover', function(d) { _this.hoverNode(d); })
      .on('mouseout', function(d) { _this.highlightTopics(); })
      .call(d3.drag()
        .on("start", dragstarted)
        .on("drag", dragged)
        .on("end", dragended))
      .on('click', function() {
        _this.nodeClicked(this);
      });

    this.marker = _this.gContainer.append('marker')
      .attr('id', 'arrowhead')
      .attr('refX', 2) // Controls the shift of the arrow head along the path
      .attr('refY', 2)
      .attr('markerWidth', 6)
      .attr('markerHeight', 4)
      .attr('orient', 'auto')
      .style('fill', '#caa')
      .append('path')
      .attr('d', 'M 0,0 V 4 L6,2 Z');

    this.node.append("title")
      .text(function(d) { return d.id; });

    this.simulation
      .nodes(_this.graph.nodes)
      .on("tick", ticked);
    this.simulation.force("link")
      .links(_this.graph.links);

    this.loaded = true;

    function ticked() {

      function curveLine(d) {
        var adjust = 0;
        if (d.sameTotal > 1) {
          if (d.sameIndexCorrected == 1) {
            adjust = d.sameIndexCorrected * 5;
          } else {
            adjust = d.sameIndexCorrected * 7;
          }
        }

        var x1 = d.source.x;
        var x2 = d.target.x;
        var y1 = d.source.y;
        var y2 = d.target.y;
        var xm = (x1 + x2) / 2;
        var ym = (y1 + y2) / 2;
        var dx = x1 - x2;
        var dy = y1 - y2;
        var dist = Math.sqrt(dx * dx + dy * dy);
        dx /= dist;
        dy /= dist;

        var x3 = xm + (adjust * dy);
        var y3 = ym - (adjust * dx);

        var result = lineFunction([
          [x1, y1],
          [x3, y3],
          [x2, y2]
        ]);
        d.xm = x3;
        d.ym = y3;
        var split = result.split("C");
        d.markerPath = split[0] + "C" + split[1];

        return result;
      }

      lineFunction = d3.line()
        // .x(function(d) { return d.x; })
        // .y(function(d) { return d.y; })
        .curve(d3.curveCardinal);

      if (_this.concentricActive) {

        var t = d3.transition("moveslower")
          .duration(200)
          .ease(d3.easeLinear);

        _this.node.each(constrain);

        _this.link.transition(t)
          .attr("display", function(d) { return displayFunction(d); })
          .attr("x1", function(d) { return d.source.x; })
          .attr("y1", function(d) { return d.source.y; })
          .attr("x2", function(d) { return d.target.x; })
          .attr("y2", function(d) { return d.target.y; })
          .attr("d", function(d) {
            return curveLine(d);
          });

        _this.markerPath.transition(t)
          .attr("display", function(d) { return displayFunction(d); })
          .attr("x1", function(d) { return d.source.x; })
          .attr("y1", function(d) { return d.source.y; })
          .attr("x2", function(d) { return d.xm; })
          .attr("y2", function(d) { return d.ym; })
          .attr("d", function(d) { return d.markerPath; });


        _this.node.transition(t)
          .attr("display", function(d) { return displayFunction(d); })
          .attr("cx", function(d) { return d.x; })
          .attr("cy", function(d) { return d.y; });

        _this.label.transition(t)
          .attr("display", function(d) { return displayFunction(d); })
          .attr("x", function(d) { return d.x; })
          .attr("y", function(d) { return d.y; });
      } else {

        _this.link
          .attr("display", function(d) { return displayFunction(d); })
          .attr("x1", function(d) { return d.source.x; })
          .attr("y1", function(d) { return d.source.y; })
          .attr("x2", function(d) { return d.target.x; })
          .attr("y2", function(d) { return d.target.y; })
          .attr("d", function(d) {
            return curveLine(d);
          });

        _this.markerPath
          .attr("display", function(d) { return displayFunction(d); })
          .attr("x1", function(d) { return d.source.x; })
          .attr("y1", function(d) { return d.source.y; })
          .attr("x2", function(d) { return d.xm; })
          .attr("y2", function(d) { return d.ym; })
          .attr("d", function(d) { return d.markerPath; });


        _this.node
          .attr("display", function(d) { return displayFunction(d); })
          .attr("cx", function(d) { return d.x; })
          .attr("cy", function(d) { return d.y; });

        _this.label
          .attr("display", function(d) { return displayFunction(d); })
          .attr("x", function(d) { return d.x; })
          .attr("y", function(d) { return d.y; });
      }

      function displayFunction(d) {
        if (!_this.concentricActive) {
          d.active = true;
          return "inline";
        } else {
          if (typeof d.target !== 'undefined') {
            if (d.source.level == null || d.target.level == null) {
              d.active = false;
              return "none"
            }
          } else {
            if (d.level == null) {
              d.active = false;
              return "none"
            }
          }
          d.active = true;
          return "inline";
        }
      }
    }

    function constrain(d) {
      if (d.level != null) {
        var cX = _this.w / 2;
        var cY = _this.h / 2;
        var levelIndex = _this.levels.indexOf(d.level.toString());
        var max = _this.settings.maxRadStep * levelIndex;
        var min = max;
        var vX = d.x - cX;
        var vY = d.y - cY;
        var magV = Math.sqrt(vX * vX + vY * vY);
        if (magV > max) {
          d.vx = 0;
          d.vy = 0;
          d.x = cX + vX / magV * max;
          d.y = cY + vY / magV * max;
        } else if (magV < min) {
          d.vx = 0;
          d.vy = 0;
          d.x = cX + vX / magV * min;
          d.y = cY + vY / magV * min;
        }
      }
    }

    function dragstarted(d) {
      if (!d3.event.active) _this.simulation.alphaTarget(0.1).restart();
      d.fx = d.x;
      d.fy = d.y;
    }

    function dragged(d) {
      d.fx = d3.event.x;
      d.fy = d3.event.y;
    }

    function dragended(d) {
      if (!d3.event.active) _this.simulation.alphaTarget(0);
      if (!_this.paused) {
        d.fx = null;
        d.fy = null;
      }
    }

    function nodeColor(d) {
      var color = '#ddd';
      switch (d.category) {
        case 'physical':
          color = "#B1ACD0";
          break;
        case 'conceptual':
          color = "#9DC0C3";
          break;
        case 'observational':
          color = "#D49C6A";
          break;
        case 'temporal':
          color = "#D4BE6A";
          break;
        default:
          color = "#bbb";
      }
      return color;
    }

    function labelColor(d) {
      var color = '#ddd';
      switch (d.category) {
        case 'physical':
          color = "#0E083B";
          break;
        case 'conceptual':
          color = "#013034";
          break;
        case 'observational':
          color = "#552800";
          break;
        case 'temporal':
          color = "#554300";
          break;
        default:
          color = "#333";
      }
      return color;
    }
  },

  //This function looks up whether a pair are neighbours
  neighboring: function(a, b) {
    return (typeof this.linkedByIndex[a + "," + b] == 'undefined' && typeof this.linkedByIndex[b + "," + a] == 'undefined' ? 0 : 1);
  },

  nodeClicked: function(node) {
    this.showNodeData(d3.select(node).datum());
  },

  linkClicked: function(link) {
    this.showLinkData(d3.select(link).datum());
  },

  showNodeData: function(nodeData) {

    var _this = this;
    var html = '<table class="table table-sm"><tr>';
    html += '<td width="100%" align="center" class="node-name">' +
      '<strong>' + nodeData.name + '</strong>' +
      '<span class="topic-message-buttons">' +
      ' <a class="zoomToNode btn btn-sm btn-outline-secondary" title="Zoom to this concept" href="javascript:;"><span class="fa fa-search"></span></a>' +
      ' <a id="centerNode" class="btn btn-sm btn-outline-secondary" title="Center this concept in a concentric layout" href="javascript:;"><span class="fa fa-dot-circle-o"></span></a>' +
      ' <a class="addNodeToFilter btn btn-sm btn-outline-secondary" title="Add this concept to your filters" href="javascript:;"><span class="fa fa-plus"></span></a>' +
      '</span>' +
      '</td>';
    html += '</tr></table>';
    _this.messageBox.html(html);
    if (_this.messageContainer.is(':visible')) {
      _this.messageContainer.addClass('shadow-pulse').on('animationend', function() {
        $(this).removeClass('shadow-pulse');
      });
    } else {
      _this.messageContainer.slideDown();
    }
    $('#centerNode').click(function() {
      _this.goConcentric(nodeData)
    });
    $('.zoomToNode').click(function() {
      _this.recenter(nodeData)
    });
    $('.addNodeToFilter').click(function() {
      _this.addToFilter(nodeData)
    });

  },

  showLinkData: function(linkData) {
    var _this = this;
    var html = '<table class="table table-sm"><tr>';
    html += '<td align="center" width="33%" class="node-name">' +
      '<strong>' + linkData.source.name + '</strong>' +
      '<span class="topic-message-buttons">' +
      ' <a class="zoomToSourceNode btn btn-sm btn-outline-secondary" title="Zoom to this concept" href="javascript:;"><span class="fa fa-search"></span></a>' +
      ' <a class="centerSourceNode btn btn-sm btn-outline-secondary" title="Center this concept in a concentric layout" href="javascript:;"><span class="fa fa-dot-circle-o"></span></a>' +
      ' <a class="addSourceNodeToFilter btn btn-sm btn-outline-secondary" title="Add this concept to your filters" href="javascript:;"><span class="fa fa-plus"></span></a>' +
      '</span>' +
      '</td>';
    html += '<td align="center" width="33%" class="node-name node-name-edge">' +
      '<strong>' + linkData.name + '</strong>' +
      '</td>';
    html += '<td align="center" width="33%" class="node-name">' +
      '<strong>' + linkData.target.name + '</strong>' +
      '<span class="topic-message-buttons">' +
      ' <a class="zoomToTargetNode btn btn-sm btn-outline-secondary" title="Zoom to this concept" href="javascript:;"><span class="fa fa-search"></span></a>' +
      ' <a class="centerTargetNode btn btn-sm btn-outline-secondary" title="Center this concept in a concentric layout" href="javascript:;"><span class="fa fa-dot-circle-o"></span></a>' +
      ' <a class="addTargetNodeToFilter btn btn-sm btn-outline-secondary" title="Add this concept to your filters" href="javascript:;"><span class="fa fa-plus"></span></a>' +
      '</span>' +
      '</td>';
    html += '</tr></table>';
    _this.messageBox.html(html);
    if (_this.messageContainer.is(':visible')) {
      _this.messageContainer.addClass('shadow-pulse').on('animationend', function() {
        $(this).removeClass('shadow-pulse');
      });
    } else {
      _this.messageContainer.slideDown();
    }
    $('.centerSourceNode').click(function() {
      _this.goConcentric(linkData.source)
    });
    $('.centerTargetNode').click(function() {
      _this.goConcentric(linkData.target)
    });
    $('.zoomToSourceNode').click(function() {
      _this.recenter(linkData.source)
    });
    $('.addSourceNodeToFilter').click(function() {
      _this.addToFilter(linkData.source)
    });
    $('.zoomToTargetNode').click(function() {
      _this.recenter(linkData.target)
    });
    $('.addTargetNodeToFilter').click(function() {
      _this.addToFilter(linkData.target)
    });
  },

  hoverNode: function(d) {
    var _this = this;
    var ids = [];
    this.graph.nodes.forEach(function(node) {
      if (_this.neighboring(node.id, d.id)) {
        ids.push(parseInt(node.id));
      };
    });
    this.highlightNodes(ids);
  },

  highlightTopics: function() {
    var _this = this;
    var ids = [];
    if (this.topics.length > 0) {
      this.graph.nodes.forEach(function(node) {
        if (_.indexOf(_this.topics, node.id) > -1) {
          ids.push(parseInt(node.id));
        };
      });
    }
    if (this.loaded)
      this.highlightNodes(ids);
  },

  highlightNodes: function(ids) {
    //accept array of nodes
    ids = typeof ids == 'undefined' || !ids ? [] : ids;

    var t = d3.transition("highlight")
      .duration(100)
      .ease(d3.easeLinear);

    if (ids.length > 0) {
      this.node
        .transition(t)
        .style('opacity', function(d) {
          if (_.indexOf(ids, parseInt(d.id)) > -1) {
            return 1;
          }
          return 0.2;
        });
      this.link
        .transition(t)
        .style('opacity', function(d) {
          if (_.indexOf(ids, parseInt(d.source.id)) > -1 && _.indexOf(ids, parseInt(d.target.id)) > -1) {
            return 1;
          }
          return 0.2;
        });
      this.label
        .transition(t)
        .style('opacity', function(d) {
          if (_.indexOf(ids, parseInt(d.id)) > -1) {
            return 1;
          }
          return 0.2;
        });
      this.markerPath
        .transition(t)
        .style('opacity', function(d) {
          if (_.indexOf(ids, parseInt(d.source.id)) > -1 && _.indexOf(ids, parseInt(d.target.id)) > -1) {
            return 1;
          }
          return 0.2;
        });
    } else {
      this.node
        .transition(t)
        .style('opacity', 1);
      this.link
        .transition(t)
        .style('opacity', 0.2);
      this.label
        .transition(t)
        .style('opacity', 1);
      this.markerPath
        .transition(t)
        .style('opacity', 0.2);
    }

  },

  addToFilter: function(node) {

    // var topicIds = Search.values.topicIds.concat([node.identifier]);
    Search.set('topicIds', node.identifier);

  },

  clearFilter() {
    Search.set('topicIds', null);
  },

  goConcentric: function(node) {

    var _this = this;

    this.breadthFirstSearch(node.id);

    this.drawConcentricGraph();

    this.simulation.alphaTarget(0.1).restart();
    setTimeout(function() { _this.simulation.alphaTarget(0); }, 1000);


    this.recenter(false, 1.5);

  },

  breadthFirstSearch: function(id) {

    var _this = this;

    this.graph.nodes.forEach(function(node) {

      node.level = null;

    });

    var rootNode = _this.graph.nodes.filter(node => node.id == id)[0];
    rootNode.level = 0;

    this.doSearch([rootNode], this.graph.nodes, 0);

    this.node.select("title")
      .text(function(d) { return d.level; });

  },

  doSearch: function(neighbors, unvisited, level) {

    var _this = this;
    var recursiveArray = [];

    var recursiveNeighbors = [];
    var recursiveUnvisited = [];

    level++;
    neighbors.forEach(function(rootNode) {

      unvisited.forEach(function(node) {

        if (_this.neighboring(node.id, rootNode.id) == 1) {
          if (node.level == null) {
            node.level = level;
            recursiveNeighbors.push(node);
          }
        }
        if (rootNode.id != node.id && (node.level == null || node.level == level)) {
          if (recursiveUnvisited.filter(unnode => unnode.id == node.id).length == 0) {
            recursiveUnvisited.push(node);
          }
        }

      });

    });
    if (unvisited.length == _.size(recursiveUnvisited)) {
      return;
    }

    if (recursiveNeighbors.length > 0 && _.size(recursiveUnvisited) > 0) {
      this.doSearch(recursiveNeighbors, recursiveUnvisited, level);
    }
  },

  drawConcentricGraph: function(id) {
    var _this = this;

    this.concentricActive = true;

    this.levels = d3.set(this.graph.nodes.map(function(node) {
      return +node.level;
    })).values();

    this.levels.sort();

    this.circles.empty();

    this.circles
      .selectAll('.boundary')
      .data(_this.levels)
      .enter()
      .append('circle')
      .attr('r', function(d, index) {
        return (index + 1) * _this.settings.maxRadStep;
      }).attr('cx', _this.w / 2).attr('cy', _this.h / 2);
  },

  recenter: function(node, scale) {

    var _this = this;
    scale = typeof scale == 'undefined' ? 2 : scale;
    if (node) {
      var newTransform = d3.zoomIdentity
        .translate(_this.w / 2 - node.x * scale, _this.h / 2 - node.y * scale)
        .scale(scale);
    } else {
      var newTransform = d3.zoomIdentity
        .translate(_this.w / 2 - _this.w / 2 * scale, _this.h / 2 - _this.h / 2 * scale)
        .scale(scale);
    }

    this.svg.transition("zoom")
      .duration(750)
      .call(this.zoom.transform, newTransform);

  },

  refresh: function() {
    var _this = this;
    this.topics = [];
    if (typeof Search.results['matched'].facets.T_id != 'undefined' && _this.graph.nodes != undefined) {
      if (Object.keys(Search.results['matched'].facets.T_id).length > 0) {
        _.each(Search.results['matched'].facets.T_id, function(value, topic) {
          if (!_.isEmpty(_this.graph.nodes.filter(node => node.identifier == topic)[0])) {
            _this.topics.push(_this.graph.nodes.filter(node => node.identifier == topic)[0].id);
          }
        });
      }
    }
    this.highlightTopics();
  },

  reset: function() {
    this.concentricActive = false;
    this.gContainer.remove();
    this.formatData(this.rawData);
  },

  pause: function() {
    this.graph.nodes.forEach(function(node) {
      node.fx = node.x;
      node.fy = node.y;
    });

    //this.simulation.stop();
    //this.simulation.alphaTarget(0).restart();
    this.paused = true;
  },

  proceed: function() {
    this.graph.nodes.forEach(function(node) {
      node.fx = null;
      node.fy = null;
    });
    this.simulation.restart();
    this.paused = false;
  },

  zoomIn: function() {
    var _this = this;
    _this.zoom.scaleBy(_this.svg.transition('zoomin').duration(400), 2);
  },

  zoomOut: function() {
    var _this = this;
    _this.zoom.scaleBy(_this.svg.transition('zoomin').duration(400), 0.5);
  },

  resize: function() {
    var _this = this;
    this.w = this.container.width();
    this.h = this.container.height();
    this.svg
      .attr('width', this.w)
      .attr('height', this.h);
    this.simulation
      .force(d3.forceCenter(_this.w / 2, _this.h / 2));
  },

  initSearch: function() {

    var _this = this;

    var allNodes = new Bloodhound({
      datumTokenizer: Bloodhound.tokenizers.obj.whitespace('name'),
      queryTokenizer: Bloodhound.tokenizers.whitespace,
      local: _this.graph.nodes
    });

    this.searchBox.typeahead({
      hint: true,
      highlight: true,
      /* Enable substring highlighting */
      minLength: 1
    }, {
      name: 'all-nodes',
      display: 'name',
      limit: 10,
      source: allNodes,
      templates: {
        suggestion: function(data) {
          if (data.active) {
            return '<tr><td width="100%" align="left" class="node-name">' + data.name + ' </td></tr>';
          } else {
            return '<tr><td width="100%" align="left" class="node-name" style="color:#888;">' + data.name + ' (Not visible)</td></tr>';
          }
        }
      }
    }).bind('typeahead:select', function(ev, suggestion) {
      _this.recenter(suggestion);
      _this.showNodeData(suggestion);
    });

  }


};


var Concepts = new TopicMap(topicSettings);