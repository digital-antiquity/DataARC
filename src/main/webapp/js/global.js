// Global variables
var Search;
var category_colors = ["#6177aa", "#fc8d62", "#66c2a5", "#54278f", "#a63603"];

// Page Level Javascript Actions
$(document).ready(function() {
  // config for the objects
  var config = {
    source: "/api/search",
    recordSource: "/api/getId",
    delay: 100, // delay before search checks in ms
    before: function() { // actions to run before search query begins
      Loader.show();
      Timeline.wait();
      Geography.wait();
      Filters.wait();
      Results.wait();
      $('#filters-count').text(0);
      $('#results-count').text(0);
      console.log('Searching');
    },
    after: function() { // actions to run after search query is finished
      console.log('Timeline');
      Timeline.refresh();
      console.log('Geography');
      Geography.refresh();
      console.log('Concepts');
      Concepts.refresh();
      console.log('Filters');
      Filters.refresh();
      console.log('Results');
      Results.refresh();
      console.log('Counts');
      $('#filters-count').text(Filters.count.toLocaleString());
      $('#results-count').text(Search.results['matched'].count.toLocaleString());
      console.log('Done');
      Loader.hide();
    }
  };
  if (testing) {
    var type = 'api'; // api or cache
    config.source = 'dev/'+type+"_search.php";
    config.recordSource = 'dev/'+type+"_getId.php";
  }

  // Define the objects
  Search = new SearchObject(config);

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