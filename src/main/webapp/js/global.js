// Global variables
var Search, Filter, Timeline;
var category_colors = ["#6177AA", "#fc8d62", "#66c2a5", "#54278f", "#a63603"];

// Page Level Javascript Actions
$(document).ready(function() {

  // Setup and init the search object
  var config = {
    source: "/api/search",
    recordSource: "/api/getId",
    delay: 100, // delay before search checks in ms
    before: function() { // actions to run before search query begins
      Loading.show();
      Timeline.wait();
      Geography.wait();
      Filter.wait();
      $('#results-count').html('<i class="fa fa-spinner text-white fa-spin"></i>');
    },
    after: function() { // actions to run after search query is finished
      Timeline.refresh(Search.facets.temporal);
      Geography.refresh();
      Concepts.refresh();
      Filter.refresh(Search.values);
      ResultsHandler = new Results('#results');
      $('#results-count').empty().text(Search.results.length);
      Loading.hide();
    }
  };
  if (testing) {
    var type = 'api'; // api or cache
    config.source = 'dev/'+type+"_search.php";
    config.recordSource = 'dev/'+type+"_getId.php";
  }

  // Define the objects
  Search = new SearchObject(config);
  Filter = new FilterObject();
  Timeline = new TimelineObject("millennium", -7000);

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

  // Activate scrollspy to add active class to navbar items on scroll
  $('body').scrollspy({
    target: '#mainNav',
    offset: 48
  });

  // Scroll reveal calls
  window.sr = ScrollReveal();
  sr.reveal('.sr-icons', {
    duration: 600,
    scale: 0.3,
    distance: '0px'
  }, 200);
  sr.reveal('.sr-button', {
    duration: 1000,
    delay: 200
  });
  sr.reveal('.sr-contact', {
    duration: 600,
    scale: 0.3,
    distance: '0px'
  }, 300);

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


/* ******* */
/* LOADING */
/* ******* */
var Loading = Loading || (function() {
  return {
    show: function(_callback) {
      $('.loading-pg').fadeIn(_callback);
    },
    hide: function(_callback) {
      $('.loading-pg').fadeOut(_callback);
    },
    showSmall: function(element) {
      $(element).append('<div class="loading loading-sm pull-right"><ul><li></li><li></li><li></li><li></li><li></li><li></li></ul></div>');
    },
    hideSmall: function(element) {
      $(element).child('.loading-sm').remove();
    }
  };
})();