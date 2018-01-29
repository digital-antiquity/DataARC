// Requires ECMA6, Lodash, jQuery

class SearchObject {

  constructor(config) {
    this.defaults = {
      "keywords": [],
      "temporal": {
        "start": null,
        "end": null,
        "period": null
      },
      "spatial": {
        "topLeft": [-75, 85],
        "bottomRight": [-0.1, 58],
        "region": null
      },
      "topicIds": [],
      "sources": [],
      "ids": [],
      "idOnly": true,
      "idAndMap": false
    };
    this.values = $.extend({}, this.defaults);
    this.all = {};
    this.results = {};
    this.facets = {};
    this.previous = null;
    this.revision = Date.now();
    this.query_count = 0;
    this.errors = 0;
    this.config = config;

    // Set an interval to check on query changes
    setInterval(() => {
      this.refresh();
    }, this.config.delay);
  }

  get(key) {
    var value;
    if (this.values.hasOwnProperty(key)) {
      value = this.values[key];
    }
    return value;
  }

  set(key, value) {
    // set the current value before overwriting it
    var previous_value = (this.values[key] ? this.values[key] : null);

    // set our value
    if (value == null) {
      this.values[key] = this.defaults[key];
    }
    else {
      if (Array.isArray(this.values[key])) {
        this.values[key] = [...new Set([].concat(...[this.values[key], [value]]))]; // _.union(this.values[key], [value]);
      }
      else {
        this.values[key] = value;
      }
    }

    // check to see if anything changed
    var changed = false;
    if (Array.isArray(this.values[key])) {
      changed = (_.difference(this.values[key], previous_value).length > 0);
    }
    else if (typeof this.values[key] === 'object') {
      changed = !(_.isEqual(this.values[key], previous_value));
    }
    else {
      changed = (previous_value != this.values[key]);
    }
    if (changed) {
      this.changed();
    }
  }

  unset(key, value) {
    if (this.values[key] && value != null) {
      if (Array.isArray(this.values[key])) {
        _.pull(this.values[key], "" + value);
        this.changed();
      }
    }
    else {
      this.set(key, null)
    }
  }

  changed() {
    this.previous = this.revision;
    this.revision = Date.now();
  }

  refresh() {
    // Exit if the search parameters have not been revised
    if (this.previous == this.revision) return;

    // Perform any before search actions
    this.config.before();

    // Sync the revision information and run the search
    this.previous = this.revision;

    // If first run then get all data before loading results
    if (this.all.features == null) {
      this.query({ "spatial": this.defaults.spatial, "idOnly": false }, (error, data) => { this.analyzeFirst(error, data); });
    }
    else {
      this.query(this.values, (error, data) => { this.analyze(error, data); });
    }
  }

  analyzeFirst(error, data) {
    if (error) throw error;

    // Save all the data
    this.all.features = data.results.features;
    this.all.facets = data.facets;
    console.log('Loaded all ' + this.all.features.length + ' features.');

    // Once all data is loaded, fire of the results query
    this.analyze(false, data);
  }

  analyze(error, data) {
    this.error = (error);
    if (this.error) {
      // Set results empty
      this.results = [];
      this.facets = {};
      console.log(error);
    } else {
      // Save the results
      this.results = (data.idList ? data.idList : []);
      this.facets = (data.facets ? data.facets : {});
      console.log('Loaded results containing ' + this.results.length + ' features.');
    }
    // Proceed to format data as needed here
    this.config.after();
  }

  query(filters, callback) {
    this.query_count++;
    d3.json(this.config.source).header("Content-Type", "application/json;charset=UTF-8").post(JSON.stringify(filters), callback);
  }

  // ****************************************************
  // Specific runctions to return a subset of results
  // ****************************************************

  // get detail information for a specific id
  getDetailsById(id, callback) {
    d3.json(this.config.recordSource+'?id='+id).header("Content-Type", "application/json;charset=UTF-8").get(callback);
  }

  // get results by id or array of ids
  getResultsById(ids, callback, local) {
    if (typeof ids === 'string')
      ids = [ids];
    if (local)
      return this.all.features.filter(feature => id.includes(feature.properties.id));
    else
      this.query({ ids: ids }, callback);
  }

  // get results by a source or sources
  getResultsBySource(sources) {
    if (typeof source === 'string')
      sources = [sources];
    return this.all.features.filter(feature => sources.includes(feature.properties.source));
  }

  // get results by keyword
  getResultsByKeyword(keyword) {
    return this.all.features.filter(feature => feature.properties.keywords.indexOf(keyword) > -1);
  }

  // get results by decade
  getResultsByDecade(decade) {
    return this.all.features.filter(feature => feature.properties.decade.includes(decade));
  }

  // get results by millenium
  getResultsByMillenium(millenium) {
    return this.all.features.filter(feature => feature.properties.millenium.includes(millenium));
  }

  // get results by century
  getResultsByCentury(century) {
    return this.all.features.filter(feature => feature.properties.century.includes(century));
  }

  // get results by bounding box a,b = lat,lng and c,d = lat,lng
  getResultsByBounds(a, b, c, d) {
    var bounds = L.latLngBounds(L.latLng(a, b), L.latLng(c, d));
    return this.all.features.filter(feature => bounds.contains(L.latLng(feature.geometry.coordinates[1], feature.geometry.coordinates[0])));
  }

  // get results by region
  getResultsByPolygon(file_id, polygon_id) {
    return this.all.features.filter(feature => feature.region === file_id + '_____' + polygon_id);
  }

  // get results within specific category
  getResultsByCategory(category) {
    return this.all.features.filter(feature => feature.properties.category.toLowerCase() == category.toLowerCase() && this.results.indexOf(feature.properties.id) > -1);
  }
}
