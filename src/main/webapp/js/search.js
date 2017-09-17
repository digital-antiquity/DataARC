/* EXAMPLE JSON QUERY OBJECT FORMAT
{
"spatial": {
  "topLeft": [null,null],
  "bottomRight": [null,null]
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
    callback: function(){}
  },
  init: function(options) {
    this.values = {};
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
    this.values[key] = value;
    this.previous = this.revision;
    this.revision = Date.now();
  },
  unset: function(key) {
    delete this.values[key];
  },
  refresh: function() {
    // Exit if the search parameters have not been revised
    if (this.previous == this.revision) return;

    // Sync the revision information and run the search
    this.previous = this.revision;

    // If first run then set all data
    if (Object.keys(Search.all).length === 0 && Search.all.constructor === Object)
      d3.queue().defer(d3.json, Search.options.source).await(Search.loadAll)

    // Load the data from the
    d3.queue().defer(d3.json, Search.options.source).await(Search.loadResults)

    // http://beta.data-arc.org/api/search
    // var request = Search.request("post", "http://beta.data-arc.org/api/search");
    // if (request) {
    //   console.log('request successful');
    //   // Define a callback function
    //   request.onload = function(){console.log('loaded');};
    //   // Send request
    //   request.send(Search.values);
    // }
    //
    // $.post("http://beta.data-arc.org/api/search",
    // {"keywords":["test"]},
    // function( data ){
    //   console.log( data );
    // });
    // $.ajax({
    //   url: "proxy.php?csurl=http://beta.data-arc.org/api/search",
    //   type: "post",
    //   data: { "keywords": ["test"] },
    //   // dataType: "json",
    //   // contentType: "application/json; charset=utf-8",
    //   success: function(resp) {
    //     console.log(resp);
    //   },
    //   error: function(resp) {
    //     console.log(resp);
    //   }
    // });
    //
    // d3.request("http://beta.data-arc.org/api/search")
    //   // .header("X-Requested-With", "XMLHttpRequest")
    //   .header("Content-Type", "application/json")
    //   .header("Access-Control-Allow-Origin", "http://dataarcdev.cast.uark.edu")
    //   .post(this.values, Response.analyze);
  },
  // request: function(method, url) {
  //   var xhr = new XMLHttpRequest();
  //   if ("withCredentials" in xhr) {
  //     // XHR has 'withCredentials' property only if it supports CORS
  //     console.log('here withcreds');
  //     xhr.open(method, url, true);
  //   } else if (typeof XDomainRequest != "undefined") { // if IE use XDR
  //     console.log("xdr");
  //     xhr = new XDomainRequest();
  //     xhr.open(method, url);
  //   } else {
  //     xhr = null;
  //   }
  //   return xhr;
  // },
  loadAll: function(error, data) {
    if (error) {
      this.errors++;
      this.previous = null;
      console.log(error);
      return;
    }
    Search.all = data.results;
    console.log('Loaded all ' + Search.results.length + ' features.');
  },
  loadResults: function(error, data) {
    if (error) {
      this.errors++;
      this.previous = null;
      console.log(error);
      return;
    }

    // Save the new data
    Search.results = data.results.features;
    Search.facets = data.facets;
    console.log('Loaded results containing ' + Search.results.length + ' features.');

    // Proceed to format data as needed here
    Search.options.callback();


    // SOME EXAMPLE SEARCH QUERIES
    // var example = Search.getResultsByKeyword(Search.values.keywords[0]);
    // console.log('KEYWORD', 'Found ' + example.length + ' features.', example);

    // var example = Search.getResultsByDecade('1050');
    // console.log('DECADE', 'Found ' + example.length + ' features.', example);

    // var example = Search.getResultsByBounds(64, -20, 60, -10); // lat1, lng1, lat2, lng2
    // console.log('BOUNDS', 'Found ' + example.length + ' features.', example);


  },


  // ****************************************************
  // Specific runctions to return a subset of results
  // ****************************************************

  // get results by id or array of ids
  getResultsById: function(id) {
    if (typeof id === 'string')
      return Search.results.filter(feature => feature.properties.id === id);
    if (Object.prototype.toString.call(id) === '[object Array]')
      return Search.results.filter(feature => id.includes(feature.properties.id));
  },

  // get results by a source or sources
  getResultsBySource: function(source) {
    if (typeof source === 'string')
      return Search.results.filter(feature => feature.properties.source === source);
    if (Object.prototype.toString.call(source) === '[object Array]')
      return Search.results.filter(feature => source.includes(feature.properties.source));
  },

  // get results by keyword
  getResultsByKeyword: function(keyword) {
    return Search.results.filter(feature => feature.properties.keywords.indexOf(keyword) > -1);
  },

  // get results by decade
  getResultsByDecade: function(decade) {
    return Search.results.filter(feature => feature.properties.decade.includes(decade));
  },

  // get results by millenium
  getResultsByMillenium: function(millenium) {
    return Search.results.filter(feature => feature.properties.millenium.includes(millenium));
  },

  // get results by century
  getResultsByCentury: function(century) {
    return Search.results.filter(feature => feature.properties.century.includes(century));
  },

  // get results by bounding box a,b = lat,lng and c,d = lat,lng
  getResultsByBounds: function(a, b, c, d) {
    var bounds = L.latLngBounds(L.latLng(a, b), L.latLng(c, d));
    return Search.results.filter(feature => bounds.contains(L.latLng(feature.geometry.coordinates[1], feature.geometry.coordinates[0])));
  },

  // get results within specific category
  getResultsByCategory: function(category) {

  },
};