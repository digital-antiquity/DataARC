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

var _PAGE_SIZE = 500;

var Search = {

  init: function(options) {
    this.defaults = {
      "spatial": {
        "topLeft": [-37, 68.46],
        "bottomRight": [-2.75, 55]
      },
      "temporal": {
        "start": null,
        "end": null
      },
      "idOnly": true,
      "keywords": [],
      "topicIds": [],
      "sources": []
    };
    this.values = $.extend({},this.defaults);
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
      Search.query("POST", {"spatial":Search.defaults.spatial, "size":0,"page":0,'facetOnly':true}, Search.analyzeFirst);
    else
      Search.query("POST", Search.values, Search.analyze);
  },

  analyzeFirst: function(error, data) {
    if (error) throw error;

    // Save all the data
    Search.all.features = [];
    Search.all.facets = data.facets;
    Search.facets = data.facets;
    console.log(Search.facets);
    console.log('Loaded all ' + Search.all.features.length + ' features.');
    // Once all data is loaded, fire of the results query
    Search.analyze(false, data);
//    Search.results = [];
    Search.query("POST", {"spatial":Search.defaults.spatial, "size":_PAGE_SIZE,"page":0}, Search.analyzePage);
    
  },

  analyzePage: function(error, data) {
    if (error) throw error;

    // Save all the data
    if (data.results == undefined) {
        return;
    }
    data.results.features.forEach(function(r) {
        Search.all.features.push(r);
    })
//    Search.all.facets.push( data.facets);
    // Once all data is loaded, fire of the results query
    data.idList.forEach(function(r) {
        Search.results.push(r);
    })
//    console.trace('Loaded page of ' + Search.all.features.length + ' features. Results:' + Search.results.length);
    var size = data.results.features.length;

    Geography.addFeatures(data.results.features, Geography._defaultStyle, false);
    $('#results-count').text(Search.results.length);
    if (size > 0) {
        Search.query("POST", {"spatial":Search.defaults.spatial, "size":_PAGE_SIZE,"page":Search.all.features.length}, Search.analyzePage);

    }  else {
        Search.options.after();
    }

  },

  analyze: function(error, data) {
    Search.error = (error);
    if (Search.error) {
      // Set results empty
      Search.results = [];
      Search.facets = {};
      console.log(error);
    }
    else {
      // Save the results
      Search.results = (data.idList ? data.idList : []);
      Search.facets = (data.facets ? data.facets : {});
      console.log('Loaded results containing ' + Search.results.length + ' features.');
    }
    // Proceed to format data as needed here
    Search.options.after();
  },

  query: function(type, filters, callback) {
      console.log(Search.options.source, filters);
    if (type === "GET")
      d3.json(Search.options.source+'?'+$.param(filters)).header("Content-Type", "application/json;charset=UTF-8").get(callback);
    if (type === "POST")
      d3.json(Search.options.source).header("Content-Type", "application/json;charset=UTF-8").post(JSON.stringify(filters), callback);
  },

  // ****************************************************
  // Specific runctions to return a subset of results
  // ****************************************************

  // get detail information for a specific id
  getDetailsById: function(id, callback) {
      d3.json(Search.options.recordSource+'?id='+ id).header("Content-Type", "application/json;charset=UTF-8").get(callback);
  },

  // get results by id or array of ids
  getResultsById: function(ids, callback, local) {
    if (typeof ids === 'string')
      ids = [ids];
    if (local)
      return Search.all.features.filter(feature => id.includes(feature.properties.id));
    else
      Search.query("POST", {ids:ids}, callback);
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
    return Search.all.features.filter(feature => feature.region === file_id+'_____'+polygon_id);
  },

  // get results within specific category
  getResultsByCategory: function(category) {
    return Search.all.features.filter(feature => feature.properties.category.toLowerCase() == category.toLowerCase());
    // returns random features
    // var features = [];
    // var i = 0;
    // for (var p in Search.all.features) {
    //   i = Math.floor(Math.random() * (1000 - 1 + 1)) + 1;
    //   if(i % 4 == 0){
    //     features.push(Search.all.features[p]);
    //   }
    // }
    // return features;
  },
};