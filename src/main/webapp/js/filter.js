// Requires search.js and concepts.js

var FilterObject = function() {
  this.settings = {
    container: '#filters',
    types: ['keywords', 'temporal', 'spatial', 'concepts', 'sources'],
    typemap: {
      'keywords': 'keywords',
      'temporal': 'temporal',
      'spatial': 'spatial',
      'concepts': 'topicIds',
      'sources': 'sources',
    },
  }
};

FilterObject.prototype = {

  wait: function() {
    $(this.settings.container).append(this.loader);
  },

  refresh: function() {
    // Set container
    $(this.settings.container).empty();

    // configure the loader object
    this.loader = $('<div>', { 'class': 'loader col-sm-12 text-center' });
    this.loader.append('<h1><i class="fa fa-cog fa-spin fa-2x"></i></h1>');

    // get the search values
    this.search_values = Search['values'];

    // prepare the dom
    this.prepare();

    // update the dom from the search values
    this.update();

    // cleanup and remove any empty fields
    this.cleanup();

    // click event
    $('.filters-remove').click(this.remove);
  },

  prepare: function(type) {
    // purge existing dom elements
    $(this.settings.container).empty();

    // create new placeholders
    this.settings.types.forEach(type => {
      var dom = $('<div>', {'class': 'col-sm filters-container', 'id': 'filters-' + type});
      dom.append('<h5>' + type + '</h5>');
      $(this.settings.container).append(dom);
    });
  },

  update: function() {
    // add the keyword filters
    if (!_.isEmpty(this.search_values[this.settings.typemap.keywords])) {
      for (keyword of this.search_values[this.settings.typemap.keywords]) {
        this.append('keywords', keyword, keyword);
      }
    }

    // add the spatial filters
    if (!_.isEmpty(this.search_values[this.settings.typemap.spatial])) {
      var spatial = this.search_values[this.settings.typemap.spatial];
      if (_.has(spatial, 'region')) {
        var [layerid, featureid] = spatial.region.split('_____');
        var feature = Geography.pLayers[layerid].selected.feature;
        if (featureid == feature.properties.id) {
          this.append('spatial', null, 'Region [' + feature.properties.SVEITARFEL + ']');
        }
      }
      if (_.has(spatial, 'topLeft')) {
        var top_left = [spatial.topLeft[0].toFixed(2), spatial.topLeft[1].toFixed(2)];
        var bottom_right = [spatial.bottomRight[0].toFixed(2), spatial.bottomRight[1].toFixed(2)];
        this.append('spatial', null, 'Bounding Box [' + top_left + '] [' + bottom_right + ']');
      }
    }

    // add the timeline filters
    if (!_.isEmpty(this.search_values[this.settings.typemap.temporal])) {
      this.append('temporal', null, this.search_values[this.settings.typemap.temporal].start +' - ' + this.search_values[this.settings.typemap.temporal].end);
    }

    // add the concept filters
    if (!_.isEmpty(this.search_values[this.settings.typemap.concepts])) {
      for (id of this.search_values[this.settings.typemap.concepts]) {
        var concept = Concepts.graph.nodes.filter(x => x.identifier.indexOf(id) > -1);
        if (concept.length > 0) {
          this.append('concepts', id, concept[0].name);
        }
      };
    }
  },

  append: function(type, key, value) {
    var dom = $('<div>', { 'class': 'p-3 mb-2 filter-' + type + ' text-white', 'data-type': type, 'data-key': key, 'data-value': value });
    dom.append(value + ' <button type="button" class="close filters-remove" aria-label="Remove"><span aria-hidden="true">&times;</span></button>');
    $(this.settings.container+'-'+type).append(dom);
  },

  cleanup: function() {
    this.settings.types.forEach(type => {
      $(this.settings.container+'-'+type).not(':has(div)').remove();
    });
  },

  remove: function() {
    var filter = $(this).parent();
    var type = filter.data('type');
    var key = filter.data('key');
    var value = filter.data('value');

    // remove the filter
    Search.unset(Filter.settings.typemap[type], key);
  }

};

var Filter = new FilterObject();