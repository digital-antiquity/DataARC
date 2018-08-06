'use strict';

// Global variables
var Loader, Filters, Timeline, Geography, Concepts, Results, Search;
var category_colors = ["#6177aa", "#fc8d62", "#66c2a5", "#54278f", "#a63603"];

// Page Level Javascript Actions
$(function() {
  // config for the objects
  var search_config = {
    source: "/api/search",
    recordSource: "/api/getId",
    delay: 100, // delay before search checks in ms
    before: function() { // actions to run before search query begins
      Loader.show();
      Filters.wait();
      Timeline.wait();
      Geography.wait();
      Results.wait();
      $('#filters-count').text(0);
      $('#results-count').text(0);
      console.log('Searching');
    },
    after: function() { // actions to run after search query is finished
      console.log('Filters');
      Filters.refresh();
      console.log('Timeline');
      Timeline.refresh();
      console.log('Geography');
      Geography.refresh();
      console.log('Concepts');
      Concepts.refresh();
      console.log('Results');
      Results.refresh();
      console.log('Counts');
      $('#filters-count').text(Filters.count.toLocaleString());
      $('#results-count').text(Search.results['matched'].count.toLocaleString());
      console.log('Done');

      // Enable tooltips
      $('[data-toggle="tooltip"]').tooltip();

      // Enable category tooltip details
      $('.typetip').hover((e) => {
        var details = {
          archaeological: 'Our archaeological data primarily include excavated and analyzed faunal data from across the North Atlantic. We plan to add other archaeological data sources in the future.',
          environmental: 'Our environmental data include data from paleoenvironmental samples from the Strategic Environmental Archaeology Database and geological tephras from Tephrabase.',
          textual: 'Our textual data include geolocated place names and places mentioned in the Icelandic sagas, and a variety of historic documents on Icelandic farms.'
        };
        var mark = $(e.target);
        var type = mark.text().toLowerCase();
        mark.tooltip({ title: details[type], trigger: 'manual' }).tooltip('show');
      }, (e) => {
        var mark = $(e.target);
        mark.tooltip('hide');
      });

      Loader.hide();
      
    }
  };
  var topic_config = {
    container: 'topicmap',
    dataUrl: '/api/topicmap/view',
    gravity: -200,
    minRadius: 5,
    maxRadius: 15,
    searchContainer: 'topicSearch'
  }

  // set testing variables
  if (testing) {
    var type = 'api'; // api or cache
    search_config.source = 'dev/' + type + '_search.php';
    search_config.recordSource = 'dev/' + type + '_getId.php';
    topic_config.dataUrl = 'dev/' + type + '_topicmap.php';
  }

  // Define the objects, loader first
  Loader = new LoaderHandler();
  Filters = new FilterHandler();
  Timeline = new TimelineObject("millennium", -7000);
  Geography = new GeographyHandler();
  Concepts = new TopicMap(topic_config);
  Results = new ResultsHandler();

  $(window).on('doneConceptInit', function(e) {
      console.log("conceptInit");
      Search = new SearchObject(search_config);
  });
  
  // Smooth scrolling using jQuery easing
  $('a.js-scroll-trigger[href*="#"]:not([href="#"])').click(function() {
    if (location.pathname.replace(/^\//, '') == this.pathname.replace(/^\//, '') && location.hostname == this.hostname) {
      var target = $(this.hash);
      target = target.length ? target : $('[name=' + this.hash.slice(1) + ']');
      if (target.length) {
        $('html, body').animate({
          scrollTop: (target.offset().top - 48)
        }, 1000, "easeInOutExpo");
        return false;
      }
    }
  });

  // Closes responsive menu when a scroll trigger link is clicked
  $('.js-scroll-trigger').click(function() {
    $('.navbar-collapse').collapse('hide');
  });

  // Handlebars helper
  Handlebars.registerHelper("fieldName", function(name) {
    if (name != undefined) {
      if (FIELDS[name.trim()] != undefined) {
        return FIELDS[name.trim()];
      }
      return name;
    }
    return "";
  });

  // Update keywords on button click
  $('#keywords-btn').click(function() {
    Search.set("keywords", $('#keywords-field').val());
    $('#keywords-field').val(null);
  });

  // Update keywords on enter key
  $('#keywords-field').keypress(function(e) {
    var key = e.which;
    if (key == 13) { // the enter key code
      $('#keywords-btn').click();
      return false;
    }
  });
});