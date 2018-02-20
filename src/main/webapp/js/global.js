'use strict';

// Global variables
var Loader, Filters, Timeline, Geography, Concepts, Results, Search;
var category_colors = ["#6177aa", "#fc8d62", "#66c2a5", "#54278f", "#a63603"];

// Page Level Javascript Actions
$(document).ready(function() {
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
  Search = new SearchObject(search_config);

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

  // Filter the timeline
  $('#filter-timeline-apply').click(Timeline.applyFilter);
});