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
      // "spatial": {
      //   "topLeft": [-75, 85],
      //   "bottomRight": [-0.1, 58]
      // },
      "spatial": {
        "topLeft": null,
        "bottomRight": null
      },
      "topicIds": [],
      "sources": [],
      "ids": [],
      "idOnly": true
      // "idAndMap": false
    };
    this.values = $.extend({}, this.defaults);
    this.results = {
      "all": {},
      "matched": {},
      "related": {},
      "contextual": {},
      "details": {}
    };
    this.previous = null;
    this.revision = Date.now();
    this.query_count = 0;
    this.errors = 0;
    this.config = config;

    // initialize the search object
    this.refresh(true);

    // set an interval to check on query changes
    setInterval(() => {
      this.refresh(false);
    }, this.config.delay);
  }

  refresh(initial) {
    // exit if the search parameters have not been revised
    if (this.previous == this.revision) return;

    // sync the revision information
    this.previous = this.revision

    // perform any before search actions
    this.config.before();

    // if first run then get all data before loading results
    if (initial) {
      var filters_all = { "spatial": this.defaults.spatial, "idOnly": false };
      $.when(this.fetch(filters_all, this.config.source)).then((all) => {
        this.analyze('all', all, filters_all);
        this.analyze('matched', all, filters_all);
        this.config.after();

        // for testing purposes only
        // console.log('Setting changed for query testing on load.');
        // this.set('spatial', {"topLeft": [-23.62, 66.02], "bottomRight": [-18.22, 64.19]});
      });
    }
    // run the search
    else {
      var filters_matched = this.values;
      var filters_related = _.assign({}, this.values, { "expandBy": 2 });
      var filters_contextual = _.assign({}, this.values, { "expandBy": 3 });
      $.when(
        this.fetch(filters_matched, this.config.source),
        this.fetch(filters_related, this.config.source),
        this.fetch(filters_contextual, this.config.source)
      ).then((matched, related, contextual) => {
        this.analyze('matched', matched, filters_matched);
        this.analyze('related', related, filters_related);
        this.analyze('contextual', contextual, filters_contextual);
        this.config.after();
      }).fail(function(xhr1, xhr2, xhr3) {
        console.log('Matched Failed', xhr1);
        console.log('Related Failed', xhr2);
        console.log('Contextual Failed', xhr3);
      });
    }
  }

  analyze(type, data, filters) {
    // set results empty
    this.results[type].data = data;
    this.results[type].filters = filters;
    this.results[type].revision = this.revision;
    this.results[type].ids = [];
    this.results[type].features = [];
    this.results[type].facets = {};
    this.results[type].count = 0;

    // make sure data is not null or undefined
    if (data == null) return;

    // make sure we have results
    if (Array.isArray(data) && data.length > 0) {
      this.results[type].data = data[0];
      data.idList = data[0].idList;
      data.results = data[0].results;
      data.facets = data[0].facets;
    }
    if (data.idList == null) return;
    if (data.results == null) return;
    if (data.facets == null) return;

    // Save the results
    this.results[type].ids = (data.idList ? data.idList : []);
    this.results[type].features = (data.results.features && type == 'all' ? data.results.features : []);
    this.results[type].facets = (data.facets ? data.facets : {});
    this.results[type].count = this.results[type].ids.length;
    console.log('Loaded ' + type + ' results containing ' + this.results[type].count + ' features.', this.results[type]);
  }

  query(filters, callback) {
    this.query_count++;
    d3.json(this.config.source).header("Content-Type", "application/json;charset=UTF-8").post(JSON.stringify(filters), callback);
  }

  // Generic function to make an AJAX call
  fetch(filters, url, success, failure) {
    return $.ajax({
      type: "POST",
      url: url,
      crossDomain: true,
      async: true,
      dataType: "json",
      contentType: "application/json; charset=utf-8",
      data: JSON.stringify(filters),
      success: function (data) { console.log('Fetch Successful'); if (success != null) success(); },
      failure: function (error) { console.log('Fetch Failed'); if (failure != null) failure(); }
    });
  }

  changed() {
    this.previous = this.revision;
    this.revision = Date.now();
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

  // ****************************************************
  // Specific runctions to return a subset of results
  // ****************************************************

  // get detail information for a specific id
  getDetailsById(id, callback) {
    // d3.json(this.config.recordSource+'?id='+id).header("Content-Type", "application/json;charset=UTF-8").get(callback);
    $.when(this.fetch(null, this.config.recordSource+'?id='+id)).then(callback)
    .fail(function(xhr) {
      console.log('Fetch details failed', xhr);
    });
  }

  // get results by id or array of ids
  getResultsById(ids, callback, local) {
    if (typeof ids === 'string') {
      ids = [ids];
    }
    if (local) {
      return this.results['all'].features.filter(feature => ids.includes(feature.properties.id));
    }
    else {
      // this.query({ ids: ids }, callback);
      $.when(this.fetch({ ids: ids }, this.config.source)).then(callback)
      .fail(function(xhr) {
        console.log('Fetch results by id failed', xhr);
      });
    }
  }

  // get all results by source or sources
  getResultsBySource(type, sources) {
    if (typeof sources === 'string') {
      sources = [sources];
    }
    return this.results['all'].features.filter(feature => sources.includes(feature.properties.source));
  }

  // get results by type and a source or sources
  getResultsByTypeSource(type, sources) {
    if (typeof sources === 'string') {
      sources = [sources];
    }
    var features = this.getResultsBySource(type, sources);
    return features.filter(feature => this.results[type].ids.includes(feature.properties.id));
  }

  // get results by keyword
  getResultsByKeyword(keyword) {
    return this.results['all'].features.filter(feature => feature.properties.keywords.indexOf(keyword) > -1);
  }

  // get results by decade
  getResultsByDecade(decade) {
    return this.results['all'].features.filter(feature => feature.properties.decade.includes(decade));
  }

  // get results by millenium
  getResultsByMillenium(millenium) {
    return this.results['all'].features.filter(feature => feature.properties.millenium.includes(millenium));
  }

  // get results by century
  getResultsByCentury(century) {
    return this.results['all'].features.filter(feature => feature.properties.century.includes(century));
  }

  // get results by bounding box a,b = lat,lng and c,d = lat,lng
  getResultsByBounds(a, b, c, d) {
    var bounds = L.latLngBounds(L.latLng(a, b), L.latLng(c, d));
    return this.results['all'].features.filter(feature => bounds.contains(L.latLng(feature.geometry.coordinates[1], feature.geometry.coordinates[0])));
  }

  // get results by region
  getResultsByPolygon(file_id, polygon_id) {
    return this.results['all'].features.filter(feature => feature.region === file_id + '_____' + polygon_id);
  }

  // get results within specific category
  getResultsByCategory(category) {
    return this.results['all'].features.filter(feature => feature.properties.category.toLowerCase() == category.toLowerCase() && this.results['all'].features.indexOf(feature.properties.id) > -1);
  }

  // get an array of ids from an array of results
  getIdsFromResults(results) {
    return results.map(feature => feature.properties.id);
  }

}
