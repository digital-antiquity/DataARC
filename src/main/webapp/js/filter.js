'use strict';

/**
 * Filter handler class for data-arc
 * Requires ECMA6, Lodash, jQuery
 */

/**
 * Filter handler
 */
class FilterHandler {

  constructor() {
    this.settings = {
      container: '#filters',
      types: ['keywords', 'temporal', 'spatial', 'concepts', 'sources'],
      typemap: {
        'keywords': 'keywords',
        'temporal': 'temporal',
        'spatial': 'spatial',
        'concepts': 'topicIds',
        'sources': 'sources',
      }
    }

    // set the container
    this.container = $(this.settings.container);

    // configure the loader object
    this.loader = $('<div>', { 'class': 'loader col-sm-12 text-center' });
    this.loader.append('<h1><i class="fa fa-cog fa-spin fa-2x"></i></h1>');
  }

  /**
   * Refresh UI
   * @return {void}
   */
  refresh() {
    this.count = 0;

    // get the search values
    this.filters = Search.values;

    // prepare the dom
    this.prepare();

    // update the dom from the search values
    this.update();

    // cleanup and remove any empty fields
    this.cleanup();

    // click event
    $('.filters-remove').click(this.remove);
  }

  wait() {
    this.container.append(this.loader);
  }

  prepare() {
    // purge existing dom elements
    this.container.empty();

    // create new placeholders
    for (let type of this.settings.types) {
      var dom = $('<div>', {'class': 'col-sm filters-container', 'id': 'filters-' + type});
      dom.append('<h5>' + type + '</h5>');
      this.container.append(dom);
    };
  }

  update() {
    // add the keyword filters
    if (!_.isEmpty(this.filters[this.settings.typemap.keywords])) {
      var keywords = this.filters[this.settings.typemap.keywords];
      for (let keyword of keywords) {
        this.append('keywords', keyword, keyword);
      }
    }

    // add the spatial filters
    if (!_.isEmpty(this.filters[this.settings.typemap.spatial])) {
      var spatial = this.filters[this.settings.typemap.spatial];
      if (spatial.region != null) {
        var [layerid, featureid] = spatial.region.split('_____');
        var feature = Geography.getGeojsonFeature(layerid, featureid);
        if (feature.properties.name) {
          this.append('spatial', null, 'Region [' + feature.properties.name + ']');
        }
        if (feature.properties.SVIETARFEL) {
          this.append('spatial', null, 'Region [' + feature.properties.SVEITARFEL + ']');
        }
      }
      if (spatial.topLeft != null && spatial.bottomRight != null) {
        var top_left = [spatial.topLeft[0].toFixed(2), spatial.topLeft[1].toFixed(2)];
        var bottom_right = [spatial.bottomRight[0].toFixed(2), spatial.bottomRight[1].toFixed(2)];
        this.append('spatial', null, 'Bounding Box [' + top_left + '] [' + bottom_right + ']');
      }
    }

    // add the timeline filters
    if (!_.isEmpty(this.filters[this.settings.typemap.temporal])) {
      var temporal = this.filters[this.settings.typemap.temporal];
      if (temporal.period != null) {
        this.append('temporal', null, temporal.period);
      }
      if (temporal.start !== null && temporal.end !== null) {
        this.append('temporal', null, temporal.start +' - ' + temporal.end);
      }
    }

    // add the concept filters
    if (!_.isEmpty(this.filters[this.settings.typemap.concepts])) {
      var concepts = this.filters[this.settings.typemap.concepts];
      for (let id of concepts) {
        var concept = Concepts.graph.nodes.filter(x => x.identifier.indexOf(id) > -1);
        if (concept.length > 0) {
          this.append('concepts', id, concept[0].name);
        }
      };
    }
  }

  append(type, key, value) {
    this.count++;
    var dom = $('<div>', { 'class': 'p-3 mb-2 filter-' + type + ' text-white', 'data-type': type, 'data-key': key, 'data-value': value });
    dom.append(value + ' <button type="button" class="close text-light filters-remove" aria-label="Remove"><span aria-hidden="true">&times;</span></button>');
    $(this.settings.container+'-'+type).append(dom);
  }

  cleanup() {
    for (let type of this.settings.types) {
      $(this.settings.container+'-'+type).not(':has(div)').remove();
    }
  }

  remove() {
    var filter = $(this).parent();
    var type = filter.data('type');
    var key = filter.data('key');
    var value = filter.data('value');

    // remove the filter
    Search.unset(Filters.settings.typemap[type], key);

    // clear the filters
    if (type === 'keywords') {
      $('#keywords-field').val(null);
    }
    if (type === 'temporal') {
      Timeline.clearFilter();
    }
    if (type === 'spatial') {
      Geography.clearFilters();
    }
    if (type === 'concepts') {
      Concepts.clearFilter();
    }
  }
}