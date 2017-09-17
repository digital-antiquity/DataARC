// requires search.js

var Geography = {
  init: function() {
    this.layer = null
    this.values = {};
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
  },
  unset: function(key) {
    delete this.values[key];
  },
  refresh: function() {
    this.addFeatures(Search.results, {
      radius: 5,
      fillColor: "#eee",
      color: "#000",
      weight: 1,
      opacity: 0.4,
      fillOpacity: 0.1
    });
  },
  bindPopup: function(feature, layer) {
    var popup = "<p>Some content.</p>";
    if (feature.properties && feature.properties.popup) {
      popup += feature.properties.popup;
    }
    layer.bindPopup(popup);
  },
  addFeatures: function(geojson, style) {
    this.clear();
    this.layer = L.geoJSON(geojson, {
      style: function(feature) {
        return feature.properties && feature.properties.style;
      },
      // onEachFeature: Geography.bindPopup,
      pointToLayer: function(feature, latlng) {
        return L.circleMarker(latlng, style);
      }
    }).addTo(map)
  },
  clear: function(key) {
    if (this.layer) {
      map.removeLayer(this.layer);
      this.layer = null;
    }
  },
};
Geography.init();