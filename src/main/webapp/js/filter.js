var FilterObject = function() {
  this.settings = {
    container: '#filter',
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
  },

  createElements: function() {

  },

};

var Filter = new FilterObject();