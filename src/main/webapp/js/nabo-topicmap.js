$(function(){

  var layout,canvas,$canvas,canvasContainer,sourceCanvas,ctx,bindCircles;
  var circleSpecs = false;
  var layoutPadding = 10;
  var aniDur = 500;
  var easing = 'linear';
  var roots = false;

  var cy;

  // get network data via ajax
  var graphP = $.ajax({
    url: '/prototype/api/topicmap/view',
    type: 'GET',
    dataType: 'json'
  });

  // also get style via ajax
  var styleP = $.ajax({
    url: '/prototype/css/style.cy.css',
    type: 'GET',
    dataType: 'text'
  });

  Promise.all([ graphP ]).then(formatData);
  function formatData(data){
    var outData = {"data":{"selected":true},"elements":{}};
    var nodes = [];
    var edges = [];
    _.each(data[0].topics, function(value){
      nodes.push({"data": {
        "id": value.id.toString(),
        "shared_name": value.name,
        "name": value.name,
        "SUID": value.id,
        "selected": false
        }
      });
    });
    _.each(data[0].associations, function(value){
      edges.push({"data": {
        "id": value.id.toString(),
        "shared_name": value.name,
        "name": value.name,
        "shared_interaction": value.name,
        "interaction": value.name,
        "SUID": value.id,
        "selected": false,
        "source": value.from,
        "target": value.to
        }
      });
    });
    outData.elements.nodes = nodes;
    outData.elements.edges = edges;

    Promise.all([ outData, styleP ]).then(initCy);
  }

  // when both graph export json and style loaded, init cy
  //Promise.all([ graphP, styleP ]).then(initCy);

  var allNodes = null;
  var allEles = null;
  var lastHighlighted = null;
  var lastUnhighlighted = null;

  function getFadePromise( ele, opacity ){
    return ele.animation({
      style: { 'opacity': opacity },
      duration: aniDur
    }).play().promise();
  };

  var restoreElesPositions = function( nhood ){
    return Promise.all( nhood.map(function( ele ){
      var p = ele.data('orgPos');

      return ele.animation({
        position: { x: p.x, y: p.y },
        duration: aniDur,
        easing: easing
      }).play().promise();
    }) );
  };

  function highlight( node ){
    clearCircles();
    cy.off('zoom pan', bindCircles);
    var oldNhood = lastHighlighted;

    var nhood = lastHighlighted = node.closedNeighborhood();
    var others = lastUnhighlighted = cy.elements().not( nhood );

    var reset = function(){
      cy.batch(function(){
        others.addClass('hidden');
        nhood.removeClass('hidden');

        allEles.removeClass('faded highlighted');

        nhood.addClass('highlighted');

        others.nodes().forEach(function(n){
          var p = n.data('orgPos');

          n.position({ x: p.x, y: p.y });
        });
      });

      return Promise.resolve().then(function(){
        if( isDirty() ){
          return fit();
        } else {
          return Promise.resolve();
        };
      }).then(function(){
        return Promise.delay( aniDur );
      });
    };

    var runLayout = function(){
      cy.off('zoom pan', bindCircles);
      var p = node.data('orgPos');

      var l = nhood.filter(':visible').makeLayout({
        name: 'concentric',
        fit: false,
        animate: true,
        animationDuration: aniDur,
        animationEasing: easing,
        boundingBox: {
          x1: p.x - 1,
          x2: p.x + 1,
          y1: p.y - 1,
          y2: p.y + 1
        },
        avoidOverlap: true,
        concentric: function( ele ){
          if( ele.same( node ) ){
            return 2;
          } else {
            return 1;
          }
        },
        levelWidth: function(){ return 1; },
        padding: layoutPadding
      });

      var promise = cy.promiseOn('layoutstop');

      l.run();

      return promise;
    };

    var fit = function(){
      return cy.animation({
        fit: {
          eles: nhood.filter(':visible'),
          padding: layoutPadding
        },
        easing: easing,
        duration: aniDur
      }).play().promise();
    };

    var showOthersFaded = function(){
      return Promise.delay( 250 ).then(function(){
        cy.batch(function(){
          others.removeClass('hidden').addClass('faded');
        });
      });
    };

    return Promise.resolve()
      .then( reset )
      .then( runLayout )
      .then( fit )
      .then( showOthersFaded )
    ;

  }

  function isDirty(){
    return lastHighlighted != null;
  }

  function clear( opts ){

    if( !isDirty() ){ return Promise.resolve(); }

    opts = $.extend({

    }, opts);

    cy.stop();
    allNodes.stop();

    var nhood = lastHighlighted;
    var others = lastUnhighlighted;

    lastHighlighted = lastUnhighlighted = null;

    var hideOthers = function(){
      return Promise.delay( 125 ).then(function(){
        others.addClass('hidden');

        return Promise.delay( 125 );
      });
    };

    var showOthers = function(){
      cy.batch(function(){
        allEles.removeClass('hidden').removeClass('faded');
      });

      return Promise.delay( aniDur );
    };

    var restorePositions = function(){
      cy.batch(function(){
        others.nodes().forEach(function( n ){
          var p = n.data('orgPos');

          n.position({ x: p.x, y: p.y });
        });
      });

      return restoreElesPositions( nhood.nodes() );
    };

    var resetHighlight = function(){
      nhood.removeClass('highlighted');
    };

    return Promise.resolve()
      .then( resetHighlight )
      .then( hideOthers )
      .then( restorePositions )
      .then( showOthers )
    ;
  }


  function showNodeInfo( node ){
    var html = '<tr>';
    html += '<td align="center" class="node-name">'+node.data().name+' (<a id="centerNode" title="Center this node in a concentric layout" data-id="'+node.data().id+'" href="javascript:;">Center</a>)</td>';
    html += '</tr>';
    $('#infotable').html(html).show();
    $('#centerNode').on('click',centerNode);
  }

  function showEdgeInfo( edge ){
    var html = '<tr>';
    html += '<td align="center" class="node-name">'+edge.source().data().name+'</td>';
    html += '<td align="center" class="node-name node-name-edge">'+edge.data().name+'</td>';
    html += '<td align="center" class="node-name">'+edge.target().data().name+'</td>';
    html += '</tr>';
    $('#infotable').html(html).show();
    $('#centerNode').on('click',centerNode);
  }

  function hideNodeInfo(){
    $('#infotable').hide();
  }

  function initCy( then ){
    var loading = document.getElementById('loading');
    var expJson = then[0];
    var styleJson = then[1];
    var elements = expJson.elements;

    loading.classList.add('loaded');

    cy = window.cy = cytoscape({
      container: document.getElementById('topicmap'),
      layout: { name: 'spread', padding: layoutPadding, directed: true, stop:setOrgPos },
      style: styleJson,
      elements: elements,
      motionBlur: true,
      selectionType: 'single',
      boxSelectionEnabled: false,
      autoungrabify: true,
      wheelSensitivity: 0.15,
    });

    allNodes = cy.nodes();
    allEles = cy.elements();

    roots = allNodes.first().id();

    allNodes.forEach(function(n){
      var p = n.position();
      var newPos = {x: p.x, y: p.y};
      n.data('orgPos', newPos);
      n.data('presetPos', newPos);
    });

    cy.on('free', 'node', function( e ){
      var n = e.cyTarget;
      var p = n.position();

      n.data('orgPos', {
        x: p.x,
        y: p.y
      });
    });

    cy.on('tap', function(){
      $('#search').blur();
    });

    cy.on('pan zoom', clearCircles);

    cy.on('select unselect', 'node, edge', _.debounce( function(e){
      var node = cy.$('node:selected');
      var edge = cy.$('edge:selected');

      if( node.nonempty() ){
        var nodeClickedEvent = jQuery.Event('nodeclicked');
        nodeClickedEvent.id = node.id();
        nodeClickedEvent.name = node.data().name;
        $( "body" ).trigger(nodeClickedEvent);

        var $term = $("#term");
        $term.val(node.data().name);
        $term.trigger("keyup");
        
        showNodeInfo( node );

        Promise.resolve().then(function(){
          return highlight( node );
        });
      } else if( edge.nonempty() ){
        showEdgeInfo( edge );
        console.log('Edge id = '+edge.id());
      } else {
        hideNodeInfo();
        clear();
        if(layout=='concentric'){
            cy.on('zoom pan', _.debounce(bindCircles, 100));
        } else {
          cy.on('zoom pan', _.debounce(bindCircles, 100));
        }
      }

    }, 100 ) );

    canvasContainer = $("canvas[data-id = 'layer0-selectbox']").parent();
    sourceCanvas = $("canvas[data-id = 'layer0-selectbox']")[0];
    $canvas = $( '<canvas></canvas>' );
    canvas = $canvas[0];
    canvasContainer.append($canvas);
    ctx = canvas.getContext('2d');
    resizeMyCanvas();
  }

  var lastSearch = '';

  $('#search').typeahead({
    minLength: 2,
    highlight: true,
  },
  {
    name: 'search-dataset',
    source: function( query, cb ){
      function matches( str, q ){
        str = (str || '').toLowerCase();
        q = (q || '').toLowerCase();

        return str.match( q );
      }

      var fields = ['name'];

      function anyFieldMatches( n ){
        for( var i = 0; i < fields.length; i++ ){
          var f = fields[i];

          if( matches( n.data(f), query ) ){
            return true;
          }
        }
        return false;
      }

      function getData(n){
        var data = n.data();

        return data;
      }

      function sortByName(n1, n2){
        if( n1.data('name') < n2.data('name') ){
          return -1;
        } else if( n1.data('name') > n2.data('name') ){
          return 1;
        }

        return 0;
      }

      var res = allNodes.stdFilter( anyFieldMatches ).sort( sortByName ).map( getData );

      cb( res );
    },
    templates: {
      suggestion: function (data) {
        console.log(data);
        return '<p class="node-name search-results">' + data.name + '</p>';
      }
    }
  }).on('typeahead:selected', function(e, entry, dataset){
    var n = cy.getElementById(entry.id);

    cy.batch(function(){
      allNodes.unselect();

      n.select();
    });

    showNodeInfo( n );
  }).on('keydown keypress keyup change', _.debounce(function(e){
    var thisSearch = $('#search').val();

    if( thisSearch !== lastSearch ){
      $('.tt-dropdown-menu').scrollTop(0);

      lastSearch = thisSearch;
    }
  }, 50));

  $('#reset').on('click', function(){
    if( isDirty() ){
      clear();
    } else {
      allNodes.unselect();

      hideNodeInfo();

      cy.stop();

      cy.animation({
        fit: {
          eles: cy.elements(),
          padding: layoutPadding
        },
        duration: aniDur,
        easing: easing
      }).play();
    }
  });

  $('#change_layout').qtip({
    position: {
      my: 'top center',
      at: 'bottom center',
      adjust: {
        method: 'shift'
      },
      viewport: true
    },

    show: {
      event: 'click'
    },

    hide: {
      event: 'unfocus'
    },

    style: {
      classes: 'qtip-bootstrap',
      tip: {
        width: 16,
        height: 8
      }
    },

    content: $('#layouts')
  });

  $('#layout-select').on('change',function(){
    layout = this.value;
    changeLayout(layout);
  });

  $('#zoom_in').on('click',function(){
    var options = {
      level: cy.zoom()*1.2,
      position: {
        x: cy.width()/2,
        y: cy.height()/2
      }
    }
    cy.zoom(options);
  });

  $('#zoom_out').on('click',function(){
    var options = {
      level: cy.zoom()-cy.zoom()*0.2,
      position: {
        x: cy.width()/2,
        y: cy.height()/2
      }
    }
    cy.zoom(options);
  });



  function resizeMyCanvas(){
    pxRatio = getPixelRatio();
    $canvas
      .attr( 'height', canvasContainer.height() )
      .attr( 'width', canvasContainer.width() )
      .css( {
        'position': 'absolute',
        'top': 0,
        'left': 0,
        'z-index': 0
      } );
    pan = cy.pan();
    ePan = {x: pan.x, y:pan.y};
    zoom = cy.zoom();
    eZoom = zoom * pxRatio;
    ctx.translate( ePan.x, ePan.y );
    ctx.scale( eZoom, eZoom );
  }

  function getPixelRatio(){
    srcCtx = sourceCanvas.getContext('2d');

    var backingStore = srcCtx.backingStorePixelRatio ||
      srcCtx.webkitBackingStorePixelRatio ||
      srcCtx.mozBackingStorePixelRatio ||
      srcCtx.msBackingStorePixelRatio ||
      srcCtx.oBackingStorePixelRatio ||
      srcCtx.backingStorePixelRatio || 1;

    return (window.devicePixelRatio || 1) / backingStore; // eslint-disable-line no-undef
  };

  function changeLayout(layout, id=false){
    cy.off('zoom pan', bindCircles);
    allEles.restore();
    var layoutOptions = {name: layout, animate: true, animationDuration: 1000, animationEasing: easing, stop: setOrgPos, avoidOverlap: true };
    if(layout=='preset'){
      circleSpecs = false;
      layoutOptions.positions = function(node){ return node.data('presetPos')};
    } else if(layout=='concentric'){
      circleSpecs = {};
      allNodes.forEach(function(n){
        n.removeData('ccLevel');
      });
      if(id){ roots = id };
      circleSpecs.num = 0;
      cy.elements().bfs({
        roots: '#'+roots,
        visit: function(i, depth){
          //if(!this.data('ccLevel')){
          if(depth < 4 && !this.data('ccLevel')){
            if (circleSpecs.num <= depth){
              circleSpecs.num++;
            }
            this.data('ccLevel',depth);
          } 
          else {
            this.remove();
          }
        },
        directed: false
      });
      allNodes.forEach(function(n){
        if(!(parseInt(n.data('ccLevel')) > -1)){
          n.remove();
        }
      });
      layoutOptions.concentric = function(node){ return 100 - node.data('ccLevel'); };

      layoutOptions.levelWidth = function(node){ return 1; };
      layoutOptions.equidistant = true;
      layoutOptions.avoidOverlap = true;
    } else if(layout=='cola'){
      circleSpecs = false;
      layoutOptions.maxSimulationTime = 1000;
    } 
    clear();
    cy.elements().layout(layoutOptions);

    if(layout=='concentric'){ 
      setTimeout(function() {
        bindCircles();
        cy.on('zoom pan', _.debounce(bindCircles, 100));
      }, 1100);
    } else {
      console.log('bind off');
      cy.off('zoom pan', bindCircles);
      clearCircles();
    }
  }

  function setOrgPos(){
    allNodes.forEach(function(n){
      var p = n.position();
      var newPos = {x: p.x, y: p.y};
      n.data('orgPos', newPos);
    });
    
  }

  function centerNode(data){
    var id = $('#centerNode').data('id');
    Promise.resolve()
    .then(clear)
    setTimeout(function() {layout = 'concentric'; changeLayout('concentric',id)}, 1500);
  }

  var bindCircles = function(){
    resizeMyCanvas();
    clearCircles();
    if(layout=='concentric'){
      drawCircles();
    }
  }

  function drawCircles() {
    var center_node = cy.elements("node[ccLevel = 0]")[0];
    cnPos = center_node.position();
    var next_node = cy.elements("node[ccLevel = 1]")[0];
    nnPos = next_node.position();
    circlePos = {x:cnPos.x, y:cnPos.y};
    circleSpecs.x = circlePos.x;
    circleSpecs.y = circlePos.y;
    circleSpecs.num = (circleSpecs.num ? circleSpecs.num : 10), 
    circleSpecs.rho = Math.sqrt((cnPos.x - nnPos.x)*(cnPos.x - nnPos.x) + (cnPos.y - nnPos.y)*(cnPos.y - nnPos.y));
    ctx.strokeStyle = 'rgba(0,0,0,.2)';
    for(var i=1; i<=circleSpecs.num; i++) {
      ctx.lineWidth = 4;
      ctx.beginPath();
      ctx.arc(circleSpecs.x, circleSpecs.y, circleSpecs.rho * i, 0, 2 * Math.PI, false);
      ctx.stroke();
      ctx.closePath();
    }
  }

  function clearCircles(){
    ctx.save();
    ctx.setTransform(1, 0, 0, 1, 0, 0);
    // Will always clear the right space
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.restore();
  }
});