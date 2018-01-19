/*
{
  "spatial": {
    "topLeft": null,
    "bottomRight": null
  },
  "temporal": {
    "start": null,
    "end": null
  },
  "idOnly": false,
  "idAndMap": false,
  "keywords": [],
  "topicIds": [],
  "sources": [],
  "ids": []
}
 */

var Search = {

  init: function(options) {
    this.defaults = {
      "spatial": {
        "topLeft": [-75, 85],
        "bottomRight": [-0.1, 58]
      },
      "temporal": null,
      "idOnly": true,
      "keywords": [],
      "topicIds": [],
      "sources": []
    };
    this.values = $.extend({}, this.defaults);
    this.all = {};
    this.results = {};
    this.facets = {};
    this.previous = null;
    this.revision = Date.now();
    this.errors = 0;
    this.options = options;

    // Set an interval to check on query changes
    setInterval(function() { Search.refresh(); }, Search.options.delay);
  },

  get: function(key) {
    var value;
    if (this.values.hasOwnProperty(key)) {
      value = this.values[key];
    }
    return value;
  },

  set: function(key, value) {
    var previous_value = (this.values[key] ? this.values[key] : null);
    this.values[key] = (value == null ? this.defaults[key] : value);
    if (previous_value != this.values[key]) {
      this.previous = this.revision;
      this.revision = Date.now();
    }
  },

  unset: function(key) {
    delete this.values[key];
  },

  refresh: function() {
    // Exit if the search parameters have not been revised
    if (this.previous == this.revision) return;

    // Perform any before search actions
    Search.options.before();

    // Sync the revision information and run the search
    this.previous = this.revision;

    // If first run then get all data before loading results
    if (Object.keys(Search.all).length === 0 && Search.all.constructor === Object)
      Search.query({ "spatial": Search.defaults.spatial }, Search.analyzeFirst);
    else
      Search.query(Search.values, Search.analyze);
  },

  analyzeFirst: function(error, data) {
    if (error) throw error;

    // Save all the data
    Search.all.features = data.results.features;
    Search.all.facets = data.facets;
    console.log('Loaded all ' + Search.all.features.length + ' features.');

    // Once all data is loaded, fire of the results query
    Search.analyze(false, data);
  },

  analyze: function(error, data) {
    Search.error = (error);
    if (Search.error) {
      // Set results empty
      Search.results = [];
      Search.facets = {};
      console.log(error);
    } else {
      // Save the results
      Search.results = (data.idList ? data.idList : []);
      Search.facets = (data.facets ? data.facets : {});
      console.log('Loaded results containing ' + Search.results.length + ' features.');
    }
    // Proceed to format data as needed here
    Search.options.after();
  },

  query: function(filters, callback) {
    d3.json(Search.options.source).header("Content-Type", "application/json;charset=UTF-8").post(JSON.stringify(filters), callback);
  },

  // ****************************************************
  // Specific runctions to return a subset of results
  // ****************************************************

  // get detail information for a specific id
  getDetailsById: function(id, callback) {
    d3.json(Search.options.recordSource+'?id='+id).header("Content-Type", "application/json;charset=UTF-8").get(callback);
  },

  // get results by id or array of ids
  getResultsById: function(ids, callback, local) {
    if (typeof ids === 'string')
      ids = [ids];
    if (local)
      return Search.all.features.filter(feature => id.includes(feature.properties.id));
    else
      Search.query({ ids: ids }, callback);
  },

  // get results by a source or sources
  getResultsBySource: function(sources) {
    if (typeof source === 'string')
      sources = [sources];
    return Search.all.features.filter(feature => sources.includes(feature.properties.source));
  },

  // get results by keyword
  getResultsByKeyword: function(keyword) {
    return Search.all.features.filter(feature => feature.properties.keywords.indexOf(keyword) > -1);
  },

  // get results by decade
  getResultsByDecade: function(decade) {
    return Search.all.features.filter(feature => feature.properties.decade.includes(decade));
  },

  // get results by millenium
  getResultsByMillenium: function(millenium) {
    return Search.all.features.filter(feature => feature.properties.millenium.includes(millenium));
  },

  // get results by century
  getResultsByCentury: function(century) {
    return Search.all.features.filter(feature => feature.properties.century.includes(century));
  },

  // get results by bounding box a,b = lat,lng and c,d = lat,lng
  getResultsByBounds: function(a, b, c, d) {
    var bounds = L.latLngBounds(L.latLng(a, b), L.latLng(c, d));
    return Search.all.features.filter(feature => bounds.contains(L.latLng(feature.geometry.coordinates[1], feature.geometry.coordinates[0])));
  },

  // get results by region
  getResultsByPolygon: function(file_id, polygon_id) {
    return Search.all.features.filter(feature => feature.region === file_id + '_____' + polygon_id);
  },

  // get results within specific category
  getResultsByCategory: function(category) {
    return Search.all.features.filter(feature => feature.properties.category.toLowerCase() == category.toLowerCase() && Search.results.indexOf(feature.properties.id) > -1);
  },
};