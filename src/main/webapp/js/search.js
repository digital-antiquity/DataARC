/* EXAMPLE JSON QUERY OBJECT FORMAT
{
"spatial": {
  "topLeft": [70.0,5.9],
  "bottomRight": [58.8,-42.9]
},
"temporal": {
  "start": null,
  "end": null
},
"idOnly": false,
"keywords": [],
"topicIds": [],
"sources": []
}
*/

var Search = {
  options: {
    source: "http://beta.data-arc.org/api/search",
    delay: 100,
    before: function(){},
    after: function(){}
  },
  init: function(options) {
    this.defaults = {
      "spatial": {
        "topLeft": [-75, 85],
        "bottomRight": [-9.5, 62.5]
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
      Search.query(Search.options.source, {"spatial":Search.defaults.spatial}, Search.analyzeFirst);
    else
      Search.query(Search.options.source, Search.values, Search.analyze);
  },
  analyzeFirst: function(error, data) {
    if (error) throw error;

    // Save all the data
    Search.all.features = data.results.features;
    Search.all.facets = data.facets;
    console.log('Loaded all ' + Search.all.features.length + ' features.');
    // console.log(Search.all.features);

    // Once all data is loaded, fire of the results query
    Search.query(Search.options.source, Search.values, Search.analyze);
  },
  analyze: function(error, data) {
    if (error) throw error;

    // Save the results
    Search.results = (data.results ? data.results : {});
    Search.facets = (data.facets ? data.facets : {});
    console.log('Loaded results containing ' + Search.results.length + ' features.');
    console.log(Search.values);

    // Proceed to format data as needed here
    Search.options.after();
  },
  query: function(source, filters, callback) {
      //FIXME: should be sending application/JSON not text/plain
    d3.json(source).header("Content-Type", "application/json;charset=UTF-8").post(JSON.stringify(filters), callback);
  },


  // ****************************************************
  // Specific runctions to return a subset of results
  // ****************************************************

  // get results by id or array of ids
  getResultsById: function(id) {
    if (typeof id === 'string')
      return Search.all.features.filter(feature => feature.properties.id === id);
    if (Object.prototype.toString.call(id) === '[object Array]')
      return Search.all.features.filter(feature => id.includes(feature.properties.id));
  },

  // get results by a source or sources
  getResultsBySource: function(source) {
    if (typeof source === 'string')
      return Search.all.features.filter(feature => feature.properties.source === source);
    if (Object.prototype.toString.call(source) === '[object Array]')
      return Search.all.features.filter(feature => source.includes(feature.properties.source));
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